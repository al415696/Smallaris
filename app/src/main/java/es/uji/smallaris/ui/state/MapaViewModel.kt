package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ErrorCategory
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.UbicationException

//@HiltViewModel
class MapaViewModel() : ViewModel() {
    constructor(cosaMapa : Int) : this() {
        this.cosaMapa = cosaMapa
    }
    var cosaMapa by mutableStateOf(1)

    private val servicioAPI: ServicioAPIs = ServicioAPIs

    suspend fun getToponimo(longitud: Double, latitud: Double):Pair<ErrorCategory,String>{
        try {
            return Pair(ErrorCategory.NotAnError,servicioAPI.getToponimoCercano(longitud, latitud))
        } catch (e: UbicationException) {
            e.printStackTrace()
            return Pair(ErrorCategory.FormatError,"Error: " + e.message)
        }catch (e: Exception){
            e.printStackTrace()
            return Pair(ErrorCategory.NotAnError,"Error inesperado")
        }
    }

    companion object{
        val Saver: Saver<MapaViewModel, *> = listSaver(
            save = { listOf(it.cosaMapa)},
            restore = {
                MapaViewModel(
                    cosaMapa = it[0]
                )
            }
        )
    }
}