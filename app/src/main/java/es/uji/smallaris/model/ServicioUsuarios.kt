package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseUser

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
    suspend fun cerrarSesion(): Boolean {

        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento()) {
            throw ConnectionErrorException("Firebase no está disponible.")
        }

        return repositorioUsuarios.cerrarSesion()
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