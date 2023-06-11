package com.example.eatit.di

object MetadataExtras {
    @JvmInline
    value class DisplayName(val value: String)

    @JvmInline
    value class MimeType(val value: String)

    @JvmInline
    value class FilePath(val value: String)
}