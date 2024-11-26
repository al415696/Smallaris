package es.uji.smallaris.model

import kotlin.jvm.Throws

class ServicioUsuarios(private val repositorioUsuarios: RepositorioUsuarios) {

    @Throws(UserAlreadyExistsException::class)
    suspend fun registrarUsuario(correo: String, contrasena: String): Usuario? {
        return repositorioUsuarios.registrarUsuario(correo, contrasena)
    }

    @Throws(UnregisteredUserException::class)
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        return repositorioUsuarios.iniciarSesion(correo, contrasena)
    }
}