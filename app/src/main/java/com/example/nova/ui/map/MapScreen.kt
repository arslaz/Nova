package com.example.nova.ui.map

import android.icu.text.StringSearch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.nio.file.WatchEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.draw.blur
import org.maplibre.compose.expressions.dsl.padding

@Composable
fun MyMap(viewModel: MapViewModel = viewModel()) {
    val mapState =
        rememberMapState(
            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
            initialCameraPosition = CameraPosition(target = Position(latitude = 53.9045, longitude = 27.5615), zoom = 10.0)

        )
    Box(
        modifier = Modifier.fillMaxSize()
    ){

        MaplibreMap (
            modifier = Modifier.fillMaxSize(),
            state = mapState
        )

        val mutableState = viewModel.isMenuOpen
        val isSearchChecked = viewModel.isSearchChecked
        val isSettingsChecked = viewModel.isSettingsChecked
        val isMapChecked = viewModel.isMapChecked

        if (!mutableState) {
            MapArrowInterface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                openMenu = { viewModel.openMenu() }
            )
        }
        if (mutableState) {
            MapInterfaceMenu(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                closeMenu = { viewModel.closeMenu() },
                isCheckedMap = isMapChecked,
                onCheckedChangeMap = { viewModel.toggleMap(it)},
                isCheckedSearch = isSearchChecked,
                onCheckedChangeSearch = { viewModel.toggleSearch(it)},
                isCheckedSettings = isSettingsChecked,
                onCheckedChangeSettings = { viewModel.toggleSettings(it)}
            )
            MapInterfacePlace(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                getCurrentLocation = {viewModel.getCurrentLocation()}

            )
        }
    }

}
@Composable
fun MapArrowInterface(
    modifier: Modifier = Modifier,
    openMenu: () -> Unit
){
    Button(
        onClick = openMenu,
        modifier = modifier
    ) {
        Text(text = "->")
    }
}
@Composable
fun MapInterfaceMenu(
    modifier: Modifier = Modifier,
    closeMenu: () -> Unit,
    isCheckedMap: Boolean,
    onCheckedChangeMap: (Boolean) -> Unit,
    isCheckedSearch: Boolean,
    onCheckedChangeSearch: (Boolean) -> Unit,
    isCheckedSettings: Boolean,
    onCheckedChangeSettings: (Boolean) -> Unit
){
    Row(
        modifier = modifier
            .padding(horizontal = 90.dp)
            .height(height = 60.dp)
            .fillMaxWidth()
            .clip(CircleShape)
            .background(color = Color.DarkGray)
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconToggleButton(checked = isCheckedMap, onCheckedChange = onCheckedChangeMap) {
            Icon(
                Icons.Filled.Place,
                contentDescription = "Карта",
                tint = if (isCheckedMap) Color(0xFFEC407A) else Color(0xFFB0BEC5),
                modifier = Modifier.size(size = 32.dp)
            )
        }
        IconToggleButton(checked = isCheckedSearch, onCheckedChange = onCheckedChangeSearch) {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Поиск",
                tint = if (isCheckedSearch) Color(0xFFEC407A) else Color(0xFFB0BEC5),
                modifier = Modifier.size(size = 32.dp)
            )
        }
        IconToggleButton(checked = isCheckedSettings, onCheckedChange = onCheckedChangeSettings) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = if (isCheckedSettings) Color(0xFFEC407A) else Color(0xFFB0BEC5),
                modifier = Modifier.size(size = 32.dp)

            )
        }
    }

}

@Composable
fun MapInterfacePlace(
    modifier: Modifier = Modifier,
    getCurrentLocation: () -> Unit
){
    IconButton(
        onClick = getCurrentLocation,
        modifier = modifier
            .padding(end = 10.dp, bottom = 100.dp)
            .size(size = 54.dp)
            .clip(CircleShape)
            .background(Color.DarkGray)

    ) {
        Icon(
            Icons.Filled.NearMe,
            contentDescription = "NearMe",
            modifier = Modifier
                .size(size = 34.dp),
            tint = Color(0xFFB0BEC5)
        )
    }
}