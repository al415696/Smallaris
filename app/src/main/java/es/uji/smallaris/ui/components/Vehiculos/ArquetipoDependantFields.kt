package es.uji.smallaris.ui.components.Vehiculos

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.ui.components.StandardDecimalFormatter
import es.uji.smallaris.ui.components.DecimalInputField
import es.uji.smallaris.ui.components.EnumDropDown
import es.uji.smallaris.ui.components.FilteredTextField
import es.uji.smallaris.ui.screens.vehiculos.ArquetipoVehiculo

@Composable
fun ArquetipoDependantFields(
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
                modifier = Modifier.padding(top = 5.dp).width(intrinsicSize = IntrinsicSize.Min),
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
                modifier = Modifier.width(intrinsicSize = IntrinsicSize.Min) ,
                opciones = ArquetipoVehiculo.Combustible.getAllOfArquetipo(),
                elegida = tipoVehiculo
            )
        }

        DecimalInputField(
            modifier = Modifier.width(150.dp),
            text = consumo,
            decimalFormatter = StandardDecimalFormatter(),
            maxLenght = 7
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
        decimalFormatter = StandardDecimalFormatter(),
        maxLenght = 7
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
@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun PreviewArquetipo() {
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
private fun PreviewCombustible() {
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
private fun PreviewElectrico() {
    ArquetipoDependantFields(
        arquetipo =  mutableStateOf(ArquetipoVehiculo.Electrico),
        tipoVehiculo = mutableStateOf(TipoVehiculo.Electrico),
        matricula = mutableStateOf(""),
        matriculaValid = mutableStateOf(false),
        consumo = mutableStateOf("")
    )
}