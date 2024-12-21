package es.uji.smallaris.ui.screens.lugares

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R
import es.uji.smallaris.model.ErrorCategory
import es.uji.smallaris.ui.components.DecimalInputField
import es.uji.smallaris.ui.components.EnumDropDown
import es.uji.smallaris.ui.components.FilteredTextField
import es.uji.smallaris.ui.components.LoadingCircle
import es.uji.smallaris.ui.components.CoordinateDecimalFormatter


@Composable
fun LugaresAddContent(
    funAddLugar: suspend (longitud: Double, latitud: Double, nombre: String) -> String,
    funConseguirToponimos: suspend (longitud: Double, latitud: Double) -> Pair<ErrorCategory,String> = {_,_->Pair(ErrorCategory.NotAnError,"")},
    funConseguirCoordenadas: suspend (toponimo: String) -> String = {""},
    onBack: () -> Unit = {}
) {
    var nombre = remember { mutableStateOf("") }
    var nombreValid = remember { mutableStateOf(true) }

    var longitud = remember { mutableStateOf("") }

    var latitud = remember { mutableStateOf("") }


    val reasonForInvalidCoordinates by remember { derivedStateOf {
        val reason = StringBuilder("")
        if (latitud.value.isEmpty())
            reason.append("Se necesita una latitud\n")
        else {
            if (latitud.value.safeToDouble() < -90)
                reason.append("Latitud debe ser mayor de -90\n")
            if (latitud.value.safeToDouble() > 90)
                reason.append("Latitud debe ser menor de 90\n")
        }

        if (longitud.value.isEmpty())
            reason.append("Se necesita una longitud\n")
        else {
            if (longitud.value.safeToDouble() < -180)
                reason.append("Longitud debe ser mayor de -180\n")
            if (longitud.value.safeToDouble() > 180)
                reason.append("Longitud debe ser menor de 180\n")
        }
        reason.removeSuffix("\n").toString()
    }}
    val coordinatesValid : Boolean by remember{ derivedStateOf { reasonForInvalidCoordinates.isEmpty() }}

    var confirmadoAdd by remember { mutableStateOf(false) }

// -4.25880 42.79103
    var mensajeError by remember { mutableStateOf("") }
    var errorConAdd by remember { mutableStateOf(false) }

    var opcionesAddLugar = remember{ mutableStateOf(OpcionesAddLugar.Coordenadas)}
    BackHandler {
        onBack()
    }
    if (confirmadoAdd) {
        LaunchedEffect(Unit) {
            mensajeError = funAddLugar(
                longitud.value.safeToDouble(),
                latitud.value.safeToDouble(),
                nombre.value,
            )
            confirmadoAdd = false
            errorConAdd = mensajeError.isNotEmpty()
            if (!errorConAdd)
                onBack()
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
                .verticalScroll(rememberScrollState())

        ) {
            Surface(modifier = Modifier,
            color= MaterialTheme.colorScheme.secondary) {
                Row(
                    modifier = Modifier
                        .height(55.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,

                    ) {

                    IconButton(onClick = onBack, modifier = Modifier.size(75.dp)) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.default_description_text),
                            modifier = Modifier.fillMaxSize(),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(15.dp)
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(45.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Nombre
                FilteredTextField(
                    text = nombre,
                    valid = nombreValid,
                    label = "Nombre para el lugar de interés"
                )
                OpcionesAddLugarWindows(
                    opcionActual = opcionesAddLugar,
                    longitud = longitud,
                    latitud = latitud,
                    reasonInvalidCoordinates = reasonForInvalidCoordinates,
                    funConseguirToponimos = funConseguirToponimos
                )





                if (confirmadoAdd) {
                    Column {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = "Añadiendo...",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(15.dp))
                        LoadingCircle(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
                if (errorConAdd)
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


            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth()
            ) {
//                Surface(color = MaterialTheme.colorScheme.primaryContainer) {// Submit Button
                Button(
                    modifier = Modifier.fillMaxSize(),
                    enabled = coordinatesValid,
                    colors = ButtonColors(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onSurface,
                    ),
                    onClick = {
                        // Handle form submission
                        confirmadoAdd = true
                    }) {
                    Text(text="Añadir",
                        style = MaterialTheme.typography.headlineLarge)
                }
//                }
            }
        }
    }
}
@Composable
fun OpcionesAddLugarWindows(
    opcionActual: MutableState<OpcionesAddLugar>,
    longitud: MutableState<String>,
    latitud: MutableState<String>,
    reasonInvalidCoordinates: String = "",
    funConseguirToponimos: suspend (longitud: Double, latitud: Double) -> Pair<ErrorCategory,String> = {_,_->Pair(ErrorCategory.NotAnError,"")},
    funConseguirCoordenadas: suspend (toponimo: String) -> String = {""},
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 10.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            EnumDropDown(
                modifier = Modifier.padding(top = 5.dp),
                elegida = opcionActual
            )



            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 55.dp,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    Modifier.padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    when (opcionActual.value) {
                        OpcionesAddLugar.Mapa -> TODO()

                        OpcionesAddLugar.Coordenadas ->
                            OpcionAddCoordenadas(
                                longitud = longitud,
                                latitud= latitud,
                                reasonInvalidCoordinates = reasonInvalidCoordinates,
                                funConseguirToponimos = funConseguirToponimos
                        )

                    }
                }
            }
        }
    }

}
@Composable
private fun OpcionAddCoordenadas(
    longitud: MutableState<String>,
    latitud: MutableState<String>,
    reasonInvalidCoordinates: String = "",
    funConseguirToponimos: suspend (longitud: Double, latitud: Double) -> Pair<ErrorCategory,String> = {_,_->Pair(ErrorCategory.NotAnError,"")},

    ){
    var loadingToponimo: Boolean by remember{ mutableStateOf(false)}
    var foundToponimo by remember{ mutableStateOf(Pair(ErrorCategory.NotAnError,""))}

    if (loadingToponimo)
        LaunchedEffect(Unit) {
            foundToponimo = funConseguirToponimos(longitud.value.safeToDouble(), latitud.value.safeToDouble())
            loadingToponimo = false
        }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)) {
                DecimalInputField(
                    modifier = Modifier.width(150.dp),
                    text = latitud,
                    decimalFormatter = CoordinateDecimalFormatter(),
                    maxLenght = 9,
                    useVisualTransformation = false
                ) {
                    Text(
                        text = "Latitud",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
                DecimalInputField(
                    modifier = Modifier.width(150.dp),
                    text = longitud,
                    decimalFormatter = CoordinateDecimalFormatter(),
                    maxLenght = 9,
                    useVisualTransformation = false
                ) {
                    Text(
                        text = "Longitud",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

            }
            Text(text = reasonInvalidCoordinates,
            color =MaterialTheme.colorScheme.error)
        }
        Button(
            enabled = reasonInvalidCoordinates.isEmpty(),
            modifier =  Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                loadingToponimo = true
            }) {
            Text(text= "Comprobar Ubicación")
        }
        Surface (
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
//                .defaultMinSize(64.dp)
            ,
            tonalElevation = 125.dp,
            shape= MaterialTheme.shapes.small,
//            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            if (loadingToponimo)
                LoadingCircle(
                    Modifier.padding(5.dp).align(Alignment.CenterHorizontally),
//                    size = 13.dp
                )
            else
                Text(
                    text = foundToponimo.second,
                    color =
                    if (foundToponimo.first == ErrorCategory.NotAnError)
                        MaterialTheme.colorScheme.onSecondaryContainer
                    else
                        MaterialTheme.colorScheme.error,

                    style = MaterialTheme.typography.bodyLarge
                )
        }
    }

}

@Preview
@Composable
private fun previewLugaresInteresAddContent() {
    LugaresAddContent(funAddLugar = {_,_,_-> "" })
}
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun OpcionesAddLugarCoordenadas() {
    OpcionesAddLugarWindows(
        opcionActual = mutableStateOf(OpcionesAddLugar.Coordenadas),
        longitud = mutableStateOf(""),
        latitud = mutableStateOf("")
    )
}
enum class OpcionesAddLugar(){
    Mapa, Coordenadas
}

fun String.safeToDouble(): Double {
    if (this.isEmpty() || this == "-")
        return 0.0

    return this.toDouble()

}