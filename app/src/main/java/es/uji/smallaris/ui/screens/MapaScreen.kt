package es.uji.smallaris.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotationState
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.localization.localizeLabels
import es.uji.smallaris.R
import es.uji.smallaris.ui.state.MapaViewModel
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun MapaScreen(
    viewModel: MapaViewModel
) {

    // Llama a debugFillList() cuando el composable se inicializa
    LaunchedEffect(Unit) {
        viewModel.debugFillList()
    }

    // Estado para almacenar la ruta
    var routeLine by rememberSaveable { mutableStateOf<LineString?>(null) }

    val items = viewModel.listaRutas

    // Estado para almacenar el marcador (texto y la posición)
    var origen by rememberSaveable { mutableStateOf<Pair<String, Point>?>(null) }
    var destino by rememberSaveable { mutableStateOf<Pair<String, Point>?>(null) }

    // Estado para controlar el número de clics y alternar entre origen y destino
    var clickCount by rememberSaveable { mutableIntStateOf(0) }

    // Estado para mantener el mapa
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(10.0) // Ajusta el nivel de zoom según lo que desees mostrar.
            center(Point.fromLngLat(-0.068547, 39.994259)) // Coordenadas iniciales
            pitch(0.0)
            bearing(0.0)
        }
    }

    val polylineState = remember {
        PolylineAnnotationState().apply {
            lineColor = Color.Blue
            lineWidth = 5.0
            // Configura otras propiedades según sea necesario
        }
    }

    // Icono personalizado de marcador
    val markerImage = rememberIconImage(
        key = "default_marker",
        painter = painterResource(R.drawable.location_on_24px) // Cambia esto por el icono que prefieras
    )

    // Lógica para realizar la llamada suspend a la API al hacer clic en el mapa
    val scope = rememberCoroutineScope()  // Para lanzar la llamada suspendida en un CoroutineScope

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            onMapClickListener = { point ->
                // Llamada a la función suspend dentro de una corutina
                scope.launch {
                    val (_, toponimo) = viewModel.getToponimo(point.longitude(), point.latitude())

                    // Cambiar comportamiento de clic según el número de clics
                    when (clickCount) {
                        0 -> {
                            origen = Pair(toponimo, point)  // Establecer origen
                        }
                        1 -> {
                            destino = Pair(toponimo, point)  // Establecer destino
                            // Una vez que origen y destino están definidos, obtener la ruta
                            val ruta = viewModel.getRuta(origen!!.second, destino!!.second)
                            routeLine = ruta.getTrayecto()  // Actualiza la ruta
                        }
                        else -> {
                            // Intercambiar origen y destino
                            origen = destino
                            destino = Pair(toponimo, point)  // Establecer nuevo destino
                            // Obtener la ruta con los nuevos origen y destino
                            val ruta = viewModel.getRuta(origen!!.second, destino!!.second)
                            routeLine = ruta.getTrayecto()  // Actualiza la ruta
                        }
                    }

                    // Incrementar el contador de clics
                    clickCount = (clickCount + 1) % 3  // Esto hará que vuelva a 0 después del tercer clic
                }
                true
            },
            content = {
                // Dibujar el marcador de origen
                origen?.let { (text, point) ->
                    PointAnnotation(point = point) {
                        iconImage = markerImage
                        iconSize = 1.0
                        textField = text  // Mostrar el texto (topónimo)
                        textColor = Color.Black
                        textSize = 10.0
                        textOffset = listOf(0.0, 1.5) // Ajuste para colocar el texto correctamente
                    }
                }

                // Dibujar el marcador de destino
                destino?.let { (text, point) ->
                    PointAnnotation(point = point) {
                        iconImage = markerImage
                        iconSize = 1.0
                        textField = text  // Mostrar el texto (topónimo)
                        textColor = Color.Black
                        textSize = 10.0
                        textOffset = listOf(0.0, 1.5) // Ajuste para colocar el texto correctamente
                    }
                }

                // Dibujar la ruta si está disponible
                routeLine?.let { line ->
                    PolylineAnnotation(
                        points = line.coordinates(),
                        polylineAnnotationState = polylineState,
                    )
                }

                // Aplicar estilo con idioma en español
                MapEffect(Unit) { mapView ->
                    mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
                        style.localizeLabels(Locale("es"))
                    }
                }
            }
        )
        LazyColumn(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Ubica la lista en la parte inferior
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.8f)) // Fondo semitransparente para mayor visibilidad
        ) {
            items(items) { ruta ->
                Text(
                    text = ruta.getNombre(),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}





