package es.uji.smallaris.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import es.uji.smallaris.R
import es.uji.smallaris.ui.state.MapaViewModel
import java.util.Locale

@Composable
fun MapaScreen(
    viewModel: MapaViewModel
) {
    var markers by rememberSaveable { mutableStateOf(listOf<Point>()) }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(15.0) // Ajusta el nivel de zoom según lo que desees mostrar.
            center(Point.fromLngLat(-0.068547, 39.994259)) // Coordenadas de la Universidad Jaume I.
            pitch(0.0)
            bearing(0.0)
        }
    }

    // Icono personalizado de marcador
    val markerImage = rememberIconImage(
        key = "default_marker",
        painter = painterResource(R.drawable.location_on_24px) // Cambia esto por el icono que prefieras
    )

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        onMapClickListener = OnMapClickListener { point ->
            markers = markers + point
            true
        },
        content = {
            markers.forEach { point ->
                PointAnnotation(point = point,
                    onClick = { clickedPoint ->
                        // Eliminar el marcador al hacer clic
                        markers = markers.filter { it != clickedPoint.point }
                        true // Consumir el evento de clic
                    }) {
                    iconImage = markerImage
                    iconSize = 1.0
                    textField = "${point.longitude()}, ${point.latitude()}"
                    textColor = Color.Black
                    textSize = 10.0
                    textOffset = listOf(0.0, 1.5) // Ajuste para colocar el texto correctamente
                }
            }

            // Aplicar estilo con idioma en español
            MapEffect(Unit) { mapView ->
                mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                    style.localizeLabels(Locale("es"))
                }
            }
        }
    )
}
