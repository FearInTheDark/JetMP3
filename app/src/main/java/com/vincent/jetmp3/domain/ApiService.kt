package com.vincent.jetmp3.domain

import com.vincent.jetmp3.domain.models.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
	suspend fun doNetworkCall(): Response<String>

	@GET("posts/{id}")
	suspend fun getPost(@Path("id") id: Int): Response<Post>
}