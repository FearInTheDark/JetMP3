package com.vincent.jetmp3.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vincent.jetmp3.data.constants.FetchState
import com.vincent.jetmp3.data.repositories.SpotifyRepository
import com.vincent.jetmp3.domain.models.SpotifyArtist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
	spotifyRepository: SpotifyRepository
) : ViewModel() {
	val fetchingState: StateFlow<FetchState> = spotifyRepository.fetchState
	val fetchedArtist: StateFlow<SpotifyArtist?> = spotifyRepository.fetchedArtist

	init {
		viewModelScope.launch {
			spotifyRepository.fetchArtistInfo()
		}
	}

}