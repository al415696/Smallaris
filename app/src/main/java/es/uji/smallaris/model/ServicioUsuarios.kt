package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseUser
import kotlin.Throws

class ServicioUsuarios(private val repositorioUsuarios: RepositorioUsuarios) {

    @Throws(UserAlreadyExistsException::class, ConnectionErrorException::class)
    suspend fun registrarUsuario(correo: String, contrasena: String): Usuario {

        // Comprobación de conexión a Firebase
        if ( !repositorioUsuarios.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible.")

        return repositorioUsuarios.registrarUsuario(correo, contrasena)
    }

    @Throws(UnregisteredUserException::class, ConnectionErrorException::class)
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {

        // Comprobación de conexión a Firebase
        if ( !repositorioUsuarios.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible.")

        return repositorioUsuarios.iniciarSesion(correo, contrasena)
    }

    @Throws(UnloggedUserException::class, ConnectionErrorException::class)
    suspend fun cerrarSesion(): Usuario {
        if (!repositorioUsuarios.enFuncionamiento()) {
            throw ConnectionErrorException("Firebase no está disponible.")
        }

        return repositorioUsuarios.cerrarSesion()
        try {
            return repositorioUsuarios.cerrarSesion()
        } catch (e: FirebaseAuthException) {
            throw UnloggedUserException("Error inesperado: ${e.localizedMessage}")
        }
    }

    fun obtenerUsuarioActual(): FirebaseUser? {
        return repositorioUsuarios.obtenerUsuarioActual()
    }

    suspend fun borrarUsuario(): Usuario {
        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento()) {
            throw ConnectionErrorException("Firebase no está disponible.")
        }

        return repositorioUsuarios.borrarUsuario()
    }
}