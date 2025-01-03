package es.uji.smallaris.ui.screens.vehiculos

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.components.ErrorBubble
import es.uji.smallaris.ui.components.FilteredTextField
import es.uji.smallaris.ui.components.LoadingCircle
import es.uji.smallaris.ui.components.TopBackBar
import es.uji.smallaris.ui.components.vehiculos.ArquetipoDependantFields
import es.uji.smallaris.ui.screens.safeToDouble
import es.uji.smallaris.ui.screens.toCleanString

@Composable
fun VehiculosUpdateContent(
    viejoVehiculo: Vehiculo,
    funUpdateVehiculo: suspend (
        viejo: Vehiculo, nuevoNombre: String,
        nuevoConsumo: Double,
        nuevaMatricula: String,
        nuevoTipoVehiculo: TipoVehiculo
    ) -> String = { _, _, _, _, _ -> "" },
    onBack: () -> Unit = {}
) {
    val nombre = rememberSaveable { mutableStateOf(viejoVehiculo.nombre) }
    val nombreValid = rememberSaveable { mutableStateOf(true) }
    val tipoVehiculo = rememberSaveable { mutableStateOf(viejoVehiculo.tipo) }
    val matricula = rememberSaveable { mutableStateOf(viejoVehiculo.matricula) }
    val matriculaValid = rememberSaveable { mutableStateOf(true) }
    val consumo = rememberSaveable { mutableStateOf(viejoVehiculo.consumo.toCleanString()) }

    var confirmadoAdd by rememberSaveable { mutableStateOf(false) }


    val errorText = rememberSaveable { mutableStateOf("") }
    val arquetipo = rememberSaveable { mutableStateOf(viejoVehiculo.tipo.getArquetipo()) }


    BackHandler {
        onBack()
    }
    if (confirmadoAdd) {
        LaunchedEffect(Unit) {
            errorText.value =
                funUpdateVehiculo(
                    viejoVehiculo,
                    nombre.value,
                    consumo.value.safeToDouble(),
                    matricula.value,
                    tipoVehiculo.value
                )


            confirmadoAdd = false
            if (errorText.value.isEmpty())
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
            TopBackBar(onBack)

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
                ArquetipoDependantFields(
                    arquetipo,
                    tipoVehiculo,
                    matricula,
                    matriculaValid,
                    consumo
                )
                if (confirmadoAdd) {
                    Column {
                        Text(
                            modifier = Modifier.align(Alignment.Start),
                            text = "Modificando...",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(Modifier.height(15.dp))
                        LoadingCircle(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }

                ErrorBubble(errorText = errorText)

            }


            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.fillMaxSize(),
                    enabled = nombreValid.value && matriculaValid.value,
                    colors = ButtonColors(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onTertiaryContainer,
                        MaterialTheme.colorScheme.surfaceDim,
                        MaterialTheme.colorScheme.onSurface,
                    ),
                    onClick = {
                        confirmadoAdd = true
                    }) {
                    Text(
                        text = "Modificar",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun PreviewVehiculosUpdateContent() {
    VehiculosUpdateContent(Vehiculo("Test", 12.587, "1234YYY", TipoVehiculo.Gasolina98))
}