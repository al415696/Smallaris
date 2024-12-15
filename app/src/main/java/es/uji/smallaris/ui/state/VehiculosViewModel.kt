package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.Vehiculo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//@HiltViewModel
class VehiculosViewModel() : ViewModel() {
    constructor(cosaVehiculo : Int) : this() {
        this.cosaVehiculos = cosaVehiculo
    }
    // Lista observable
    private val _items = MutableStateFlow<List<Vehiculo>>(emptyList())
    val items: StateFlow<List<Vehiculo>> = _items

    var cosaVehiculos by mutableStateOf(1)
    fun hacerCosa(){
        cosaVehiculos += 1
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