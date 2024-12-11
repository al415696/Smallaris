package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

//@HiltViewModel
class RutasViewModel() : ViewModel() {
    constructor(cosaRutas : Int) : this() {
        this.cosaRutas = cosaRutas
    }
    var cosaRutas by mutableStateOf(1)
    companion object{
        val Saver: Saver<RutasViewModel, *> = listSaver(
            save = { listOf(it.cosaRutas)},
            restore = {
                RutasViewModel(
                    cosaRutas = it[0]
                )
            }
        )
    }
}