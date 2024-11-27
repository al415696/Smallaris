package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

interface RepositorioUsuarios: Repositorio {
    fun obtenerAuth(): FirebaseAuth
    suspend fun registrarUsuario(correo: String, contrasena: String): Usuario
    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario
    fun obtenerFirestore(): FirebaseFirestore
}