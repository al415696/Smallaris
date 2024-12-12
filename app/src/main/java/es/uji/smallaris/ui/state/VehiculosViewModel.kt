package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

//@HiltViewModel
class VehiculosViewModel() : ViewModel() {
    constructor(cosaVehiculo : Int) : this() {
        this.cosaVehiculos = cosaVehiculo
    }
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