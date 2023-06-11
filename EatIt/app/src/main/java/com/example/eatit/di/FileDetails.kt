package com.example.eatit.di

import android.net.Uri
import okio.FileMetadata
import okio.Path

data class FileDetails(
    val uri: Uri,
    val path: Path,
    val metadata: FileMetadata
)