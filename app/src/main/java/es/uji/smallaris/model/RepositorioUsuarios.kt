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
    suspend fun cerrarSesion(): Boolean
    suspend fun borrarUsuario(): Usuario?
}