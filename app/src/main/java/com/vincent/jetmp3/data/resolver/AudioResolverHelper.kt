package com.vincent.jetmp3.data.resolver

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import com.vincent.jetmp3.domain.models.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import javax.inject.Inject

class AudioResolverHelper @Inject constructor(
	@ApplicationContext val context: Context
) {
	private var cursor: Cursor? = null

	private val selection = "${MediaStore.Audio.Media.DATA} LIKE ? OR ${MediaStore.Audio.Media.DATA} LIKE ?"

	private val selectionArgs = arrayOf(
		"%/storage/emulated/0/Backup/%",
		"%/storage/emulated/0/Download/%"
	)

	private val projection = arrayOf(
		MediaStore.Audio.Media._ID,
		MediaStore.Audio.Media.DISPLAY_NAME,
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.DATA,
		MediaStore.Audio.Media.DURATION,
		MediaStore.Audio.Media.SIZE,
		MediaStore.Audio.Media.ALBUM,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.YEAR
	)

	private val sortOrder = null

	@WorkerThread
	fun getLocalTracks(): List<Track> {
		return getCursorData()
	}

	private fun getCursorData(): MutableList<Track> {
		val audioList = mutableListOf<Track>()

		cursor = context.contentResolver.query(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			projection,
			selection,
			selectionArgs,
			sortOrder
		)

		cursor?.use {
			val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
			val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)


			it.apply {
				if (count == 0) {
					Log.e("AudioResolverHelper", "getCursorData: No audio files found")
				} else {
					while (it.moveToNext()) {
						val id = getLong(idColumn)
						val title = getString(titleColumn)
						val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

						val audioFile = Track(
							id = id,
							name = title,
							uri = uri.toString(),
							artistId = "artist",
							createdAt = Date().toString()
						)

						Log.d("AudioResolverHelper", "getCursorData: $audioFile")
						audioList.add(audioFile)
					}
				}
			}
		}

		return audioList
	}
}