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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import es.uji.smallaris.R
import es.uji.smallaris.model.ErrorCategory
import es.uji.smallaris.ui.components.DecimalInputField
import es.uji.smallaris.ui.components.FilteredTextField
import es.uji.smallaris.ui.components.CoordinateDecimalFormatter
import es.uji.smallaris.ui.components.LoadingCircle
import es.uji.smallaris.ui.screens.Vehiculos.toCleanString
import java.util.Locale


@Composable
fun LugaresAddContent(
    funAddLugar: suspend (longitud: Double, latitud: Double, nombre: String) -> String,
    funConseguirToponimos: suspend (longitud: Double, latitud: Double) -> Pair<ErrorCategory, String> = { _, _ ->
        Pair(
            ErrorCategory.NotAnError,
            ""
        )
    },
    funConseguirCoordenadas: suspend (toponimo: String) -> Pair<ErrorCategory,Pair<Double,Double>> = {Pair(ErrorCategory.NotAnError, Pair(-999.9,-999.99))},
    onBack: () -> Unit = {}
) {
    val tempLongitud = remember { mutableStateOf("") }
    val tempLatitud = remember { mutableStateOf("") }
    val reasonForInvalidLatitud by remember {
        derivedStateOf {
            if (tempLatitud.value.isEmpty())
                "Necesaria"
            else if (tempLatitud.value.safeToDouble() < -90)
                "Debe ser > -90º"
            else if (tempLatitud.value.safeToDouble() > 90)
                "Debe ser < 90º"
            else
                ""
        }
    }

    val reasonForInvalidLongitud by remember {
        derivedStateOf {
            if (tempLongitud.value.isEmpty())
//                "Se necesita una longitud"
                "Necesaria"
            else if (tempLongitud.value.safeToDouble() < -180)
//                    "Debe ser mayor de -180º"
                "Debe ser > -180º"
            else if (tempLongitud.value.safeToDouble() > 180)
//                    "Debe ser menor de 180º"
                "Debe ser < 180º"
            else
                ""
        }

    }
    val checkValidCoordinates : (latitud: Double, longitud: Double) -> Boolean =
        { latitud: Double, longitud: Double ->
            latitud >= -90 && latitud <= 90 && longitud >= -180 && longitud <= 180
        }
    val finalLongitud = remember{ mutableDoubleStateOf(-999.9)}
    val finalLatitud = remember{ mutableDoubleStateOf(-999.9)}

    val finalCoordinatesValid = remember { derivedStateOf {
        checkValidCoordinates(finalLatitud.doubleValue, finalLongitud.doubleValue)
    }}



    val setLongLat : (latitud: Double, longitud: Double) -> Unit =
        { latitud: Double, longitud: Double ->
            finalLatitud.doubleValue = latitud
            finalLongitud.doubleValue = longitud
        }

    val toponimo = remember { mutableStateOf("")}
    val toponimoValid = remember { mutableStateOf(true)}

    val showAddDialogue = remember { mutableStateOf(false) }

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
    val updateMap = {
        longitud: Double,latitud: Double, -> if (checkValidCoordinates(latitud, longitud)){
            setLongLat(latitud, longitud)
            tempLatitud.value = latitud.toCleanString()
            tempLongitud.value = longitud.toCleanString()

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
    }

    val opcionesAddLugar = remember { mutableStateOf(OpcionesAddLugar.Toponimo) }
    BackHandler {
        onBack()
    }
    if (showAddDialogue.value) {
        AddAlertDialogue(
            shouldShowDialog =  showAddDialogue,
            addFuncition = {optionalName: String ->  funAddLugar(finalLongitud.doubleValue,finalLatitud.doubleValue,optionalName)},
            longitud = finalLongitud.doubleValue,
            latitud = finalLatitud.doubleValue,
            toponimo = toponimo.value,
            onBack = onBack
        )
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
            Surface(
                modifier = Modifier,
                color = MaterialTheme.colorScheme.secondary
            ) {
                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {

                    IconButton(onClick = onBack, modifier = Modifier.size(30.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.default_description_text),
                            modifier = Modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(5.dp)
                    .weight(1f),
                contentAlignment = Alignment.TopCenter
            ) {
                MapboxMap(
                    modifier= Modifier.fillMaxSize(),//width(100.dp).height(600.dp),
                    mapViewportState = mapboxMapState,
                    compass = {},
                    logo = {},
                    scaleBar = {},
                    attribution = {}
                )
                Column(
                    modifier = Modifier.padding(vertical = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Nombre
                    OpcionesAddLugarWindows(
                        opcionActual = opcionesAddLugar,
                        longitud = tempLongitud,
                        latitud = tempLatitud,
                        toponimo = toponimo,
                        toponimoValid = toponimoValid,
                        updateMap= updateMap,
                        reasonInvalidLongitud = reasonForInvalidLongitud,
                        reasonInvalidLatitude = reasonForInvalidLatitud,
                        funConseguirToponimos = funConseguirToponimos,
                        funConseguirCoordenadas = funConseguirCoordenadas


                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.fillMaxSize(),
                    enabled = finalCoordinatesValid.value,
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
                        text = "Añadir",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}

@Composable
fun OpcionesAddLugarWindows(
    opcionActual: MutableState<OpcionesAddLugar>,
    longitud: MutableState<String>,
    latitud: MutableState<String>,
    toponimo: MutableState<String>,
    toponimoValid: MutableState<Boolean>,
    reasonInvalidLongitud: String = "",
    reasonInvalidLatitude: String = "",
    updateMap: (longitud: Double, latitud: Double) -> Unit = {_,_ ->},
    funConseguirToponimos: suspend (longitud: Double, latitud: Double) -> Pair<ErrorCategory, String> = { _, _ -> Pair(ErrorCategory.NotAnError, "") },
    funConseguirCoordenadas: suspend (toponimo: String) -> Pair<ErrorCategory,Pair<Double,Double>> = {Pair(ErrorCategory.NotAnError, Pair(-999.9,-999.99))},
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        tonalElevation = 55.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            when (opcionActual.value) {
                OpcionesAddLugar.Toponimo ->
                    OpcionAddToponimo(
                        toponimo = toponimo,
                        toponimoValid = toponimoValid,
                        updateMap= updateMap,
                        funSwitch = {opcionActual.value = OpcionesAddLugar.Coordenadas},
                        funConseguirCoordenadas = funConseguirCoordenadas
                    )

                OpcionesAddLugar.Coordenadas ->
                    OpcionAddCoordenadas(
                        longitud = longitud,
                        latitud = latitud,
                        toponimo= toponimo,
                        reasonInvalidLongitud = reasonInvalidLongitud,
                        reasonInvalidLatitude = reasonInvalidLatitude,
                        funConseguirToponimos = funConseguirToponimos,
                        updateMap= updateMap,
                        funSwitch = {opcionActual.value = OpcionesAddLugar.Toponimo}

                    )

            }
        }

    }

}

@Composable
private fun OpcionAddCoordenadas(
    longitud: MutableState<String>,
    latitud: MutableState<String>,
    toponimo: MutableState<String>,
    reasonInvalidLongitud: String = "",
    reasonInvalidLatitude: String = "",
    updateMap: (longitud: Double, latitud: Double) -> Unit = {_,_ ->},
    funSwitch: () -> Unit = {},
    funConseguirToponimos: suspend (longitud: Double, latitud: Double) -> Pair<ErrorCategory, String> = { _, _ -> Pair(ErrorCategory.NotAnError, "") },
    ) {
    var loadingToponimo: Boolean by remember { mutableStateOf(false) }
    var foundToponimo by remember { mutableStateOf(Pair(ErrorCategory.NotAnError, "")) }

    if (loadingToponimo)
        LaunchedEffect(Unit) {
            updateMap(longitud.value.safeToDouble(), latitud.value.safeToDouble())
            foundToponimo =
                funConseguirToponimos(longitud.value.safeToDouble(), latitud.value.safeToDouble())
            if (foundToponimo.first == ErrorCategory.NotAnError)
                toponimo.value = foundToponimo.second
            loadingToponimo = false
        }
    Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = funSwitch) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Filled.ChangeCircle,
                    contentDescription = stringResource(R.string.default_description_text)
                )
            }
            DecimalInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                text = latitud,
                decimalFormatter = CoordinateDecimalFormatter(),
                maxLenght = 9,
                useVisualTransformation = false,
                supportingText = {
                    if (reasonInvalidLatitude.isNotEmpty())
                        Text(
                            text = reasonInvalidLatitude,
                            color = MaterialTheme.colorScheme.error,
//                            style = MaterialTheme.typography.bodySmall
                        )
                }
            ) {
                Text(
                    text = "Latitud",
                    style = MaterialTheme.typography.labelSmall
                )
            }


            VerticalDivider()
            DecimalInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                text = longitud,
                decimalFormatter = CoordinateDecimalFormatter(),
                maxLenght = 9,
                useVisualTransformation = false,
                supportingText = {
                    if (reasonInvalidLongitud.isNotEmpty())
                        Text(
                            text = reasonInvalidLongitud,
                            color = MaterialTheme.colorScheme.error,
//                            style = MaterialTheme.typography.bodySmall
                        )
                }
            ) {
                Text(
                    text = "Longitud",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(
                onClick = { loadingToponimo = true },
                enabled = reasonInvalidLatitude.isEmpty() && reasonInvalidLongitud.isEmpty()
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.default_description_text)
                )
            }

        }
        if (foundToponimo.second.isNotEmpty()) {
            if (foundToponimo.first == ErrorCategory.NotAnError)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        modifier = Modifier.padding(15.dp),
                        text = foundToponimo.second,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge

                    )
                }
            else
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
                        text = foundToponimo.second,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge

                    )
                }

        }
        if (loadingToponimo)
            LoadingCircle()
    }
}

