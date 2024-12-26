package es.uji.smallaris.model

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlin.Throws

class ServicioUsuarios(private val repositorioUsuarios: RepositorioUsuarios) {

    @Throws(UserAlreadyExistsException::class, ConnectionErrorException::class)
    suspend fun registrarUsuario(correo: String, contrasena: String): Usuario {

        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible.")

        try {
            // Primero, registramos al usuario
            val usuario = repositorioUsuarios.registrarUsuario(correo, contrasena)

            // Retornamos el usuario que está autenticado
            return usuario
        } catch (e: FirebaseAuthWeakPasswordException) {
            throw Exception("La contraseña es demasiado débil.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("El correo electrónico tiene un formato inválido.")
        } catch (e: FirebaseAuthUserCollisionException) {
            throw UserAlreadyExistsException("El correo electrónico ya está registrado.")
        } catch (e: FirebaseNetworkException) {
            throw Exception("Problema de conexión a Internet.")
        } catch (e: Exception) {
            throw Exception("Error desconocido: ${e.localizedMessage}")
        }
    }

    @Throws(UnregisteredUserException::class, ConnectionErrorException::class)
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {

        // Comprobación de conexión a Firebase
        if ( !repositorioUsuarios.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible.")

        try {
            return repositorioUsuarios.iniciarSesion(correo, contrasena)
        } catch (e: FirebaseAuthInvalidUserException) {
            throw UnregisteredUserException("El usuario no está registrado.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw UnregisteredUserException("Credenciales inválidas. ${e.errorCode}")
        } catch (e: FirebaseAuthException) {
            throw Exception("Error en la autenticación: ${e.localizedMessage}")
        } catch (e: Exception) {
            throw Exception("Error inesperado: ${e.localizedMessage}")
        }
    }

    @Throws(UnloggedUserException::class, ConnectionErrorException::class)
    suspend fun cerrarSesion(): Usuario {
        if (!repositorioUsuarios.enFuncionamiento()) {
            throw ConnectionErrorException("Firebase no está disponible.")
        }

        try {
            return repositorioUsuarios.cerrarSesion()
        } catch (e: FirebaseAuthException) {
            throw UnloggedUserException("Error inesperado: ${e.localizedMessage}")
        }
    }

    fun obtenerUsuarioActual(): FirebaseUser? {
        return repositorioUsuarios.obtenerUsuarioActual()
    }
}