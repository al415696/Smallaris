package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

//@HiltViewModel
class LugaresViewModel() : ViewModel() {
    constructor(cosaLugares : Int) : this() {
        this.cosaLugares = cosaLugares
    }
    var cosaLugares by mutableStateOf(1)
    companion object{
        val Saver: Saver<LugaresViewModel, *> = listSaver(
            save = { listOf(it.cosaLugares)},
            restore = {
                LugaresViewModel(
                    cosaLugares = it[0]
                )
            }
        )
    }
}