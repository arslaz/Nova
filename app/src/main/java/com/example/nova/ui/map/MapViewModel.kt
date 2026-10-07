package com.example.nova.ui.map

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

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
    var isLightMapThemes by mutableStateOf(true)
        private set
    var selectStop by mutableStateOf<Root?>(null)
    fun toggleSearch(checked: Boolean){ isSearchChecked = checked }
    fun toggleSettings(checked: Boolean){ isSettingsChecked = checked }
    fun toggleMap(checked: Boolean){ isMapChecked = checked }
    fun toggleMenuOpen() { isMenuOpen = !isMenuOpen}
    fun onMyLocation(){ isFallowing = true }
    fun onUserLocaction(){ isFallowing = false}
    fun onFirstFallowing(){ isFirstFallowing = true}
    fun toggleMapThemes(){ isLightMapThemes = !isLightMapThemes}
    fun onBottomSheet(stop: Root){
        selectStop = stop
    }
    fun offBottomSheet(){
        selectStop = null
    }

}