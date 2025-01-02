package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.ServicioUsuarios
import es.uji.smallaris.model.ServicioVehiculos
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.UnloggedUserException
import es.uji.smallaris.model.UnregisteredUserException
import es.uji.smallaris.model.UserAlreadyExistsException
import es.uji.smallaris.model.UserException
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.model.WrongPasswordException

//@HiltViewModel
class UsuarioViewModel() : ViewModel() {
    private val servicioUsuarios: ServicioUsuarios = ServicioUsuarios.getInstance()

    val servicioVehiculos:ServicioVehiculos = ServicioVehiculos.getInstance()

    suspend fun getVehiculos(): List<Vehiculo> {
        return servicioVehiculos.getVehiculos()
    }

    var sesionIniciada by mutableStateOf(servicioUsuarios.obtenerUsuarioActual() != null)

    private fun updateSesion(){
        sesionIniciada = servicioUsuarios.obtenerUsuarioActual() != null
    }

    suspend fun iniciarSesion(email:String,passwd: String): String{
        try {
           servicioUsuarios.iniciarSesion(email,passwd)
            updateSesion()
//            sesionIniciada = true
        } catch (e: ConnectionErrorException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: UnregisteredUserException) {
            return e.message?: "Usuario no registrado"
        }catch (e: WrongPasswordException) {
            return e.message?: "Usuario no registrado o contraseña errónea"
        } catch (e: Exception) {
            updateSesion()
            return e.message?: "Error inesperado, inicio de sesión cancelado"
        }
        return ""
    }
    suspend fun registrar(email:String,passwd: String): String{
        try {
            servicioUsuarios.registrarUsuario(email,passwd)
//            sesionIniciada = true
            updateSesion()
        } catch (e: ConnectionErrorException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: UserAlreadyExistsException) {
            return "El correo que has introducido ya está en uso, introduce otro"
        }catch (e: UserException) {
            return e.message?: "Credenciales inválidos"
        }catch (e: Exception) {
            updateSesion()
            return e.message?: "Error inesperado, registro no completado"
        }
        return ""
    }
    suspend fun cerrarSesion(): String{
        try {
            servicioUsuarios.cerrarSesion()
//            sesionIniciada = false
            updateSesion()
        } catch (e: ConnectionErrorException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: UnloggedUserException) {
            updateSesion()
            return "No hay ninguna sesión iniciada que cerrar"
        }catch (e: Exception) {
            updateSesion()
            return e.message?: "Error inesperado, no se ha cerrado sesión"
        }
        return ""
    }
    suspend fun eliminarCuenta(): String{
        try {
            servicioUsuarios.borrarUsuario()
//            sesionIniciada = false
            updateSesion()
        } catch (e: ConnectionErrorException) {
            return "No se puede establecer conexión con el servidor, vuelve a intentarlo más tarde"
        }catch (e: UnloggedUserException) {
            updateSesion()
            return "No hay ninguna sesión iniciada que borrar"
        }catch (e: Exception) {
            updateSesion()
            return e.message?: "Error inesperado, no se ha borrado"
        }
        return ""
    }
    suspend fun getDefaultVehiculo(): Vehiculo?{
        return try {
            servicioUsuarios.obtenerVehiculoPorDefecto()
        } catch (e: Exception) {
            null
        }
    }
    suspend fun setDefaultVehiculo(vehiculo: Vehiculo?): Boolean{
        return try {

            if (vehiculo != null) {
                servicioUsuarios.establecerVehiculoPorDefecto(vehiculo)
            }
            else false
        } catch (e: Exception) {
            false
        }
    }
    suspend fun getDefaultTipoRuta(): TipoRuta?{
        return try {
            servicioUsuarios.obtenerTipoRutaPorDefecto()
        } catch (e: Exception) {
            null
        }
    }
    suspend fun setDefaultTipoRuta(tipoRuta: TipoRuta?): Boolean{
        return try {

            if (tipoRuta != null) {
                servicioUsuarios.establecerTipoRutaPorDefecto(tipoRuta)
            }
            else false
        } catch (e: Exception) {
            false
        }
    }

    fun getNombreUsuarioActual(): String{
        return try {
            servicioUsuarios.obtenerUsuarioActual()?.email ?: "Tu cuenta principal"
        } catch (e: ConnectionErrorException) {
            "Tu cuenta principal?"
        }catch (e: Exception) {
            "Tu cuenta principal??"
        }
    }


    companion object{
        val Saver: Saver<UsuarioViewModel, *> = listSaver(
            save = { listOf<Any>()},
            restore = {
                UsuarioViewModel(
                )
            }
        )
    }
}