package com.vincent.jetmp3.domain

import com.vincent.jetmp3.data.constants.SearchType
import com.vincent.jetmp3.data.models.NestArtist
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.domain.models.CategoryScreenData
import com.vincent.jetmp3.domain.models.RecentCategoryItem
import com.vincent.jetmp3.domain.models.response.NestResponse
import com.vincent.jetmp3.domain.models.response.SearchResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NestService {

	@GET("tracks/{trackId}")
	suspend fun getTrack(
		@Header("Authorization") auth: String,
		@Path("trackId") trackId: Long,
	): Response<Track>

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
	): Response<NestResponse>

	@POST("playlists/{trackId}")
	suspend fun addTrackToNewPlaylist(
		@Header("Authorization") auth: String,
		@Path("trackId") trackId: Long,
		@Body body: Map<String, String>,
	) : Response<NestResponse>

	@POST("playlists/add/{playlistId}/{trackId}")
	suspend fun toggleTrackToPlaylist(
		@Header("Authorization") auth: String,
		@Path("playlistId") playlistId: Number,
		@Path("trackId") trackId: Number,
	): Response<NestResponse>

	@DELETE("playlists/{playlistId}")
	suspend fun deletePlaylist(
		@Header("Authorization") auth: String,
		@Path("playlistId") playlistId: Number,
	): Response<NestResponse>

	@GET("favorites")
	suspend fun getFavoriteData(
		@Header("Authorization") auth: String,
	): Response<CategoryScreenData>

	@GET("tracks/history")
	suspend fun getHistoryData(
		@Header("Authorization") auth: String,
	): Response<CategoryScreenData>

	@POST("tracks/listen/{trackId}")
	suspend fun addToHistory(
		@Header("Authorization") auth: String,
		@Path("trackId") trackId: Long,
	): Response<NestResponse>

	@GET("playlists/{playlistId}")
	suspend fun getPlaylistData(
		@Header("Authorization") auth: String,
		@Path("playlistId") playlistId: String,
	): Response<CategoryScreenData>

	@GET("tracks/categories")
	suspend fun getCategories(
		@Header("Authorization") auth: String,
	): Response<RecentCategoryItem>
}