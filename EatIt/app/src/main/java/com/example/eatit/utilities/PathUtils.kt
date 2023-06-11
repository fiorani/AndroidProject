package com.example.eatit.utilities

import android.net.Uri
import okio.Path
import okio.Path.Companion.toPath

fun Path.toUri(): Uri {
    val str = this.toString()

    if (str.startsWith("content:/")) {
        return Uri.parse(str.replace("content:/", "content://"))
    }

    return Uri.parse(str)
}

fun Uri.toOkioPath(): Path {
    return this.toString().toPath(false)
}

@Deprecated(
    "Use the Uri.toOkioPath() method instead",
    ReplaceWith("toOkioPath()"),
    DeprecationLevel.WARNING
)
fun Uri.toPath() = this.toOkioPath()