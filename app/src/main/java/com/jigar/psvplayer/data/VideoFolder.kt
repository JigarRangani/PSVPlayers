package com.jigar.psvplayer.data

import android.net.Uri

data class VideoFolder(
    val bucketId: String,
    val folderName: String,
    val videoCount: Int,
    val firstVideoUri: Uri
)