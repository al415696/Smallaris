package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

interface RepositorioUsuarios: Repositorio {
    fun obtenerFirestore(): FirebaseFirestore
    fun obtenerAuth(): FirebaseAuth
    fun obtenerUsuarioActual(): FirebaseUser?
    suspend fun registrarUsuario(correo: String, contrasena: String): Usuario
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario
    suspend fun borrarUsuario(): Usuario
    suspend fun cerrarSesion(): Usuario
    suspend fun cambiarContrasena(contrasenaVieja: String, contrasenaNueva: String): Boolean
    suspend fun establecerVehiculoPorDefecto(vehiculo: Vehiculo): Boolean
    suspend fun obtenerVehiculoPorDefecto(): Vehiculo?
    suspend fun establecerTipoRutaPorDefecto(tipoRuta: TipoRuta): Boolean
    suspend fun obtenerTipoRutaPorDefecto(): TipoRuta?
}