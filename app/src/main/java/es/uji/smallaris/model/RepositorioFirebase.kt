package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date

class RepositorioFirebase : RepositorioVehiculos, RepositorioLugares, RepositorioUsuarios, Repositorio{

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun obtenerAuth(): FirebaseAuth {
        return auth
    }

    override fun getVehiculos(): List<Vehiculo> {
        return mutableListOf()
    }

    override fun addVehiculos(nuevo: Vehiculo): Boolean {
        return true
    }

    override fun updateVehiculos(viejo: Vehiculo, nuevo: Vehiculo): Boolean {
        return false
    }

    override suspend fun getLugares(): List<LugarInteres> {
        return mutableListOf()
    }

    override suspend fun addLugar(lugar: LugarInteres): Boolean {
        return true
    }

    override suspend fun registrarUsuario(correo: String, contrasena: String): Usuario {
        val resultadoAutenticacion = auth.createUserWithEmailAndPassword(correo, contrasena).await()
        val usuario = resultadoAutenticacion.user

        if (usuario != null) {
            val usuarioData = mapOf(
                "correo" to usuario.email,
                "uid" to usuario.uid
            )

            db.collection("usuarios")
                .document(usuario.uid)
                .set(usuarioData)
                .await()

            return Usuario(correo = usuario.email ?: "", uid = usuario.uid)
        } else {
            throw Exception("Error creando el usuario.")
        }
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {
        val resultadoAutenticacion = auth.signInWithEmailAndPassword(correo, contrasena).await()
        val usuario = resultadoAutenticacion.user

        if (usuario != null) {
            return Usuario(correo = usuario.email ?: "", uid = usuario.uid)
        } else {
            throw Exception("Usuario no encontrado o credenciales incorrectas.")
        }
    }

    override suspend fun enFuncionamiento(): Boolean {
        val fechaActual = Date()
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val fechaFormateada = formato.format(fechaActual)

        return try {
            db.collection("test")
                .document("testConnection")
                .set(mapOf("status" to "active $fechaFormateada UTC"))
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

}