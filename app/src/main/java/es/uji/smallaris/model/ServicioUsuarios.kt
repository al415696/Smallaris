package es.uji.smallaris.model

import kotlin.jvm.Throws

class ServicioUsuarios(private val repositorioUsuarios: RepositorioUsuarios) {

    @Throws(UserAlreadyExistsException::class)
    suspend fun registrarUsuario(correo: String, contrasena: String): Usuario {
        if ( !repositorioUsuarios.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible.")
        try {
            return repositorioUsuarios.registrarUsuario(correo, contrasena)
        } catch (e: UserAlreadyExistsException) {
            throw UserAlreadyExistsException()
        } catch (e: Exception) {
            throw Exception("Error inesperado al registrar el usuario: '${e.message}'.")
        }
    }

    @Throws(UnregisteredUserException::class)
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {
        if ( !repositorioUsuarios.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible.")
        try {
            return repositorioUsuarios.iniciarSesion(correo, contrasena)
        } catch (e: UnregisteredUserException) {
            throw UnregisteredUserException()
        } catch (e: Exception) {
            throw Exception("Error inesperado al iniciar sesión: ${e.message}.")
        }
    }
}