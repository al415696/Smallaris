package es.uji.smallaris.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.OrdenVehiculo
import es.uji.smallaris.model.ServicioVehiculos
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.VehicleException
import es.uji.smallaris.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//@HiltViewModel
class VehiculosViewModel() : ViewModel() {
    constructor(cosaVehiculo : Int) : this() {
        this.cosaVehiculos = cosaVehiculo
    }
    val servicioVehiculos:ServicioVehiculos = ServicioVehiculos.getInstance()

    // Lista observable
//    var items: MutableState<List<Vehiculo>> = mutableStateOf(emptyList())
    var items: SnapshotStateList<Vehiculo> = mutableStateListOf<Vehiculo>()
    private var currentSorting: OrdenVehiculo = OrdenVehiculo.FAVORITO_THEN_NOMBRE

    fun sortItems(ordenVehiculo: OrdenVehiculo = OrdenVehiculo.FAVORITO_THEN_NOMBRE){
       currentSorting = ordenVehiculo
        sortItems()
    }
    fun sortItems(){
        items.sortWith(currentSorting.comparator())
    }

    var cosaVehiculos by mutableStateOf(1)
    fun hacerCosa(){
        cosaVehiculos += 1
    }
    suspend fun addVehiculo(nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo): String{
        try {
            servicioVehiculos.addVehiculo(nombre, consumo, matricula, tipo)
            updateList()
        }
        catch (e: ConnectionErrorException) {
            e.printStackTrace()
            return "Error al conectarse con el servidor"
        }
        catch (e: VehicleException){

            return e.message ?: ""
        }
        return ""
    }
    suspend fun updateVehiculo(viejo: Vehiculo,
                               nuevoNombre: String = viejo.nombre,
                               nuevoConsumo: Double = viejo.consumo,
                               nuevaMatricula: String = viejo.matricula,
                               nuevoTipoVehiculo: TipoVehiculo = viejo.tipo): String{
        try {
            if (servicioVehiculos.updateVehiculo(viejo, nuevoNombre,nuevoConsumo, nuevaMatricula, nuevoTipoVehiculo)) {
                updateList()
                return ""
            }
            else{
                return "Fallo inesperado, prueba con otro momento"
            }
        }
        catch (e: ConnectionErrorException) {
            e.printStackTrace()
            return "Error al conectarse con el servidor"
        }
        catch (e: VehicleException){

            return e.message ?: ""
        }
        return ""
    }
    suspend fun setVehiculoFavorito(vehiculo: Vehiculo, favorito: Boolean){
        try {
            if(servicioVehiculos.setFavorito(vehiculo, favorito))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun deleteVehiculo(vehiculo: Vehiculo){
        try {
            if(servicioVehiculos.deleteVehiculo(vehiculo))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun debugFillList(){

        var c: Char = 'A'
        while (c <= 'E') {
            servicioVehiculos.addVehiculo(c.toString(), 45.458,  "1234$c$c$c", TipoVehiculo.Gasolina95)
            ++c
        }

    }

    suspend fun updateList(){
//        items.value = servicioVehiculos.getVehiculos()
        // Step 1: Add missing elements to the items list
        val nueva = servicioVehiculos.getVehiculos()
        nueva.forEach { element ->
            if (!items.contains(element)) {
                items.add(element)
            }
        }

        // Step 2: Remove extra elements from the items list
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!nueva.contains(element)) {
                iterator.remove()
            }
        }

        // Step 3: Rearrange elements in the items list to match the nueva list
        sortItems()
//        val orderMap = nueva.withIndex().associate { it.value to it.index }
//        items.sortBy { orderMap[it] }
    }
    companion object{
        val Saver: Saver<VehiculosViewModel, *> = listSaver(
            save = { listOf(it.cosaVehiculos)},
            restore = {
                VehiculosViewModel(
                    cosaVehiculo = it[0]
                )
            }
        )
    }
}
//saver = Saver