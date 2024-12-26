package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.RepositorioFirebase
import es.uji.smallaris.model.ServicioUsuarios
import es.uji.smallaris.model.ServicioVehiculos
import es.uji.smallaris.model.UnregisteredUserException
import es.uji.smallaris.model.UserAlreadyExistsException

//@HiltViewModel
class UsuarioViewModel() : ViewModel() {
    constructor(cosaUsuario : Int) : this() {
        this.cosaUsuario = cosaUsuario
    }
    var cosaUsuario by mutableStateOf(1)
    val servicioUsuarios: ServicioUsuarios = ServicioUsuarios.getInstance()

    var sesionIniciada by mutableStateOf(servicioUsuarios.obtenerUsuarioActual() != null)

    fun isSesionIniciada(): Boolean{
        return sesionIniciada
    }
    suspend fun iniciarSesion(email:String,passwd: String): String{
        try {
           servicioUsuarios.iniciarSesion(email,passwd)
            sesionIniciada = true
        } catch (e: ConnectionErrorException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: UnregisteredUserException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: Exception) {
            return e.message?: "Error inesperado, inicio de sesión cancelado"
        }
        return ""
    }
    suspend fun registrar(email:String,passwd: String): String{
        try {
            servicioUsuarios.registrarUsuario(email,passwd)
            sesionIniciada = true
        } catch (e: ConnectionErrorException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: UserAlreadyExistsException) {
            return "El correo que has introducido ya está en uso, introduce otro"
        }catch (e: Exception) {
            return e.message?: "Error inesperado, registro no completado"
        }
        return ""
    }

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