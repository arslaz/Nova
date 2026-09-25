package com.example.nova.ui.map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import org.maplibre.compose.location.LocationPermission
import org.maplibre.compose.location.LocationState
import org.maplibre.compose.location.rememberDefaultHeadingProvider
import org.maplibre.compose.location.rememberDefaultLocationProvider
import org.maplibre.compose.location.rememberLocationState

class MapViewModel : ViewModel(){

    var isMenuOpen by mutableStateOf(true)
        private set
    var isSearchChecked by mutableStateOf(false)
        private set
    var isSettingsChecked by mutableStateOf(false)
        private set
    var isMapChecked by mutableStateOf(true)
        private set
    var isFallowing by mutableStateOf(false)
        private set
    var isFirstFallowing by mutableStateOf(false)
        private set
    fun toggleSearch(checked: Boolean){ isSearchChecked = checked }
    fun toggleSettings(checked: Boolean){ isSettingsChecked = checked }
    fun toggleMap(checked: Boolean){ isMapChecked = checked }
    fun toggleMenuOpen() { isMenuOpen = !isMenuOpen}
    fun onMyLocation(){ isFallowing = true}
    fun onUserLocaction(){ isFallowing = false}
    fun onFirstFallowing(){ isFirstFallowing = true}

}