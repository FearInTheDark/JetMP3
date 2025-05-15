package com.vincent.jetmp3.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vincent.jetmp3.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthOtp() {
	Scaffold(
		topBar = {
			TopAppBar(
				navigationIcon = { Icon(imageVector = Icons.Outlined.ArrowBack, contentDescription = null) },
				title = {},
			)
		}
	) { innerPadding ->
		Box(
			Modifier
				.fillMaxSize()
				.background(MaterialTheme.colorScheme.background)
				.padding(innerPadding)
		) {
			Column(
				Modifier
					.fillMaxWidth()
					.align(Alignment.BottomCenter),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Icon(painter = painterResource(R.drawable.logos__google_bard_icon), null)
			}
		}
	}
}