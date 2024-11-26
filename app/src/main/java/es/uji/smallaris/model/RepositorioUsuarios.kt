package es.uji.smallaris.model

interface RepositorioUsuarios: Repositorio {
    suspend fun registrarUsuario(correo: String, contrasena: String): Usuario?
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario?
}