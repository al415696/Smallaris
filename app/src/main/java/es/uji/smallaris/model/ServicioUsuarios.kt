package es.uji.smallaris.model

import kotlin.jvm.Throws

class ServicioUsuarios(private val repositorioUsuarios: RepositorioUsuarios) {

    @Throws(UserAlreadyExistsException::class)
    fun registrarUsuario(correo: String, contrasena: String): Usuario? {
        repositorioUsuarios.registrarUsuario(correo, contrasena)
        return null
    }

    @Throws(UnregisteredUserException::class)
    fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        repositorioUsuarios.iniciarSesion(correo, contrasena)
        return null
    }
}