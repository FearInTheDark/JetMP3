package com.vincent.jetmp3.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vincent.jetmp3.R
import com.vincent.jetmp3.ui.theme.LabelLineSmall
import com.vincent.jetmp3.utils.NavigationBarItem
import com.vincent.jetmp3.utils.Screen

@Composable
fun MyNavigationBar(
	modifier: Modifier = Modifier,
	navController: NavController
) {
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute = navBackStackEntry?.destination?.route

	val items = listOf(
		NavigationBarItem(
			route = "home",
			title = "Home",
			icon = R.drawable.material_symbols__home_outline_rounded,
			activeIcon = R.drawable.material_symbols__home_rounded
		),
		NavigationBarItem(
			route = "search",
			title = "Search",
			icon = R.drawable.tabler__search,
			activeIcon = R.drawable.mingcute__search_fill
		),
		NavigationBarItem(
			route = "library",
			title = "Library",
			icon = R.drawable.solar__library_outline,
			activeIcon = R.drawable.solar__library_bold
		),
		NavigationBarItem(
			route = "now_playing",
			title = "Playing",
			icon = R.drawable.solar__play_outline,
			activeIcon = R.drawable.solar__play_bold
		),

		)


	NavigationBar(
		containerColor = Color.Transparent,
		modifier = modifier
	) {
		Row(
			horizontalArrangement = Arrangement.Absolute.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.padding(
					horizontal = 24.dp,
					vertical = 4.dp
				)
				.background(Color.Transparent)
		) {
			items.forEach() { item ->
				NavBarItem(
					value = item,
					selected = currentRoute == item.route,
				) {
					if (currentRoute != item.route) {
						navController.navigate(item.route) {
							popUpTo(Screen.Home.route) { inclusive = false }
							launchSingleTop = true
						}
					}
				}
			}
		}

	}
}

@Composable
fun NavBarItem(
	value: NavigationBarItem,
	selected: Boolean,
	onClick: () -> Unit
) {

	Box(
		modifier = Modifier
			.clip(RoundedCornerShape(4.dp))
			.background(Color.Transparent)
			.clickable { onClick() },
		contentAlignment = Alignment.Center,
	) {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Icon(
				painter = if (selected) painterResource(value.activeIcon) else painterResource(value.icon),
				contentDescription = null,
				tint = if (selected) MaterialTheme.colorScheme.onSurface else Color.Gray,
				modifier = Modifier.size(28.dp)
			)
			Text(
				text = value.title,
				style = LabelLineSmall,
				color = if (selected) MaterialTheme.colorScheme.onSurface else Color.Gray
			)
		}
	}
}


