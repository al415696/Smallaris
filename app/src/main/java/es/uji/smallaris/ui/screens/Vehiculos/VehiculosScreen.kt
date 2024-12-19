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
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.model.OrdenVehiculo
import kotlin.enums.EnumEntries

@Composable
fun VehiculosScreen(
    viewModel: VehiculosViewModel,
    testFunction: () -> Unit
) {
    val modifier: Modifier = Modifier
    val items = viewModel.items
    val currentContent = remember { mutableStateOf(VehiculoScreenContent.Lista)}
    val currentUpdatedVehiculo: MutableState<Vehiculo> = remember { mutableStateOf(
        if (items.isEmpty())    Vehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)

        else items[0]

        )}
    var currentOrderIndex: Int = 0


    Surface(color = MaterialTheme.colorScheme.primary) {
        println("items "+ items)

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
                    println(currentOrderIndex)
                    println(OrdenVehiculo.entries[currentOrderIndex].toString())
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
enum class ArquetipoVehiculo(){
    Combustible{
        private val members :List<TipoVehiculo> = listOf(TipoVehiculo.Gasolina95,TipoVehiculo.Gasolina98,TipoVehiculo.Diesel)
        private val unidad: String = "L/100km"
        override fun getAllOfArquetipo(): List<TipoVehiculo> {
            return members
//            var test = TipoVehiculo.entries.toMutableList()
//            test.removeAll(listOf(TipoVehiculo.Electrico, TipoVehiculo.Pie, TipoVehiculo.Bici, TipoVehiculo.Desconocido))
//            return test


        }

        override fun getUnidad(): String {
            return unidad
        }
    },
    Electrico{
        private val members :List<TipoVehiculo> = listOf(TipoVehiculo.Electrico)
        private val unidad: String = "kWh/100 km"
        override fun getAllOfArquetipo(): List<TipoVehiculo> {
            return members


        }

        override fun getUnidad(): String {
            return unidad
        }
    },
    Otro{
        private val members :List<TipoVehiculo> = listOf(
            TipoVehiculo.Pie,
            TipoVehiculo.Bici,
            TipoVehiculo.Desconocido
        )
        private val unidad: String = "Cal"

        override fun getAllOfArquetipo(): List<TipoVehiculo> {
            return members
        }

        override fun getUnidad(): String {
            return unidad
        }
    };
    fun classify(tipoVehiculo: TipoVehiculo): ArquetipoVehiculo{

        return if (tipoVehiculo in Electrico.getAllOfArquetipo())
            Electrico
        else if (tipoVehiculo in Otro.getAllOfArquetipo()
        )
            Otro
        else
            Combustible
    }
    abstract fun getUnidad(): String
    fun getUnidad(tipoVehiculo: TipoVehiculo): String{
        for (arc in ArquetipoVehiculo.entries){
            if (tipoVehiculo in arc.getAllOfArquetipo())
                return arc.getUnidad()
        }
        return ""
    }

    abstract fun getAllOfArquetipo(): List<TipoVehiculo>
}
//enum class OrdenLugarInteres{
//    FAVORITO_THEN_NOMBRE{
//        override fun comparator(): Comparator<LugarInteres>{
//            return compareBy<LugarInteres>{
//                if (it.isFavorito()) 0 else 1
//            }.thenBy{
//                it.nombre
//            }
//        }
//    },
//    NOMBRE{
//        override fun comparator(): Comparator<LugarInteres>{
//            return compareBy<LugarInteres>{
//                it.nombre
//            }
//        }
//    };
//    abstract fun comparator(): Comparator<LugarInteres>
//}