package com.vincent.jetmp3.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vincent.jetmp3.data.constants.FetchState
import com.vincent.jetmp3.ui.components.search.ArtistDetailScreen
import com.vincent.jetmp3.ui.layout.LoadingOverlay
import com.vincent.jetmp3.ui.theme.HeadStyleLarge
import com.vincent.jetmp3.ui.theme.LabelLineMedium
import com.vincent.jetmp3.ui.viewmodels.SearchViewModel

@Composable
fun SearchScreen(searchViewModel: SearchViewModel = hiltViewModel()) {

	val fetchedArtist = searchViewModel.fetchedArtist.collectAsState()
	val fetchingState = searchViewModel.fetchingState.collectAsState()

	when (fetchingState.value) {
		FetchState.SUCCESS -> @Composable {
			Column(
				Modifier
					.fillMaxSize()
					.padding(4.dp),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = "Search",
					style = HeadStyleLarge,
					color = MaterialTheme.colorScheme.onSurface
				)

				Spacer(Modifier.height(50.dp))
				if (fetchedArtist.value != null) {
					ArtistDetailScreen(artist = fetchedArtist.value)
				} else {
					Text(
						text = "No Artist Fetched",
						style = LabelLineMedium,
						color = MaterialTheme.colorScheme.onSurface
					)
				}
			}
		}

		FetchState.LOADING -> @Composable {
			LoadingOverlay(true)
		}

		else -> {
			Column(
				Modifier
					.fillMaxSize()
					.padding(4.dp),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = "Error",
					style = HeadStyleLarge,
					color = MaterialTheme.colorScheme.onSurface
				)
			}
		}
	}
}