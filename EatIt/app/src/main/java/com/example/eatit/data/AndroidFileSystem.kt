package com.example.eatit.data

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.eatit.utilities.toOkioPath
import com.example.eatit.utilities.toUri
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.FileHandle
import okio.FileMetadata
import okio.FileNotFoundException
import okio.FileSystem
import okio.Path
import okio.Sink
import okio.Source
import okio.sink
import okio.source
import java.io.File
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume

class AndroidFileSystem(private val context: Context) : FileSystem() {
    private val contentResolver = context.contentResolver

    private fun isPhysicalFile(file: Path): Boolean {
        return file.toString().first() == '/'
    }

    private fun requireFileExist(file: File) {
        if (!file.exists()) throw IOException("$this doesn't exist.")
    }

    private fun requireFileCreate(file: File) {
        if (file.exists()) throw IOException("$this already exists.")
    }

    override fun appendingSink(file: Path, mustExist: Boolean): Sink {
        if (isPhysicalFile(file)) {
            val target = file.toFile()
            if (mustExist) requireFileExist(target)

            return target.sink(append = true)
        }

        if (!mustExist) {
            throw IOException("Appending on an inexisting path isn't supported ($file)")
        }

        val uri = file.toUri()
        val outputStream = contentResolver.openOutputStream(uri, "a")

        if (outputStream == null) {
            throw IOException("Couldn't open an OutputStream ($file)")
        } else {
            return outputStream.sink()
        }
    }

    /**
     * Not yet implemented
     */
    override fun atomicMove(source: Path, target: Path) {
        TODO("Not yet implemented")
    }

    override fun canonicalize(path: Path): Path {
        throw UnsupportedOperationException("Paths can't be canonicalized in AndroidFileSystem")
    }

    /**
     * Not yet implemented
     */
    override fun createDirectory(dir: Path, mustCreate: Boolean) {
        TODO("Not yet implemented")
    }

    override fun createSymlink(source: Path, target: Path) {
        throw UnsupportedOperationException("Symlinks  can't be created in AndroidFileSystem")
    }

    override fun delete(path: Path, mustExist: Boolean) {
        if (isPhysicalFile(path)) {
            val file = path.toFile()
            val deleted = file.delete()
            if (!deleted) {
                if (!file.exists()) throw FileNotFoundException("no such file: $path")
                else throw IOException("failed to delete $path")
            }
        } else {
            val uri = path.toUri()
            val deletedRows = contentResolver.delete(uri, null, null)

            if (deletedRows == 0) {
                throw IOException("failed to delete $path")
            }
        }
    }

    override fun list(dir: Path): List<Path> = list(dir, throwOnFailure = true)!!

    override fun listOrNull(dir: Path): List<Path>? = list(dir, throwOnFailure = false)

    private fun list(dir: Path, throwOnFailure: Boolean): List<Path>? {
        if (isPhysicalFile(dir)) {
            return listPhysicalDirectory(dir, throwOnFailure)
        }

        return listDocumentProvider(dir, throwOnFailure)
    }

    private fun listPhysicalDirectory(dir: Path, throwOnFailure: Boolean): List<Path>? {
        val file = dir.toFile()
        val entries = file.list()
        if (entries == null) {
            if (throwOnFailure) {
                if (!file.exists()) throw FileNotFoundException("no such file: $dir")
                throw IOException("failed to list $dir")
            } else {
                return null
            }
        }
        val result = entries.mapTo(mutableListOf()) { dir / it }
        result.sort()
        return result
    }

    private fun listDocumentProvider(dir: Path, throwOnFailure: Boolean): List<Path>? {
        // TODO: Verify path is a directory
        val rootUri = dir.toUri()
        val documentId = DocumentsContract.getDocumentId(rootUri)
        val treeUri = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, documentId)

