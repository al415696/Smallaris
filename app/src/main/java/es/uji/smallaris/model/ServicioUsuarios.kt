package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseUser

class ServicioUsuarios(private val repositorioUsuarios: RepositorioUsuarios) {

    @Throws(UserAlreadyExistsException::class, ConnectionErrorException::class)
    suspend fun registrarUsuario(correo: String, contrasena: String): Usuario {

        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible.")

        return repositorioUsuarios.registrarUsuario(correo, contrasena)
    }

    @Throws(
        UnregisteredUserException::class,
        InvalidPasswordException::class,
        ConnectionErrorException::class
    )
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {

        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible.")

        return repositorioUsuarios.iniciarSesion(correo, contrasena)
    }

    @Throws(UnloggedUserException::class, ConnectionErrorException::class)
    suspend fun cerrarSesion(): Usuario {
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

    suspend fun cambiarContrasena(contrasenaVieja: String, contrasenaNueva: String): Boolean {
        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento()) {
            throw ConnectionErrorException("Firebase no está disponible.")
        }

        return repositorioUsuarios.cambiarContrasena(contrasenaVieja, contrasenaNueva)
    }

    @Throws(VehicleException::class, ConnectionErrorException::class)
    suspend fun establecerVehiculoPorDefecto(vehiculo: Vehiculo): Boolean {
        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento()) {
            throw ConnectionErrorException("Firebase no está disponible.")
        }

        return repositorioUsuarios.establecerVehiculoPorDefecto(vehiculo)
    }

    suspend fun obtenerVehiculoPorDefecto(): Vehiculo? {
        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento()) {
            throw ConnectionErrorException("Firebase no está disponible.")
        }

        return repositorioUsuarios.obtenerVehiculoPorDefecto()
    }

    @Throws(RouteException::class, ConnectionErrorException::class)
    suspend fun establecerTipoRutaPorDefecto(tipoRuta: TipoRuta): Boolean {
        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento()) {
            throw ConnectionErrorException("Firebase no está disponible.")
        }

        return repositorioUsuarios.establecerTipoRutaPorDefecto(tipoRuta)
    }

    suspend fun obtenerTipoRutaPorDefecto(): TipoRuta? {
        // Comprobación de conexión a Firebase
        if (!repositorioUsuarios.enFuncionamiento()) {
            throw ConnectionErrorException("Firebase no está disponible.")
        }

        return repositorioUsuarios.obtenerTipoRutaPorDefecto()
    }

    companion object {
        private lateinit var servicioUsuarios: ServicioUsuarios
        fun getInstance(): ServicioUsuarios {
            if (!this::servicioUsuarios.isInitialized) {
                servicioUsuarios = ServicioUsuarios(RepositorioFirebase.getInstance())
            }
            return servicioUsuarios
        }
    }
}