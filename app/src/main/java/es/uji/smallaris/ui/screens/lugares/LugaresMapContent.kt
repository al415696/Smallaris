package es.uji.smallaris.ui.screens.lugares

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddLocationAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationState
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import es.uji.smallaris.R
import es.uji.smallaris.model.ErrorCategory
import es.uji.smallaris.ui.components.DecimalInputField
import es.uji.smallaris.ui.components.FilteredTextField
import es.uji.smallaris.ui.components.CoordinateDecimalFormatter
import es.uji.smallaris.ui.components.LoadingCircle
import es.uji.smallaris.ui.components.TopBackBar
import es.uji.smallaris.ui.screens.Vehiculos.toCleanString
import kotlinx.coroutines.launch
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
    val opcionesAddLugar = rememberSaveable { mutableStateOf(OpcionesAddLugar.Toponimo) }
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

            val markerImage = rememberIconImage(
                key = "default_marker",
                painter = painterResource(R.drawable.add_location_alt_24px)// Cambia esto por el icono que prefieras
            )
            MapboxMap(
                modifier = Modifier.fillMaxSize(),//width(100.dp).height(600.dp),
                mapViewportState = mapboxMapState,
                compass = {},
                logo = {},
                scaleBar = {},
                attribution = {},

                content = {
                    marker.let { point ->
                        PointAnnotation(point = point) {
                            iconColor = Color.Red
                            iconImage = markerImage
                            iconSize = 3.5
                            iconOffset = listOf(0.0, -10.0)
                            textColor = Color.Black
                            textSize = 10.0
                            textOffset =
                                listOf(0.0, 1.5) // Ajuste para colocar el texto correctamente
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




@Preview
@Composable
private fun previewLugaresInteresMapContent() {
    LugaresMapContent()
}

