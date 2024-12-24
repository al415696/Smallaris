package es.uji.smallaris.ui.screens.rutas

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
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
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.localization.localizeLabels
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import es.uji.smallaris.R
import es.uji.smallaris.model.ErrorCategory
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.components.DecimalInputField
import es.uji.smallaris.ui.components.FilteredTextField
import es.uji.smallaris.ui.components.CoordinateDecimalFormatter
import es.uji.smallaris.ui.components.EnumDropDown
import es.uji.smallaris.ui.components.ListDropDown
import es.uji.smallaris.ui.components.LoadingCircle
import es.uji.smallaris.ui.components.TopBackBar
import es.uji.smallaris.ui.screens.vehiculos.toCleanString
import kotlinx.coroutines.launch
import java.util.Locale


@Composable
fun RutasAddContent(
    funAddRuta: suspend (
        nombreRuta: String,
        inicio: LugarInteres,
        fin: LugarInteres,
        vehiculo: Vehiculo,
        tipoRuta: TipoRuta
    ) -> String = { _, _, _, _, _ -> "" },
//    funConseguirToponimos: suspend (longitud: Double, latitud: Double) -> Pair<ErrorCategory, String> = { _, _ ->
//        Pair(
//            ErrorCategory.NotAnError,
//            ""
//        )
//    },
//    funConseguirCoordenadas: suspend (toponimo: String) -> Pair<ErrorCategory, Pair<Double, Double>> = {
//        Pair(
//            ErrorCategory.NotAnError,
//            Pair(-999.9, -999.99)
//        )
//    },
    funConseguirLugares: suspend () -> List<LugarInteres> = { emptyList() },
    funConseguirVehiculos: suspend () -> List<Vehiculo> = { emptyList() },

    onBack: () -> Unit = {}
) {

    val inicio: MutableState<LugarInteres?> = remember { mutableStateOf(null) }
    val destino: MutableState<LugarInteres?> = remember { mutableStateOf(null) }
    var hayLugares by remember { mutableStateOf(true) }

    var listLugares by rememberSaveable { mutableStateOf(emptyList<LugarInteres>()) }

    val vehiculo: MutableState<Vehiculo?> = remember { mutableStateOf(null) }
    var hayVehiculos by remember { mutableStateOf(true) }

    var listVehiculos by rememberSaveable { mutableStateOf(emptyList<Vehiculo>()) }

    var currentTipoRuta = remember { mutableStateOf(TipoRuta.Rapida) }

    val showAddDialogue = rememberSaveable { mutableStateOf(false) }

    val mapboxMapState =
        rememberMapViewportState {
            setCameraOptions {
                zoom(15.0) // Ajusta el nivel de zoom según lo que desees mostrar.
                center(
                    Point.fromLngLat(
                        -0.068547,
                        39.994259
                    )
                ) // Coordenadas de la Universidad Jaume I.
                pitch(0.0)
                bearing(0.0)
            }
        }
    val listaTipoRuta = listOf(TipoRuta.Rapida,TipoRuta.Economica,TipoRuta.Corta)
    var marker by rememberSaveable { mutableStateOf<Point?>(null) }

    val updateMap = { longitud: Double, latitud: Double ->

        marker = Point.fromLngLat(longitud, latitud)
        mapboxMapState.setCameraOptions {
            zoom(15.0) // Ajusta el nivel de zoom según lo que desees mostrar.
            center(
                Point.fromLngLat(longitud, latitud)
            )
            pitch(0.0)
            bearing(0.0)
            center(Point.fromLngLat(longitud, latitud))
        }

    }

//    val opcionesAddRuta = rememberSaveable { mutableStateOf(OpcionesAddRuta.Toponimo) }
    BackHandler {
        onBack()
    }
    if (showAddDialogue.value) {

    }

    LaunchedEffect(Unit) {
        println("Freddy: Empieza")
        listLugares = funConseguirLugares()
        println("Freddy: Acaba")

        if (listLugares.isNotEmpty()) {
            println("Freddy: Encontrado")
            inicio.value = listLugares[0]
            destino.value = listLugares[0]
        } else {
            println("Freddy: No Encontrado")
            hayLugares = false
        }

    }
    LaunchedEffect(Unit) {
        println("Pirate: Empieza")
        listVehiculos = funConseguirVehiculos()
        println("Pirate: Acaba")

        if (listVehiculos.isNotEmpty()) {
            println("Pirate: Encontrado")
            vehiculo.value = listVehiculos[0]
        } else {
            println("Pirate: No Encontrado")
            hayVehiculos = false
        }

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


            val scope = rememberCoroutineScope()
            val markerImage = rememberIconImage(
                key = "default_marker",
                painter = painterResource(R.drawable.add_location_alt_24px)// Cambia esto por el icono que prefieras
            )/*
                MapboxMap(
                    modifier= Modifier.fillMaxSize(),//width(100.dp).height(600.dp),
                    mapViewportState = mapboxMapState,
                    compass = {},
                    logo = {},
                    scaleBar = {},
                    attribution = {},
                    onMapClickListener = OnMapClickListener { point ->
                        // Llamada a la función suspend dentro de una corutina
                        scope.launch {
                            val functionalLongitud = "%.5f".format(point.longitude()).toDouble()
                            val functionalLatitud = "%.5f".format(point.latitude()).toDouble()
                            tempLongitud.value = functionalLongitud.toCleanString()
                            tempLatitud.value = functionalLatitud.toCleanString()
                            val result = funConseguirToponimos(functionalLongitud, functionalLatitud)
                            if (result.first == ErrorCategory.NotAnError){
                                setLongLat(functionalLatitud,functionalLongitud)
                                marker = point
                                toponimo.value = result.second
                            }
                        }
                        true
                    },
                    content = {
                        marker?.let { point ->
                            PointAnnotation(point = point) {
                                iconColor= Color.Red
                                iconImage = markerImage
                                iconSize = 3.5
                                iconOffset = listOf(0.0, -10.0)
                                textColor = Color.Black
                                textSize = 10.0
                                textOffset = listOf(0.0, 1.5) // Ajuste para colocar el texto correctamente
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
                */

            Surface(
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    Column(modifier = Modifier) {
                        Text(text = "Origen")
                        Surface(modifier = Modifier.fillMaxWidth(0.8f)) {
                            ListDropDown(
                                opciones = listLugares,
                                elegida = inicio,
                                shownValue = { objeto: LugarInteres? ->
                                    objeto?.nombre ?: "Cargando..."
                                }
                            )
                        }
                    }

                    Column(modifier = Modifier) {
                        Text(text = "Destino")
                        Surface(modifier = Modifier.fillMaxWidth(0.8f)) {

                            ListDropDown(
                                opciones = listLugares,
                                elegida = destino,
                                shownValue = { objeto: LugarInteres? ->
                                    objeto?.nombre ?: "Cargando..."
                                }
                            )
                        }
                    }
                    if (!hayLugares) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            contentColor = MaterialTheme.colorScheme.error,
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(
                                modifier = Modifier.padding(15.dp),
                                text = "No tienes ningún lugar guardado, así no puedes crear rutas!",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    HorizontalDivider(Modifier.padding(vertical = 20.dp))

                    Column(modifier = Modifier) {
                        Text(text = "Vehiculo")
                        Surface(modifier = Modifier.fillMaxWidth(0.8f)) {

                            ListDropDown(
                                modifier = Modifier.fillMaxWidth(),
                                opciones = listVehiculos,
                                elegida = vehiculo,
                                shownValue = { objeto: Vehiculo? ->
                                    objeto?.nombre ?: "Cargando..."
                                }
                            )
                        }
                    }
                    if (!hayVehiculos) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            contentColor = MaterialTheme.colorScheme.error,
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(
                                modifier = Modifier.padding(15.dp),
                                text = "No tienes ningún vehiculo guardado, así no puedes crear rutas!",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    HorizontalDivider(Modifier.padding(vertical = 20.dp))

                    Column(modifier = Modifier.fillMaxWidth(0.8f),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = "Tipo de ruta"
                        )
                        Surface(modifier = Modifier.fillMaxWidth(0.4f)) {
                            EnumDropDown(
                                opciones = listaTipoRuta,
                                elegida = currentTipoRuta
                            )
                        }
                    }

                }
            }
            Spacer(
                Modifier
                    .fillMaxHeight()
                    .weight(1f))
            Column(
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth()
                    .align(Alignment.End)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.End),
                    colors = ButtonColors(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onSurface,
                    ),
                    onClick = {
                        // Handle form submission
                        showAddDialogue.value = true
                    }) {
                    Text(
                        text = "Calcular",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
//                }
            }
        }


    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun PreviewRutasAddContent() {
    RutasAddContent()
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun OpcionesAddRutaToponimoBurbujaError() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        contentColor = MaterialTheme.colorScheme.error,
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            modifier = Modifier.padding(15.dp),
            text = "Ubicación no encontrada para ese topónimo",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

//@Composable
//fun AddAlertDialogue(
//    shouldShowDialog: MutableState<Boolean>,
//    addFuncition: suspend (nombre: String) -> String = { "" },
//    longitud: Double = 0.0,
//    latitud: Double = 0.0,
//    toponimo: String = "Tu ubicación, donde si no",
//    onBack: () -> Unit = {}
//) {
//    var optionalName = remember { mutableStateOf("") }
//    var confirmadoAdd by remember { mutableStateOf(false) }
//    var errorConAdd by remember { mutableStateOf(false) }
//    var mensajeError by remember { mutableStateOf("") }
//
//    if (confirmadoAdd)
//        LaunchedEffect(Unit) {
//            mensajeError = addFuncition(optionalName.value)
//            confirmadoAdd = false
//            errorConAdd = mensajeError.isNotEmpty()
//            if (!errorConAdd)
//                onBack()
//        }
//    if (shouldShowDialog.value) {
//        AlertDialog(
//            onDismissRequest = {
//                shouldShowDialog.value = false
//            },
//
//            title = {
//                Text(
//                    text = "Se añadirá el ruta de interés con coordenadas\n" +
//                            "N $latitud\nE $longitud\n" +
//                            "en $toponimo",
//                    textAlign = TextAlign.Center
//
//                )
//            },
//            text = {
//                Column(
//                    modifier = Modifier,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    TextField(value = optionalName.value,
//                        onValueChange = { optionalName.value = it },
//                        placeholder = { Text(text = toponimo) },
//                        label = { Text(text = "Nombre para el ruta") },
//                        supportingText = { Text(text = "(Deja vacío para que sea el topónimo)") }
//                    )
//
//                    if (confirmadoAdd) {
//                        Column {
//                            Text(text = "Añadiendo...")
//                            LoadingCircle(modifier = Modifier.align(Alignment.CenterHorizontally))
//                        }
//                    }
//                    if (errorConAdd) {
//                        Surface(
//                            color = MaterialTheme.colorScheme.errorContainer,
//                            contentColor = MaterialTheme.colorScheme.error
//                        ) {
//                            Text(
//                                modifier = Modifier.padding(horizontal = 5.dp),
//                                text = mensajeError,
//                                style = MaterialTheme.typography.titleLarge,
//                            )
//                        }
//                    }
//                }
//            },
//            confirmButton = {
//                Button(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(60.dp),
//                    onClick = {
//                        confirmadoAdd = true
//                    }
//                ) {
//                    Text(
//                        text = "Confirmar",
//                        style = MaterialTheme.typography.headlineLarge
//                    )
//                }
//            }
//        )
//    }
//}


enum class OpcionesAddRuta() {
    Toponimo, Coordenadas
}
