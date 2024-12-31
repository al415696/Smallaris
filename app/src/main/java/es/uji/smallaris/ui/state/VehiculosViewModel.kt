package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.OrdenVehiculo
import es.uji.smallaris.model.RouteException
import es.uji.smallaris.model.ServicioVehiculos
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.VehicleException
import es.uji.smallaris.model.Vehiculo

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
        catch (e: VehicleException) {

            return e.message ?: "Fallo con el vehículo, no se ha añadido"
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
            if(servicioVehiculos.setVehiculoFavorito(vehiculo, favorito))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun deleteVehiculo(vehiculo: Vehiculo): String{
        try {
            if(servicioVehiculos.deleteVehiculo(vehiculo))
                updateList()
            return ""
        }
        catch (e: ConnectionErrorException) {
            return "Error al conectarse con el servidor"
        }
        catch (e: VehicleException) {
            return e.message?: "Error con el vehiculo"
        }
        catch (e: RouteException) {
            return e.message?: "Se usa en alguna ruta, no se puede borrar"
        }
        catch (e: Exception) {
            return e.message?:"Fallo inesperado, prueba con otro momento"
        }
    }
    suspend fun debugFillList(){

        var c: Char = 'A'
        while (c <= 'E') {
            servicioVehiculos.addVehiculo(c.toString(), 45.458,  "1234$c$c$c", TipoVehiculo.Gasolina95)
            ++c
        }

    }
    suspend fun initializeList(){
        servicioVehiculos.updateVehiculos()
        updateList()
    }

    suspend fun updateList(){
        // Step 1: Add missing elements
        val nueva = servicioVehiculos.getVehiculos()
        nueva.forEach { element ->
            if (!items.contains(element)) {
                items.add(element)
            }
        }

        // Step 2: Remove extra elements
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!nueva.contains(element)) {
                iterator.remove()
            }
        }

        // Step 3: Rearrange elements
        sortItems()
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