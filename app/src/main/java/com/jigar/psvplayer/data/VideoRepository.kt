package com.jigar.psvplayer.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class VideoRepository(private val context: Context) {

    fun getFolderList(): Flow<List<VideoFolder>> {
        return flow {
            val folders = mutableMapOf<String, VideoFolder>()
            val projection = arrayOf(
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID
            )
            val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
                val bucketNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)

                while (cursor.moveToNext()) {
                    val bucketId = cursor.getString(bucketIdColumn)
                    val bucketName = cursor.getString(bucketNameColumn)
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val folder = folders[bucketId]
                    if (folder == null) {
                        folders[bucketId] = VideoFolder(
                            bucketId = bucketId,
                            folderName = bucketName,
                            videoCount = 1,
                            firstVideoUri = contentUri
                        )
                    } else {
                        folders[bucketId] = folder.copy(videoCount = folder.videoCount + 1)
                    }
                }
            }
            emit(folders.values.toList())
        }.flowOn(Dispatchers.IO)
    }

    fun getVideosInFolder(bucketId: String): Flow<List<VideoItem>> {
        return flow {
            val videos = mutableListOf<VideoItem>()
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
            )
            val selection = "${MediaStore.Video.Media.BUCKET_ID} = ?"
            val selectionArgs = arrayOf(bucketId)
            val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

            context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val duration = cursor.getInt(durationColumn)
                    val size = cursor.getInt(sizeColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    videos.add(VideoItem(id, contentUri, name, duration, size))
                }
            }
            emit(videos)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteVideo(videoItem: VideoItem) {
        withContext(Dispatchers.IO) {
            context.contentResolver.delete(videoItem.uri, null, null)
        }
    }
}
