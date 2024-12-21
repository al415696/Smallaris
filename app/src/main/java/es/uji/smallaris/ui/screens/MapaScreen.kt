package es.uji.smallaris.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import es.uji.smallaris.ui.state.MapaViewModel

@Composable
fun MapaScreen(
    viewModel: MapaViewModel
){
    MapboxMap(
        Modifier.fillMaxSize(),
        mapViewportState = rememberMapViewportState {
            setCameraOptions {
                zoom(15.0) // Ajusta el nivel de zoom según lo que desees mostrar.
                center(Point.fromLngLat(-0.068547, 39.994259)) // Coordenadas de la Universidad Jaume I.
                pitch(0.0)
                bearing(0.0)
            }
        },
    )
}