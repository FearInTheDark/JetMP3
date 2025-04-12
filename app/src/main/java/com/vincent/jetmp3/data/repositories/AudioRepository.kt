package com.vincent.jetmp3.data.repositories

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.MediaStore
import com.vincent.jetmp3.data.models.AudioFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor() {
	@SuppressLint("NewApi")
	suspend fun getAudioFiles(contentResolver: ContentResolver): List<AudioFile> =
		withContext(Dispatchers.IO) {
			val audioList = mutableListOf<AudioFile>()
			val selection = "${MediaStore.Audio.Media.DATA} LIKE ? OR ${MediaStore.Audio.Media.DATA} LIKE ?"
			val selectionArgs = arrayOf(
				"%/storage/emulated/0/Music/%",
				"%/storage/emulated/0/Download/%"
			)



			val projection = arrayOf(
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.DATA,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.SIZE,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.YEAR
			)

			val cursor = contentResolver.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projection,
				selection,
				selectionArgs,
				null
			)

			cursor?.use {
				val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
				val displayNameColumn =
					it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
				val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
				val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
				val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

				while (it.moveToNext()) {
					val id = it.getLong(idColumn)
					val displayName = it.getString(displayNameColumn) ?: "Unknown"
					val artist = it.getString(artistColumn) ?: "Unknown"
					val uri = it.getString(dataColumn)
					val duration = it.getLong(durationColumn)
					audioList.add(AudioFile(id, displayName, artist, uri, duration))
				}
			}
			audioList
		}
}