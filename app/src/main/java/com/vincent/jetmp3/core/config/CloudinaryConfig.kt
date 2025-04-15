package com.vincent.jetmp3.core.config

import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.vincent.jetmp3.data.constants.CloudinaryCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Singleton

@Singleton
object CloudinaryConfig {
	private val cloudinary = Cloudinary(
		ObjectUtils.asMap(
			"cloud_name" to CloudinaryCredentials.CloudName,
			"api_key" to CloudinaryCredentials.ApiKey,
			"api_secret" to CloudinaryCredentials.ApiSecret
		)
	)

	suspend fun uploadImage(file: File): String? {
		return withContext(Dispatchers.IO) {
			try {
				val result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
				result["url"] as? String
			} catch (e: Exception) {
				Log.d("CloudinaryConfig", "Error uploading image: ${e.message}")
				null
			}
		}
	}
}