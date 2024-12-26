package es.uji.smallaris.ui.screens.vehiculos


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.state.VehiculosViewModel
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import es.uji.smallaris.model.OrdenVehiculo
import java.util.Locale

@Composable
fun VehiculosScreen(
    viewModel: VehiculosViewModel
) {
    val modifier: Modifier = Modifier
    val items = viewModel.items
    val currentContent = rememberSaveable { mutableStateOf(VehiculoScreenContent.Lista)}
    val currentUpdatedVehiculo: MutableState<Vehiculo> = remember { mutableStateOf(
        if (items.isEmpty())    Vehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)

        else items[0]

        )}
    var currentOrderIndex = 0


    Surface(color = MaterialTheme.colorScheme.primary) {

        when(currentContent.value){
            VehiculoScreenContent.Lista ->
                VehiculosListContent(
                modifier = modifier,
                items = items,
                updateFunction = { vehiculo ->
                    currentContent.value = VehiculoScreenContent.Update
                    currentUpdatedVehiculo.value = vehiculo
                },
                favoriteFuncion = {vehiculo: Vehiculo, favorito: Boolean ->  viewModel.setVehiculoFavorito(vehiculo, favorito) },
                addFunction = {currentContent.value = VehiculoScreenContent.Add},
                sortFunction = {
                    currentOrderIndex = (currentOrderIndex+1) %OrdenVehiculo.entries.size
                    viewModel.sortItems(OrdenVehiculo.entries[currentOrderIndex])
                    OrdenVehiculo.entries[currentOrderIndex].getNombre()
                               },
                deleteFuncition = {vehiculo: Vehiculo ->  viewModel.deleteVehiculo(vehiculo) }
                )
            VehiculoScreenContent.Add ->
                VehiculosAddContent(
                funAddVehiculo = {
                    nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo ->
                    viewModel.addVehiculo(nombre, consumo, matricula, tipo)
                                 },
                onBack = {currentContent.value = VehiculoScreenContent.Lista }
            )
            VehiculoScreenContent.Update ->
                VehiculosUpdateContent(
                viejoVehiculo = currentUpdatedVehiculo.value,
                funUpdateVehiculo = {viejo: Vehiculo, nuevoNombre: String,
                                     nuevoConsumo: Double,
                                     nuevaMatricula: String,
                                     nuevoTipoVehiculo: TipoVehiculo -> viewModel.updateVehiculo(viejo, nuevoNombre, nuevoConsumo, nuevaMatricula, nuevoTipoVehiculo)},
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

fun Double.toCleanString(): String {
    return if (this % 1.0 == 0.0) {
        String.format(Locale.US,"%.0f", this)
    } else {
        this.toString()
    }
}