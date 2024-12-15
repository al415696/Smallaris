package es.uji.smallaris.ui.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ServicioVehiculos
import es.uji.smallaris.model.TipoVehiculo
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
    var items: MutableState<List<Vehiculo>> = mutableStateOf(emptyList())

    var cosaVehiculos by mutableStateOf(1)
    fun hacerCosa(){
        cosaVehiculos += 1
    }
    suspend fun addVehiculo(nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo){
        try {
            servicioVehiculos.addVehiculo(nombre, consumo, matricula, tipo)
            updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    suspend fun updateList(){
        items.value = servicioVehiculos.getVehiculos()
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