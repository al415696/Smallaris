package es.uji.smallaris.ui.screens.lugares

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.localization.localizeLabels
import es.uji.smallaris.R
import es.uji.smallaris.ui.components.TopBackBar
import java.util.Locale


@Composable
fun LugaresMapContent(
    onBack: () -> Unit = {},
    marker: Point = Point.fromLngLat(-0.068547, 39.994259)
) {
    val mapboxMapState =
        rememberMapViewportState {
            setCameraOptions {
                zoom(15.0) // Ajusta el nivel de zoom según lo que desees mostrar.
                center(
                    marker
                ) // Coordenadas de la Universidad Jaume I.
                pitch(0.0)
                bearing(0.0)
            }
        }
    BackHandler {
        onBack()
    }


    val savedMarker = rememberSaveable { mutableStateOf(marker) }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary

    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            TopBackBar(onBack)

            val markerImage = rememberIconImage(
                key = "default_marker",
                painter = painterResource(R.drawable.add_location_alt_24px)// Cambia esto por el icono que prefieras
            )
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapboxMapState,
                compass = {},
                logo = {},
                scaleBar = {},
                attribution = {},

                content = {

                    PointAnnotation(point = savedMarker.value) {
                        iconColor = Color.Red
                        iconImage = markerImage
                        iconSize = 3.5
                        iconOffset = listOf(0.0, -10.0)
                        textColor = Color.Black
                        textSize = 10.0
                        textOffset =
                            listOf(0.0, 1.5) // Ajuste para colocar el texto correctamente
                    }


                    // Aplicar estilo con idioma en español
                    MapEffect(Unit) { mapView ->
                        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) { style ->
                            style.localizeLabels(Locale("es"))
                        }
                    }
                }
            )


        }
    }
}


@Preview
@Composable
private fun PreviewLugaresInteresMapContent() {
    LugaresMapContent()
}

