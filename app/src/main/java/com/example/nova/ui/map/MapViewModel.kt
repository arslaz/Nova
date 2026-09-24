package com.example.nova.ui.map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class MapViewModel : ViewModel(){

    var isMenuOpen by mutableStateOf(false)
        private set
    var isSearchChecked by mutableStateOf(false)
        private set
    var isSettingsChecked by mutableStateOf(false)
        private set
    var isMapChecked by mutableStateOf(false)
        private set
    fun toggleSearch(checked: Boolean){ isSearchChecked = checked }
    fun toggleSettings(checked: Boolean){ isSettingsChecked = checked }
    fun toggleMap(checked: Boolean){ isMapChecked = checked }
    fun openMenu() { isMenuOpen = true}
    fun closeMenu() { isMenuOpen = false}

    fun getCurrentLocation(){

    }

}