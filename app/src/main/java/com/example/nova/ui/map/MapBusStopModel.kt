package com.example.nova.ui.map

import kotlinx.serialization.Serializable

@Serializable
data class Root(
    val type: String,
    val geometry: Geometry,
    val properties: Properties,
)
@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Double>,
)
@Serializable
data class Properties(
    val name: String,
    val type: String,
    val id: String,
)