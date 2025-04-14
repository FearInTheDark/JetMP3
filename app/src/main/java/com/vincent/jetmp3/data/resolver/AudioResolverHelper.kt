package com.vincent.jetmp3.data.resolver

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import com.vincent.jetmp3.data.models.AudioFile
import dagger.hilt.android.qualifiers.ApplicationContext
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
	fun getAudioData(): List<AudioFile> {
		return getCursorData()
	}

	private fun getCursorData(): MutableList<AudioFile> {
		val audioList = mutableListOf<AudioFile>()

		cursor = context.contentResolver.query(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			projection,
			selection,
			selectionArgs,
			sortOrder
		)

		cursor?.use {
			val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
			val displayNameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
			val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
			val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
			val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
			val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)


			it.apply {
				if (count == 0) {
					Log.e("AudioResolverHelper", "getCursorData: No audio files found")
				} else {
					while (it.moveToNext()) {
						val id = getLong(idColumn)
						val displayName = getString(displayNameColumn)
						val title = getString(titleColumn)
						val artist = getString(artistColumn)
						val data = getString(dataColumn)
						val duration = getLong(durationColumn)
						val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)

						val audioFile = AudioFile(
							id = id,
							uri = uri,
							data = data,
							displayName = displayName,
							title = title,
							artist = artist,
							duration = duration
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