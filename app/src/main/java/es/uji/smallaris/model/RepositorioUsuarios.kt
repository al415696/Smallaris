package es.uji.smallaris.model

interface RepositorioUsuarios {
    fun registrarUsuario(correo: String, contrasena: String): Usuario
    fun iniciarSesion(correo: String, contrasena: String): Usuario
}