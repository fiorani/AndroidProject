package com.example.eatit.model

import android.net.Uri
import okio.FileMetadata
import okio.Path

data class FileDetails(
    val uri: Uri,
    val path: Path,
    val metadata: FileMetadata
)