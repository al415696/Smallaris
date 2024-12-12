package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

//@HiltViewModel
class UsuarioViewModel() : ViewModel() {
    constructor(cosaUsuario : Int) : this() {
        this.cosaUsuario = cosaUsuario
    }
    var cosaUsuario by mutableStateOf(1)
    companion object{
        val Saver: Saver<UsuarioViewModel, *> = listSaver(
            save = { listOf(it.cosaUsuario)},
            restore = {
                UsuarioViewModel(
                    cosaUsuario = it[0]
                )
            }
        )
    }
}