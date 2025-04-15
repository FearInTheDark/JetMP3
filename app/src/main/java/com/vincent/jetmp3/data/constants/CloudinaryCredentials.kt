package com.vincent.jetmp3.data.constants

sealed class CloudinaryCredentials(val key: String) {
	data object CloudName: CloudinaryCredentials("dsy29z79v")
	data object ApiKey : CloudinaryCredentials("285498128657738")
	data object ApiSecret : CloudinaryCredentials("RVnwELrPATWBzAwKeOgMmCtOGxc")
}