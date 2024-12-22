package es.uji.smallaris.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MapaScreen(
    viewModel: MapaViewModel
) {
    // Estado para almacenar el marcador (texto y la posición)
    var marker by rememberSaveable { mutableStateOf<Pair<String, Point>?>(null) }

    // Estado para mantener el mapa
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(15.0) // Ajusta el nivel de zoom según lo que desees mostrar.
            center(Point.fromLngLat(-0.068547, 39.994259)) // Coordenadas iniciales
            pitch(0.0)
            bearing(0.0)
        }
    }

    // Icono personalizado de marcador
    val markerImage = rememberIconImage(
        key = "default_marker",
        painter = painterResource(R.drawable.location_on_24px) // Cambia esto por el icono que prefieras
    )

    // Lógica para realizar la llamada suspend a la API al hacer clic en el mapa
    val scope = rememberCoroutineScope()  // Para lanzar la llamada suspendida en un CoroutineScope

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = mapViewportState,
        onMapClickListener = OnMapClickListener { point ->
            // Llamada a la función suspend dentro de una corutina
            scope.launch {
                val (_, toponimo) = viewModel.getToponimo(point.longitude(), point.latitude())
                // Almacenar el topónimo y el punto en el estado
                marker = Pair(toponimo, point)
            }
            true
        },
        content = {
            marker?.let { (text, point) ->
                PointAnnotation(point = point) {
                    iconImage = markerImage
                    iconSize = 1.0
                    textField = text  // Mostrar el texto (topónimo)
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