        val cursor = contentResolver.query(
            treeUri,
            arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID),
            null,
            null,
            null,
            null
        )

        if (cursor == null) {
            if (throwOnFailure) {
                throw IOException("failed to list $dir")
            } else {
                return null
            }
        }

        val result = mutableListOf<Path>()

        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                result.add(
                    DocumentsContract.buildDocumentUriUsingTree(rootUri, documentId).toOkioPath()
                )
            }
        }

        return result
    }

    override fun metadataOrNull(path: Path): FileMetadata? {
        val uri = path.toUri()

        return when (uri.authority) {
            null -> fetchMetadataFromPhysicalFile(path)
            MediaStore.AUTHORITY -> fetchMetadataFromMediaStore(path, uri)
            else -> fetchMetadataFromDocumentProvider(path, uri)
        }
    }

    private fun fetchMetadataFromPhysicalFile(path: Path): FileMetadata? {
        val file = path.toFile()
        val isRegularFile = file.isFile
        val isDirectory = file.isDirectory
        val lastModifiedAtMillis = file.lastModified()
        val size = file.length()

        if (!isRegularFile &&
            !isDirectory &&
            lastModifiedAtMillis == 0L &&
            size == 0L &&
            !file.exists()
        ) {
            return null
        }

        val fileExtension: String = MimeTypeMap.getFileExtensionFromUrl(file.toString())
        val mimeType = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(fileExtension.lowercase(Locale.getDefault()))

        val extras = mutableMapOf(
            Path::class to path,
            MetadataExtras.DisplayName::class to MetadataExtras.DisplayName(file.name),
            MetadataExtras.FilePath::class to MetadataExtras.FilePath(file.absolutePath),
        )

        if (mimeType != null) extras[MetadataExtras.MimeType::class] =
            MetadataExtras.MimeType(mimeType)

        return FileMetadata(
            isRegularFile = isRegularFile,
            isDirectory = isDirectory,
            symlinkTarget = null,
            size = size,
            createdAtMillis = null,
            lastModifiedAtMillis = lastModifiedAtMillis,
            lastAccessedAtMillis = null,
            extras = extras
        )
    }

    private fun fetchMetadataFromMediaStore(path: Path, uri: Uri): FileMetadata? {
        if (uri.pathSegments.firstOrNull().isNullOrBlank()) {
            return null
        }

        val isPhotoPickerUri = uri.pathSegments.firstOrNull() == "picker"

        val projection = if (isPhotoPickerUri) {
            arrayOf(
                MediaStore.MediaColumns.DATE_TAKEN,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATA,
            )
        } else {
            arrayOf(
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATA,
            )
        }

        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        ) ?: return null

        cursor.use { cursor ->
            if (!cursor.moveToNext()) {
                return null
            }

            val createdTime: Long
            var lastModifiedTime: Long? = null

            if (isPhotoPickerUri) {
                createdTime =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN))
            } else {
                createdTime =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED))
                lastModifiedTime =
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
            }

            val displayName =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
            val mimeType =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
            val filePath =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))

            return FileMetadata(
                isRegularFile = true,
                isDirectory = false,
                symlinkTarget = null,
                size = size,
                createdAtMillis = createdTime,
                lastModifiedAtMillis = lastModifiedTime,
                lastAccessedAtMillis = null,
                extras = mapOf(
                    Path::class to path,
                    Uri::class to uri,
                    MetadataExtras.DisplayName::class to MetadataExtras.DisplayName(displayName),
                    MetadataExtras.MimeType::class to MetadataExtras.MimeType(mimeType),
                    MetadataExtras.FilePath::class to MetadataExtras.FilePath(filePath),
                )
            )
        }
    }

    private fun fetchMetadataFromDocumentProvider(path: Path, uri: Uri): FileMetadata? {
        val cursor = contentResolver.query(
            uri,
            arrayOf(
                DocumentsContract.Document.COLUMN_LAST_MODIFIED,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE
            ),
            null,
            null,
            null
        ) ?: return null

        cursor.use { cursor ->
            if (!cursor.moveToNext()) {
                return null
            }

            // DocumentsContract.Document.COLUMN_LAST_MODIFIED
            val lastModifiedTime = cursor.getLong(0)
            // DocumentsContract.Document.COLUMN_DISPLAY_NAME
            val displayName = cursor.getString(1)
            // DocumentsContract.Document.COLUMN_MIME_TYPE
            val mimeType = cursor.getString(2)
            // DocumentsContract.Document.COLUMN_SIZE
            val size = cursor.getLong(3)

            val isFolder = mimeType == DocumentsContract.Document.MIME_TYPE_DIR ||
                    mimeType == DocumentsContract.Root.MIME_TYPE_ITEM

            return FileMetadata(
                isRegularFile = !isFolder,
                isDirectory = isFolder,
                symlinkTarget = null,
                size = size,
                createdAtMillis = null,
                lastModifiedAtMillis = lastModifiedTime,
                lastAccessedAtMillis = null,
                extras = mapOf(
                    Path::class to path,
                    Uri::class to uri,
                    MetadataExtras.DisplayName::class to MetadataExtras.DisplayName(displayName),
                    MetadataExtras.MimeType::class to MetadataExtras.MimeType(mimeType),
                )
            )
        }
    }

    /**
     * Not yet implemented
     */
    override fun openReadOnly(file: Path): FileHandle {
        TODO("Not yet implemented")
    }

    /**
     * Not yet implemented
     */
    override fun openReadWrite(file: Path, mustCreate: Boolean, mustExist: Boolean): FileHandle {
        TODO("Not yet implemented")
    }

    override fun sink(file: Path, mustCreate: Boolean): Sink {
        if (isPhysicalFile(file)) {
            val target = file.toFile()
            if (mustCreate) requireFileCreate(target)

            return file.toFile().sink()
        }

        if (mustCreate) {
            throw IOException("Path creation isn't supported ($file)")
        }

        val uri = file.toUri()
        val outputStream = contentResolver.openOutputStream(uri)

        if (outputStream == null) {
            throw IOException("Couldn't open an OutputStream ($file)")
        } else {
            return outputStream.sink()
        }
    }

    override fun source(file: Path): Source {
        if (isPhysicalFile(file)) {
            return file.toFile().source()
        }

        val uri = file.toUri()
        val inputStream = contentResolver.openInputStream(uri)

        if (inputStream == null) {
            throw IOException("Couldn't open an InputStream ($file)")
        } else {
            return inputStream.source()
        }
    }

    @Deprecated(
        "Use the updated createMediaStoreUri() method",
        ReplaceWith("createMediaStoreUri(filename, collection, directory)"),
        DeprecationLevel.WARNING
    )
    fun createMediaStoreUri(filename: String, directory: String): Uri? {
        val newEntry = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.DATA, "$directory/$filename")
        }

        return context.contentResolver.insert(MediaStore.Files.getContentUri("external"), newEntry)
    }

    fun createMediaStoreUri(
        filename: String,
        collection: Uri = MediaStore.Files.getContentUri("external"),
        directory: String?
    ): Uri? {
        val newEntry = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            if (directory !== null) put(MediaStore.MediaColumns.DATA, "$directory/$filename")
        }

        return context.contentResolver.insert(collection, newEntry)
    }

    suspend fun scanUri(uri: Uri, mimeType: String): Uri? {
        val cursor = contentResolver.query(
            uri,
            arrayOf(MediaStore.Files.FileColumns.DATA),
            null,
            null,
            null
        ) ?: throw Exception("Uri $uri could not be found")

        val path = cursor.use {
            if (!cursor.moveToFirst()) {
                throw Exception("Uri $uri could not be found")
            }

            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
        }

        return suspendCancellableCoroutine { continuation ->
            MediaScannerConnection.scanFile(
                context,
                arrayOf(path),
                arrayOf(mimeType)
            ) { _, scannedUri ->
                if (scannedUri == null) {
                    continuation.cancel(Exception("File $path could not be scanned"))
                } else {
                    continuation.resume(scannedUri)
                }
            }
        }
    }

    suspend fun scanFile(file: File, mimeType: String): Uri? {
        return suspendCancellableCoroutine { continuation ->
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.toString()),
                arrayOf(mimeType)
            ) { _, scannedUri ->
                if (scannedUri == null) {
                    continuation.cancel(Exception("File $file could not be scanned"))
                } else {
                    continuation.resume(scannedUri)
                }
            }
        }
    }
}