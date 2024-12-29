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
import es.uji.smallaris.model.UnloggedUserException
import es.uji.smallaris.model.UnregisteredUserException
import es.uji.smallaris.model.UserAlreadyExistsException
import es.uji.smallaris.model.Vehiculo

//@HiltViewModel
class UsuarioViewModel() : ViewModel() {
    constructor(cosaUsuario : Int) : this() {
        this.cosaUsuario = cosaUsuario
    }
    var cosaUsuario by mutableStateOf(1)
    private val servicioUsuarios: ServicioUsuarios = ServicioUsuarios.getInstance()

    val servicioVehiculos:ServicioVehiculos = ServicioVehiculos.getInstance()

    suspend fun getVehiculos(): List<Vehiculo> {
        return servicioVehiculos.getVehiculos()
    }

    var sesionIniciada by mutableStateOf(servicioUsuarios.obtenerUsuarioActual() != null)

    suspend fun iniciarSesion(email:String,passwd: String): String{
        try {
           servicioUsuarios.iniciarSesion(email,passwd)
            sesionIniciada = true
        } catch (e: ConnectionErrorException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: UnregisteredUserException) {
            return "Usuario no registrado o contraseña errónea"
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
    suspend fun cerrarSesion(): String{
        try {
            servicioUsuarios.cerrarSesion()
            sesionIniciada = false
        } catch (e: ConnectionErrorException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: UnloggedUserException) {
            return "No hay ninguna sesión iniciada que cerrar"
        }catch (e: Exception) {
            return e.message?: "Error inesperado, no se ha cerrado sesión"
        }
        return ""
    }
    suspend fun eliminarCuenta(): String{
        try {
            servicioUsuarios.borrarUsuario()
            sesionIniciada = false
        } catch (e: ConnectionErrorException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: UnloggedUserException) {
            return "No hay ninguna sesión iniciada que borrar"
        }catch (e: Exception) {
            return e.message?: "Error inesperado, no se ha borrado"
        }
        return ""
    }
    fun getNombreUsuarioActual(): String{
        try {
            return servicioUsuarios.obtenerUsuarioActual()?.email ?: "Tu cuenta principal"
        } catch (e: ConnectionErrorException) {
            return "Tu cuenta principal?"
        }catch (e: Exception) {
            return "Tu cuenta principal??"
        }
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