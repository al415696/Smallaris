package es.uji.smallaris.ui.screens.rutas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotationState
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions
import es.uji.smallaris.R
import es.uji.smallaris.model.Ruta
import es.uji.smallaris.ui.components.TopBackBar
import java.util.Locale


@Composable
fun RutasMapContent(
    onBack: () -> Unit = {},
    ruta: Ruta
) {
    val routeLine by rememberSaveable { mutableStateOf<LineString?>(ruta.getTrayecto()) }
    val polylineState = remember {
        PolylineAnnotationState().apply {
            lineColor = Color.Blue
            lineWidth = 5.0
            // Configura otras propiedades según sea necesario
        }
    }

    val markerImage =
        rememberIconImage(
            key = "default_marker",
            painter = painterResource(R.drawable.add_location_alt_24px) // Cambia esto por el icono que prefieras
        )


    val savedMarkerInicio = rememberSaveable {
        mutableStateOf(Point.fromLngLat(ruta.getInicio().longitud, ruta.getInicio().latitud))
    }
    val savedMarkerFin = rememberSaveable {
        mutableStateOf(Point.fromLngLat(ruta.getFin().longitud, ruta.getFin().latitud))
    }

    val mapboxMapState =
        rememberMapViewportState {
            setCameraOptions {
                zoom(15.0) // Ajusta el nivel de zoom según lo que desees mostrar.
                center(
                    Point.fromLngLat(
                        (ruta.getInicio().longitud + ruta.getFin().longitud) / 2,
                        (ruta.getInicio().latitud + ruta.getFin().latitud) / 2
                    )
                ) // Coordenadas de la Universidad Jaume I.
                pitch(0.0)
                bearing(0.0)
            }
        }

    val opcionesAddRuta = rememberSaveable { mutableStateOf(OpcionesAddRuta.Toponimo) }

    BackHandler {
        onBack()
    }

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

            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapboxMapState,
                compass = {},
                logo = {},
                scaleBar = {},
                attribution = {},

                content = {
                    // Dibujar la ruta si está disponible
                    routeLine?.let { line ->
                        LaunchedEffect(line) {
                            mapboxMapState.transitionToOverviewState(
                                OverviewViewportStateOptions.Builder()
                                    .geometry(line)
                                    .padding(EdgeInsets(100.0, 100.0, 100.0, 100.0))
                                    .build()
                            )
                        }

                        // Dibujar la ruta si está disponible
                        PolylineAnnotation(
                            points = line.coordinates(),
                            polylineAnnotationState = polylineState,
                        )

                        // Dibujar el punto de inicio (con un marcador específico)
                        PointAnnotation(point = savedMarkerInicio.value) {
                            iconColor = Color.Green // Color verde para el inicio
                            iconImage = markerImage
                            iconSize = 2.5
                            iconOffset = listOf(0.0, -10.0)
                        }

                        // Dibujar el punto de fin (con un marcador diferente)
                        PointAnnotation(point = savedMarkerFin.value) {
                            iconColor = Color.Red // Color rojo para el fin
                            iconImage = markerImage
                            iconSize = 2.5
                            iconOffset = listOf(0.0, -10.0)
                        }
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


