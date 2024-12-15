package es.uji.smallaris.ui.screens.Vehiculos


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.state.VehiculosViewModel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun VehiculosScreen(
    viewModel: VehiculosViewModel,
    testFunction: () -> Unit
) {
    val modifier: Modifier = Modifier
    val items by viewModel.items
    val currentContent = remember { mutableStateOf(VehiculoScreenContent.Lista)}
    val currentUpdatedVehiculo: MutableState<Vehiculo> = remember { mutableStateOf(
        if (items.isEmpty())    Vehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)

        else items[0]

        )}

    Surface(color = MaterialTheme.colorScheme.primary) {
        println("items "+ items)

        when(currentContent.value){
            VehiculoScreenContent.Lista -> VehiculosListContent(
                modifier = modifier,
                items = items,
                updateFunction = { vehiculo ->
                    currentContent.value = VehiculoScreenContent.Update
                    currentUpdatedVehiculo.value = vehiculo
                },
                favoriteFuncion = {vehiculo: Vehiculo, favorito: Boolean ->  viewModel.setVehiculoFavorito(vehiculo, favorito) },
                addFunction = {currentContent.value = VehiculoScreenContent.Add},
                deleteFuncition = {vehiculo: Vehiculo ->  viewModel.deleteVehiculo(vehiculo) }
                )
            VehiculoScreenContent.Add -> VehiculosAddContent(
                funAddVehiculo = {nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo ->
                    viewModel.addVehiculo(nombre, consumo, matricula, tipo)},
                testViewModel = viewModel,
                onBack = {currentContent.value = VehiculoScreenContent.Lista }
            )
            VehiculoScreenContent.Update -> VehiculosUpdateContent(
                viejoVehiculo = currentUpdatedVehiculo.value,
                funUpdateVehiculo = {viejo:Vehiculo, nuevo:Vehiculo ->  },
                onBack = {currentContent.value = VehiculoScreenContent.Lista }

            )
        }
    }


}

private enum class VehiculoScreenContent(){
    Lista,
    Add,
    Update
}
private enum class ArquetipoVehiculo(){
    Combustible,
    Electrico,
    Otro
}