@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.nova.ui.map

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState

import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.nova.R
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.dsl.interpolate
import org.maplibre.compose.expressions.dsl.linear
import org.maplibre.compose.expressions.dsl.zoom
import org.maplibre.compose.interaction.ClickResult
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.layers.LocationIndicatorLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.location.LocationPermission
import org.maplibre.compose.location.LocationState
import org.maplibre.compose.location.LocationTrackingEffect
import org.maplibre.compose.location.rememberDefaultHeadingProvider
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.location.rememberLocationState
import org.maplibre.compose.map.LocalMapState
import org.maplibre.compose.map.MapState
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.spatialk.geojson.toJson
import org.maplibre.compose.expressions.dsl.Feature
import org.maplibre.compose.expressions.dsl.asString
import org.maplibre.compose.expressions.dsl.format
import org.maplibre.compose.expressions.dsl.span
import org.maplibre.compose.expressions.value.SymbolAnchor
import org.maplibre.compose.layers.Anchor
import org.maplibre.compose.sources.rememberImageSource
import org.maplibre.compose.expressions.dsl.*
import org.maplibre.compose.expressions.dsl.Feature.get
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json

@Composable
fun MyMap(viewModel: MapViewModel = viewModel()) {

    Box(
        modifier = Modifier.fillMaxSize()
    ){

        val isSearchChecked = viewModel.isSearchChecked
        val isSettingsChecked = viewModel.isSettingsChecked
        val isMapChecked = viewModel.isMapChecked
        val isLightMapThemes = viewModel.isLightMapThemes
        val showBottomSheet = viewModel.selectStop

        val locationProvider = rememberDefaultLocationProvider()
        val headingProvider = rememberDefaultHeadingProvider()

        val locationState =
            rememberLocationState(
                provider = locationProvider,
                headingProvider = headingProvider,
            )
        val mapState =
            rememberMapState(
                baseStyle = if (isLightMapThemes) BaseStyle.Uri("https://tiles.openfreemap.org/styles/positron") else BaseStyle.Uri("https://tiles.openfreemap.org/styles/fiord"),
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
                MapLayers()
            }

        MapScreen(viewModel, locationState, mapState)
        MapZoom(
            Modifier.align (Alignment.CenterEnd),
            mapState = mapState,
        )

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

        if (showBottomSheet != null){
            MapBottomSheet()
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
//                            event.position?.let({viewModel.toggleMenuOpen()})
                            ClickResult.Pass
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

@Composable
fun MapLayers(
    viewModel: MapViewModel = viewModel(),
){
    val context = LocalContext.current
    val geojsonBusStopMinsk = remember {
        context.assets.open("stops.geojson")
            .bufferedReader()
            .use { it.readText() }
    }
    val geojsonSubwayMinsk = remember {
        context.assets.open("metro_minsk.geojson")
            .bufferedReader()
            .use { it.readText() }
    }
    val geojsonExitSubwayMinsk = remember {
        context.assets.open("metro_exit_minsk.geojson")
            .bufferedReader()
            .use { it.readText() }
    }

    val busStopMinsk =
        rememberGeoJsonSource(GeoJsonData.JsonString(geojsonBusStopMinsk))
    val metroMinsk =
        rememberGeoJsonSource(GeoJsonData.JsonString(geojsonSubwayMinsk))
    val metroExitMinsk =
        rememberGeoJsonSource(GeoJsonData.JsonString(geojsonExitSubwayMinsk))
    // Bus stop Minsk
    SymbolLayer(
        id = "bus-stop-Minsk",
        source = busStopMinsk,
        iconImage = switch(
            input = get("type").asString(),
            cases = listOf(
                case("bus", image(painterResource(R.drawable.ic_bus_stop))),
                case("trolleybus", image(painterResource(R.drawable.ic_trolleybus_stop)))
            ),
            fallback = image(painterResource(R.drawable.ic_tram_stop))
        ),
        iconSize = const(0.85f),
        onClick = { features ->
            viewModel.onBottomSheet(Json.decodeFromString<Root>(features[0].toJson()))
            ClickResult.Consume
        },
        iconAllowOverlap = const(true),
        iconOpacity = interpolate(linear(),zoom(),13.0 to const(0.0f), 14.0 to const(1.0f)),
        textField = format(span(Feature.get("name").asString())),
        textFont = const(listOf("Noto Sans Regular")),
        textSize = const(8.sp),
        textAnchor = const(SymbolAnchor.Top),
        textOffset = const(Offset(0.0f, 0.8f)).cast(),
        textOpacity = interpolate(linear(),zoom(),14.0 to const(0.0f), 15.0 to const(1.0f))
    )

    // Metro exit number
    SymbolLayer(
        id = "metro-exit-Nline-Minsk",
        source = metroExitMinsk,
        iconImage = switch(
            get("colour").asString(),
            listOf(
                case("blue", image(painterResource(R.drawable.number_one_minsk_metro))),
                case("red", image(painterResource(R.drawable.number_two_minsk_metro)))
            ),
            fallback = image(painterResource(R.drawable.number_three_minsk_metro))
        ),
        iconSize = const(0.5f),
        iconAllowOverlap = const(true),
        iconAnchor = const(SymbolAnchor.Left),
        iconOpacity = interpolate(linear(), zoom(), 15.0 to const(0.0f), 16.0 to const(1.0f)),
        iconOffset = const(Offset(25.0f, 0.0f)).cast(),
    )

    // Metro exit Minsk
    SymbolLayer(
        id = "metro-exit-Minsk",
        source = metroExitMinsk,
        iconImage = image(painterResource(R.drawable.ic_metro_minsk)),
        iconSize = const(1.0f),
        onClick = { features ->
            Log.d("clicke","Clicked on ${features[0].toJson()}")
            ClickResult.Consume
        },
        iconAllowOverlap = const(true),
        iconOpacity = interpolate(linear(),zoom(),14.0 to const(0.0f), 15.0 to const(1.0f)),
        textField = format(
            span(Feature.get("name").asString()),
            span(const("\nВыход ")),
            span(Feature.get("ref").asString())
        ),
        textFont = const(listOf("Noto Sans Regular")),
        textSize = const(9.sp),
        textAnchor = const(SymbolAnchor.Top),
        textOffset = const(Offset(0.0f, 0.7f)).cast(),
        textOpacity = interpolate(linear(),zoom(),15.0 to const(0.0f), 16.0 to const(1.0f))
    )

    // Metro Minsk Number line
    SymbolLayer(
        id = "metro-stop-Nline-Minsk",
        source = metroMinsk,
        iconImage = switch(
            get("colour").asString(),
            listOf(
                case("blue", image(painterResource(R.drawable.number_one_minsk_metro))),
                case("red", image(painterResource(R.drawable.number_two_minsk_metro)))
            ),
            fallback = image(painterResource(R.drawable.number_three_minsk_metro))
        ),
        iconSize = const(0.5f),
        iconAllowOverlap = const(true),
        iconAnchor = const(SymbolAnchor.Left),
        iconOpacity = interpolate(linear(), zoom(), 12.0 to const(0.0f), 13.0 to const(1.0f), 14.0 to const(1.0f), 15.0 to const(0.0f)),
        iconOffset = const(Offset(25.0f, 0.0f)).cast(),
    )

    // Metro Minsk
    SymbolLayer(
        id = "metro-stop-Minsk",
        source = metroMinsk,
        iconImage = image(painterResource(R.drawable.ic_metro_minsk)),
        iconSize = const(1.0f),
        onClick = { features ->
            Log.d("clicke","Clicked on ${features[0].toJson()}")
            ClickResult.Consume
        },
        iconAllowOverlap = const(true),
        iconOpacity = interpolate(linear(),zoom(),11.0 to const(0.0f), 12.0 to const(1.0f), 14.0 to const(1.0f), 15.0 to const(0.0f)),
        textField = format(span(Feature.get("name").asString())),
        textFont = const(listOf("Noto Sans Regular")),
        textSize = const(9.sp),
        textAnchor = const(SymbolAnchor.Top),
        textOffset = const(Offset(0.0f, 0.7f)).cast(),
        textOpacity = interpolate(linear(),zoom(),11.0 to const(0.0f), 12.0 to const(1.0f), 14.0 to const(1.0f), 15.0 to const(0.0f))
    )
}

@Composable
fun MapBottomSheet(viewModel: MapViewModel = viewModel()){
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = { viewModel.offBottomSheet()}
    ) {
        val selectStop = viewModel.selectStop
        Text(
            selectStop .toString(),
            modifier = Modifier.padding(16.dp)
        )
    }
}