package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

//@HiltViewModel
class MapaViewModel() : ViewModel() {
    constructor(cosaMapa : Int) : this() {
        this.cosaMapa = cosaMapa
    }
    var cosaMapa by mutableStateOf(1)

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