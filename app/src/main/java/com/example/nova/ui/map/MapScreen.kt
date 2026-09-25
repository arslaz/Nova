package com.example.nova.ui.map

import android.text.Layout
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.LineHeightStyle
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.expressions.dsl.zoom
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.layers.LocationIndicatorLayer
import org.maplibre.compose.location.LocationPermission
import org.maplibre.compose.location.LocationState
import org.maplibre.compose.location.LocationTrackingEffect
import org.maplibre.compose.location.rememberDefaultHeadingProvider
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.location.rememberLocationState
import org.maplibre.compose.map.LocalMapState
import org.maplibre.compose.map.MapState

@Composable
fun MyMap(viewModel: MapViewModel = viewModel()) {

    Box(
        modifier = Modifier.fillMaxSize()
    ){


        val isMenuOpen = viewModel.isMenuOpen
        val isSearchChecked = viewModel.isSearchChecked
        val isSettingsChecked = viewModel.isSettingsChecked
        val isMapChecked = viewModel.isMapChecked

        val locationProvider = rememberDefaultLocationProvider()
        val headingProvider = rememberDefaultHeadingProvider()

        val locationState =
            rememberLocationState(
                provider = locationProvider,
                headingProvider = headingProvider,
            )
        val mapState =
            rememberMapState(
                baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
                initialCameraPosition = CameraPosition(target = Position(latitude = 53.9045, longitude = 27.5615), zoom = 10.0),
                ){
                val mapState = checkNotNull(LocalMapState.current)

                LocationIndicatorLayer(
                    id = "user",
                    locationState = locationState,
                )
                LocationTrackingEffect(locationState = locationState) {
                    if(viewModel.isFallowing && !viewModel.isFirstFallowing){
                        mapState.animateCamera(CameraUpdate(target = currentLocation.position, zoom = 15.0))
                        viewModel.onFirstFallowing()
                    }else if(viewModel.isFallowing){
                        mapState.animateCamera(CameraUpdate(target = currentLocation.position))
                    }

                }
            }

        MapScreen(viewModel, locationState, mapState)
        MapZoom(
            Modifier.align (Alignment.CenterEnd),
            mapState = mapState,
        )

        if (isMenuOpen) {
            MapInterfaceMenu(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars),
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
                getCurrentLocation = {
                    viewModel.onMyLocation()
                    if (locationState.permission !is LocationPermission.Granted) {
                        locationState.requestPermission()
                    }
                }

            )
        }
    }

}

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    locationState: LocationState,
    mapState: MapState
){

    MaplibreMap (
        modifier = Modifier.fillMaxSize(),
        state = mapState,
        interactions =
            MapInteractions {
                callbacks {
                    click {
                        onEvent { event ->
                            event.position?.let({viewModel.toggleMenuOpen()})
                            ClickResult.Consume
                        }
                    }


                }
                camera {
                    pan {
                        onStart { viewModel.onUserLocaction() }
                    }
                }
            }
    )

}
@Composable
fun MapInterfaceMenu(
    modifier: Modifier = Modifier,
    isCheckedMap: Boolean,
    onCheckedChangeMap: (Boolean) -> Unit,
    isCheckedSearch: Boolean,
    onCheckedChangeSearch: (Boolean) -> Unit,
    isCheckedSettings: Boolean,
    onCheckedChangeSettings: (Boolean) -> Unit
){

    Row(
        modifier = modifier
            .padding(horizontal = 100.dp, vertical = 8.dp)
            .height(height = 60.dp)
            .fillMaxWidth()
            .clip(CircleShape)
            .background(color = Color.Black.copy(0.95f))
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ){
        IconToggleButton(
            checked = isCheckedMap,
            onCheckedChange = onCheckedChangeMap,
        ) {
            Icon(
                Icons.Filled.Place,
                contentDescription = "Карта",
                tint = if (isCheckedMap) Color(0xFFEC407A) else Color(0xFFB0BEC5),
                modifier = Modifier.size(size = 32.dp)
            )
        }
        IconToggleButton(
            checked = isCheckedSearch,
            onCheckedChange = onCheckedChangeSearch
        ) {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Поиск",
                tint = if (isCheckedSearch) Color(0xFFEC407A) else Color(0xFFB0BEC5),
                modifier = Modifier.size(size = 32.dp)
            )
        }
        IconToggleButton(
            checked = isCheckedSettings,
            onCheckedChange = onCheckedChangeSettings
        ) {
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
            .background(Color.Black.copy(alpha = 0.90f))

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

@Composable
fun MapZoom(
    modifier: Modifier = Modifier,
    mapState: MapState
){
    Box(
        modifier = modifier
            .width(width = 40.dp)
            .clip(CircleShape)
            .fillMaxHeight()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .pointerInput(key1 = Unit) {
                detectDragGestures(
                    onDragStart = { startPosition -> },
                    onDragEnd = { },
                    onDragCancel = { },
                ) { change, dragAmount ->
                    Log.d("zommstrinp", "${dragAmount.y}")
                    change.consume()
                    mapState.setCameraPosition(mapState.cameraPosition.copy( zoom = mapState.cameraPosition.zoom - dragAmount.y / 40))
                }
            }
    )

}