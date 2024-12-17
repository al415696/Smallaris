package es.uji.smallaris.ui.screens.Vehiculos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.components.DecimalFormatter
import es.uji.smallaris.ui.components.DecimalInputField
import es.uji.smallaris.ui.components.EnumDropDown

@Composable
fun VehiculosUpdateContent(
    viejoVehiculo: Vehiculo,
    funUpdateVehiculo: (viejo: Vehiculo, nuevo: Vehiculo) -> Unit,
    onBack: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf(viejoVehiculo.nombre) }
    var tipoVehiculo = remember { mutableStateOf(viejoVehiculo.tipo) }
    var matricula by remember { mutableStateOf(viejoVehiculo.matricula) }
    var consumo = remember { mutableStateOf(viejoVehiculo.consumo.toString()) }

    Surface (
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
            Column(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(15.dp)
                .weight(1f),
                verticalArrangement = Arrangement.spacedBy(45.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Name Input
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre metodo de transporte") }
                )
                EnumDropDown(elegida = tipoVehiculo)
                TextField(
                    value = matricula,
                    onValueChange = { matricula = it },
                    label = { Text("Matricula del vehiculo") }
                )
                DecimalInputField(text = consumo,decimalFormatter =  DecimalFormatter()){
                    Text("Consumo en unidad")
                }


            }
            Column(horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .height(75.dp)
                    .fillMaxWidth()) {
//                Surface(color = MaterialTheme.colorScheme.primaryContainer) {// Submit Button
                Button(
                    modifier = Modifier.fillMaxSize(),
                    enabled = !viejoVehiculo.equals(Vehiculo(nombre, if (consumo.value.isEmpty()) 0.0 else consumo.value.toDouble(), matricula, tipoVehiculo.value)),
                    colors = ButtonColors(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.onSurface,



                        ) ,
                    onClick = {
                        // Handle form submission
                        funUpdateVehiculo(viejoVehiculo, Vehiculo (nombre, if (consumo.value.isEmpty()) 0.0 else consumo.value.toDouble(), matricula, tipoVehiculo.value))
                    }) {
                    Text("Modificar")
                }
//                }
            }
        }
    }
}