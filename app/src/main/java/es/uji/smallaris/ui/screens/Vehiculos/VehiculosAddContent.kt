package es.uji.smallaris.ui.screens.Vehiculos

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.ui.components.DecimalFormatter
import es.uji.smallaris.ui.components.DecimalInputField
import es.uji.smallaris.ui.components.EnumDropDown
import es.uji.smallaris.ui.components.FilteredTextField
import es.uji.smallaris.ui.components.LoadingCircle

@Composable
fun VehiculosAddContent(
    funAddVehiculo: suspend (nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo) -> String,
    onBack: () -> Unit = {}
) {
    var nombre = remember { mutableStateOf("") }
    var nombreValid = remember { mutableStateOf(false) }
    var tipoVehiculo = remember { mutableStateOf(TipoVehiculo.Desconocido) }
    var matricula = remember { mutableStateOf("") }
    var matriculaValid = remember { mutableStateOf(false) }
    var consumo = remember { mutableStateOf("") }

    var confirmadoAdd by remember { mutableStateOf(false) }


    var mensajeError by remember { mutableStateOf("") }
    var errorConAdd by remember { mutableStateOf(false) }
    var arquetipo = remember { mutableStateOf(ArquetipoVehiculo.Combustible) }

    BackHandler {
        onBack()
    }
    if (confirmadoAdd) {
        LaunchedEffect(Unit) {
            mensajeError = funAddVehiculo(
                nombre.value,
                if (consumo.value.isEmpty()) 0.0 else consumo.value.toDouble(),
                matricula.value,
                tipoVehiculo.value
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
                    filter = { input ->
                        if (input.isEmpty())
                            "Tiene que tener un nombre"
                        else
                            ""
                    },
                    label = "Nombre del método de transporte"
                )

                // Elegir arquetipo de vehiculo
                ArquetipoDependantFields(arquetipo, tipoVehiculo, matricula, matriculaValid, consumo)
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
                    enabled = nombreValid.value && matriculaValid.value,
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
private fun ArquetipoDependantFields(
    arquetipo: MutableState<ArquetipoVehiculo>,
    tipoVehiculo: MutableState<TipoVehiculo>,
    matricula: MutableState<String>,
    matriculaValid: MutableState<Boolean>,
    consumo: MutableState<String>
) {
    Surface(

        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 10.dp,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            EnumDropDown(
                modifier = Modifier.padding(top = 5.dp),
                opciones = listOf(
                    ArquetipoVehiculo.Combustible,
                    ArquetipoVehiculo.Electrico
                ),
                elegida = arquetipo
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
                    when (arquetipo.value) {
                        ArquetipoVehiculo.Combustible -> {
                            if (!ArquetipoVehiculo.Combustible.getAllOfArquetipo()
                                    .contains(tipoVehiculo.value)
                            )
                                tipoVehiculo.value = TipoVehiculo.Gasolina95

                            CombustibleExclusiveOptions(
                                tipoVehiculo,
                                matricula,
                                matriculaValid,
                                consumo
                            )
                        }

                        ArquetipoVehiculo.Electrico -> {
                            tipoVehiculo.value = TipoVehiculo.Electrico
                            ElectricoExclusiveOptions(
                                matricula,
                                matriculaValid,
                                consumo
                            )
                        }

                        ArquetipoVehiculo.Otro -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun CombustibleExclusiveOptions(
    tipoVehiculo: MutableState<TipoVehiculo> = mutableStateOf(TipoVehiculo.Gasolina95),
    matricula: MutableState<String> = mutableStateOf(""),
    matriculaValid: MutableState<Boolean> = mutableStateOf(true),
    consumo: MutableState<String> = mutableStateOf(""),

    ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    )
    {
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Tipo de combustible:   ",
                style = MaterialTheme.typography.labelLarge
            )
            EnumDropDown(
                opciones = ArquetipoVehiculo.Combustible.getAllOfArquetipo(),
                elegida = tipoVehiculo
            )
        }

        DecimalInputField(
            modifier = Modifier.width(150.dp),
            text = consumo,
            decimalFormatter = DecimalFormatter(),
            maxLenght = 5
        ) {
            Text(
                text = "Consumo en L/100km",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
    FilteredTextField(
        text = matricula,
        valid = matriculaValid,
        filter = { input ->
            if (input.isEmpty())
                "Tiene que tener una matricula"
            else if (!(input.contains("[0-9][0-9][0-9][0-9][A-Z][A-Z][A-Z]".toRegex()) || input.contains(
                    "[A-Z][0-9][0-9][0-9][0-9][A-Z][A-Z]".toRegex()
                ))
            )
                "La matrícula debe seguir el formato\n LNNNNLL o NNNNLLL"
            else
                ""
        },
        label = "Matrícula del vehículo",
        maxLength = 7
    )


}

@Composable
private fun ElectricoExclusiveOptions(
    matricula: MutableState<String> = mutableStateOf(""),
    matriculaValid: MutableState<Boolean> = mutableStateOf(true),
    consumo: MutableState<String> = mutableStateOf(""),
) {
    DecimalInputField(
        modifier = Modifier.width(175.dp),
        text = consumo,
        decimalFormatter = DecimalFormatter(),
        maxLenght = 5
    ) {
        Text(
            "Consumo en kWh/100 km",
            style = MaterialTheme.typography.labelSmall
        )
    }
    FilteredTextField(
        text = matricula,
        valid = matriculaValid,
        filter = { input ->
            if (input.isEmpty())
                "Tiene que tener una matricula"
            else if (!(input.contains("[0-9][0-9][0-9][0-9][A-Z][A-Z][A-Z]".toRegex()) || input.contains(
                    "[A-Z][0-9][0-9][0-9][0-9][A-Z][A-Z]".toRegex()
                ))
            )
                "La matrícula debe seguir el formato\n LNNNNLL o NNNNLLL"
            else
                ""
        },
        label = "Matrícula del vehículo",
        maxLength = 7
    )


}

@Preview
@Composable
private fun previewVehiculosAddContent() {
    VehiculosAddContent(funAddVehiculo = { nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo -> "" })
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun previewCombustible() {
    ArquetipoDependantFields(
        arquetipo =  mutableStateOf(ArquetipoVehiculo.Combustible),
        tipoVehiculo = mutableStateOf(TipoVehiculo.Gasolina95),
        matricula = mutableStateOf(""),
        matriculaValid = mutableStateOf(false),
        consumo = mutableStateOf("")
    )
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun previewElectrico() {
    ArquetipoDependantFields(
        arquetipo =  mutableStateOf(ArquetipoVehiculo.Electrico),
        tipoVehiculo = mutableStateOf(TipoVehiculo.Electrico),
        matricula = mutableStateOf(""),
        matriculaValid = mutableStateOf(false),
        consumo = mutableStateOf("")
    )
}