@Composable
private fun OpcionAddToponimo(
    toponimo: MutableState<String>,
    toponimoValid: MutableState<Boolean>,
    funSwitch: () -> Unit = {},
    updateMap: (longitud: Double, latitud: Double) -> Unit = {_,_ ->},
    funConseguirCoordenadas: suspend (toponimo: String) -> Pair<ErrorCategory,Pair<Double,Double>> = {Pair(ErrorCategory.NotAnError, Pair(-999.9,-999.99))},
    ) {
    var loadingToponimo: Boolean by remember { mutableStateOf(false) }

    var searchToponimoResults by remember { mutableStateOf(Pair(ErrorCategory.NotAnError, Pair(-999.9,-999.99))) }
    var foundInitialized: Boolean by remember{ mutableStateOf(false)}
    val foundToponimo by remember { derivedStateOf{
        if (foundInitialized)
            searchToponimoResults.first == ErrorCategory.NotAnError
        else true
    } }
    if (loadingToponimo)
        LaunchedEffect(Unit) {
            println("Begins load")
            searchToponimoResults = funConseguirCoordenadas(toponimo.value)
            println("Finished toponimo")
            foundInitialized = true
            if (foundToponimo) {
                println("Found")
                updateMap(searchToponimoResults.second.first, searchToponimoResults.second.second)
            }
            println("Finished load")
            loadingToponimo = false
        }
    Column(modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = funSwitch) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Filled.ChangeCircle,
                    contentDescription = stringResource(R.string.default_description_text)
                )
            }
            FilteredTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(IntrinsicSize.Min),
                text = toponimo,
                valid = toponimoValid,
                filter = { input: String -> if (input.isEmpty()) "Necesario" else "" },
                label = "Topónimo ubicación"
            )

            IconButton(
                onClick = { loadingToponimo = true },
                enabled = toponimoValid.value
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.default_description_text)
                )
            }
        }
        if (!foundToponimo) {
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
        if (loadingToponimo){
            LoadingCircle()
        }
    }
}




