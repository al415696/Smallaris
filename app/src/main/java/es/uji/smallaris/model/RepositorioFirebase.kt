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
        TODO("Not yet implemented")
    }

    override fun addVehiculos(nuevo: Vehiculo): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateVehiculos(viejo: Vehiculo, nuevo: Vehiculo): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getLugares(): List<LugarInteres> {
        return mutableListOf()
    }

    override suspend fun addLugar(lugar: LugarInteres): Boolean {
        return true
    }

    override suspend fun registrarUsuario(correo: String, contrasena: String): Usuario {
        // Verificar si dicho usuario ya existe
        val usuarioExistente = db.collection("usuarios")
            .whereEqualTo("correo", correo)
            .get()
            .await()

        if (!usuarioExistente.isEmpty) {
            throw UserAlreadyExistsException()
        }

        // Intentar registrar dicho usuario
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
            throw Exception()
        }
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {
        // Verificar si dicho usuario está registrado
        val usuarioExistente = db.collection("usuarios")
            .whereEqualTo("correo", correo)
            .get()
            .await()

        if (usuarioExistente.isEmpty) {
            throw UnregisteredUserException()
        }

        // Intentar iniciar sesión
        val resultadoAutenticacion = auth.signInWithEmailAndPassword(correo, contrasena).await()
        val usuario = resultadoAutenticacion.user

        if (usuario != null) {
            return Usuario(correo = usuario.email ?: "", uid = usuario.uid)
        } else {
            throw Exception()
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