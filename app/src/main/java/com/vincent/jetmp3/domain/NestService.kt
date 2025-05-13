package com.vincent.jetmp3.domain

import com.vincent.jetmp3.data.constants.SearchType
import com.vincent.jetmp3.data.models.NestArtist
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.domain.models.response.FavoriteResponse
import com.vincent.jetmp3.domain.models.response.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NestService {
	@GET("tracks")
	suspend fun getAllTracks(
		@Header("Authorization") auth: String,
		@Query("img") img: Boolean = true
	): Response<List<Track>>

	@GET("artists/{artistId}")
	suspend fun getArtist(
		@Header("Authorization") auth: String,
		@Path("artistId") artistId: String
	): Response<NestArtist>

	@GET("search")
	suspend fun search(
		@Header("Authorization") auth: String,
		@Query("type") type: SearchType = SearchType.ANY,
		@Query("q") query: String,
		@Query("limit") limit: Int = 10,
		@Query("offset") offset: Int = 0,
	): Response<SearchResult>

	@POST("favorites")
	suspend fun toggleFavorite(
		@Header("Authorization") auth: String,
		@Query("trackId") trackId: Long,
	): FavoriteResponse
}