@Preview
@Composable
private fun previewLugaresInteresAddContent() {
    LugaresAddContent(funAddLugar = { _, _, _ -> "" })
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun OpcionesAddLugarCoordenadasConError() {
    OpcionesAddLugarWindows(
        opcionActual = mutableStateOf(OpcionesAddLugar.Coordenadas),
        longitud = mutableStateOf(""),
        latitud = mutableStateOf(""),
        toponimo = mutableStateOf(""),
        toponimoValid = mutableStateOf(false),
        reasonInvalidLongitud = "Necesaria",
        reasonInvalidLatitude = "Debe ser > -180"
    )
}
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun OpcionesAddLugarCoordenadasSinError() {
    OpcionesAddLugarWindows(
        opcionActual = mutableStateOf(OpcionesAddLugar.Coordenadas),
        longitud = mutableStateOf(""),
        latitud = mutableStateOf(""),
        toponimo = mutableStateOf(""),
        toponimoValid = mutableStateOf(false),
        reasonInvalidLongitud = "",
        reasonInvalidLatitude = ""
    )
}
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun OpcionesAddLugarToponimoConError() {
    OpcionesAddLugarWindows(
        opcionActual = mutableStateOf(OpcionesAddLugar.Toponimo),
        longitud = mutableStateOf(""),
        latitud = mutableStateOf(""),
        toponimo = mutableStateOf(""),
        toponimoValid = mutableStateOf(false),
        reasonInvalidLongitud = "Necesaria",
        reasonInvalidLatitude = "Debe ser > -180"
    )
}@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun OpcionesAddLugarToponimoBurbujaError() {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        contentColor = MaterialTheme.colorScheme.error,
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium,
    ){
        Text(modifier = Modifier.padding(15.dp),
            text= "Ubicación no encontrada para ese topónimo",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
@Composable
fun AddAlertDialogue(
    shouldShowDialog: MutableState<Boolean>,
    addFuncition:suspend (nombre: String) -> String = {""},
    longitud: Double = 0.0,
    latitud: Double = 0.0,
    toponimo: String = "Tu ubicación, donde si no",
    onBack: () -> Unit = {}
) {
    var optionalName = remember { mutableStateOf("")}
    var confirmadoAdd by remember{ mutableStateOf(false) }
    var errorConAdd by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }

    if(confirmadoAdd)
        LaunchedEffect(Unit) {
            mensajeError = addFuncition(optionalName.value)
            confirmadoAdd = false
            errorConAdd = mensajeError.isNotEmpty()
            if (!errorConAdd)
                onBack()
        }
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },

            title = { Text(text = "Se añadirá el lugar de interés con coordenadas\n" +
                    "N $latitud\nE $longitud\n" +
                    "en $toponimo",
                textAlign = TextAlign.Center

            )},
            text = {
                Column(modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    TextField(value = optionalName.value,
                        onValueChange = { optionalName.value = it },
                        placeholder = { Text(text = toponimo) },
                        label = { Text(text = "Nombre para el lugar") },
                        supportingText = { Text(text = "(Deja vacío para que sea el topónimo)") }
                    )

                    if (confirmadoAdd) {
                        Column {
                            Text(text = "Añadiendo...")
                            LoadingCircle(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                    }
                    if (errorConAdd){
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 5.dp),
                                text = mensajeError,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    onClick = {
                        confirmadoAdd = true
                    }
                ) {
                    Text(
                        text = "Confirmar",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        )
    }
}
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun PreviewAddAlertDialogue(){
    AddAlertDialogue(shouldShowDialog = mutableStateOf(true),
        )

}
enum class OpcionesAddLugar() {
    Toponimo, Coordenadas
}

fun String.safeToDouble(): Double {
    if (this.isEmpty() || this == "-")
        return 0.0

    return this.toDouble()
}
fun Double.toCleanString(): String {
    return if (this % 1.0 == 0.0) {
        String.format(Locale.US,"%.0f", this)
    } else {
        this.toString()
    }
}