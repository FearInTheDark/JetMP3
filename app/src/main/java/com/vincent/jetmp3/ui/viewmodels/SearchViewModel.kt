package com.vincent.jetmp3.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.data.constants.FetchState
import com.vincent.jetmp3.data.modules.SpotifyManager
import com.vincent.jetmp3.domain.models.Artist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
	spotifyManager: SpotifyManager
) : ViewModel() {
	val fetchingState: StateFlow<FetchState> = spotifyManager.fetchState
	val fetchedArtist: StateFlow<Artist?> = spotifyManager.fetchedArtist

	init {
		viewModelScope.launch {
			spotifyManager.fetchArtistInfo()
		}
	}

}