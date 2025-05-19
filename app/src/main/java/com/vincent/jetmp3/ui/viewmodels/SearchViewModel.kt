package com.vincent.jetmp3.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import com.vincent.jetmp3.data.constants.UIState
import com.vincent.jetmp3.data.models.Track
import com.vincent.jetmp3.data.repository.NestRepository
import com.vincent.jetmp3.domain.models.response.SearchResult
import com.vincent.jetmp3.media.service.MediaServiceHandler
import com.vincent.jetmp3.media.service.PlayerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
	private val nestRepository: NestRepository,
	private val mediaServiceHandler: MediaServiceHandler,
) : ViewModel() {

	private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
	val searchQuery get() = _searchQuery.asStateFlow()

	private val _result: MutableStateFlow<SearchResult?> = MutableStateFlow(null)
	val result = _result.asStateFlow()

	private val _uiState: MutableStateFlow<UIState> = MutableStateFlow(UIState.Ready)
	val uiState = _uiState.asStateFlow()

	init {
		viewModelScope.launch {
			_searchQuery.debounce(500)
				.filter { it.isNotBlank() }
				.distinctUntilChanged()
				.collectLatest { performSearch(it) }
		}
	}

	init {
		viewModelScope.launch {
			performSearch("a")
		}
	}

	private suspend fun performSearch(query: String) {
		try {
			_uiState.value = UIState.Fetching
			_result.value = nestRepository.search(query)
		} catch (e: CancellationException) {
			Log.e("SearchViewModel", "Search cancelled", e)
		} catch (e: Exception) {
			Log.e("SearchViewModel", "Search failed", e)
		} finally {
			_uiState.value = UIState.Ready
		}
	}

	@UnstableApi
	suspend fun prepareAndPlay(
		tracks: List<Track> = _result.value?.tracks ?: emptyList(),
		index: Int = 0
	) {
		mediaServiceHandler.setMediaItemList(tracks, index)
		mediaServiceHandler.onPlayerEvents(PlayerEvent.PlayPause)
	}

	fun setSearchQuery(query: String) {
		_searchQuery.value = query
	}

	fun clear() {
		_searchQuery.value = ""
		_result.value = null
	}
}