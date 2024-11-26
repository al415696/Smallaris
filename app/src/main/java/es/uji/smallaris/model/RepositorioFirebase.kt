package es.uji.smallaris.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RepositorioFirebase : RepositorioVehiculos, RepositorioLugares, RepositorioUsuarios, Repositorio{

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun getVehiculos(): List<Vehiculo> {
        TODO("Not yet implemented")
    }

    override fun addVehiculos(nuevo: Vehiculo): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateVehiculos(viejo: Vehiculo, nuevo: Vehiculo): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLugares(): List<LugarInteres> {
        return mutableListOf()
    }

    override fun addLugar(lugar: LugarInteres): Boolean {
        return true
    }

    override suspend fun registrarUsuario(correo: String, contrasena: String): Usuario? {
        return try {
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

                Usuario(correo = usuario.email ?: "", uid = usuario.uid)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error registrando usuario: ${e.message}")
            null
        }
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        return try {
            val resultadoAutenticacion = auth.signInWithEmailAndPassword(correo, contrasena).await()
            val usuario = resultadoAutenticacion.user
            if (usuario != null) {
                Usuario(correo = usuario.email ?: "", uid = usuario.uid)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error iniciando sesión: ${e.message}")
            null
        }
    }

    // Función suspendida que verifica si Firestore está funcionando correctamente
    override suspend fun enFuncionamiento(): Boolean {
        return try {
            // Intentamos escribir un documento en la colección 'test'
            db.collection("test")
                .document("testConnection")
                .set(mapOf("status" to "active"))
                .await() // Usamos await() para suspender la función hasta que se complete la operación

            true // Si la operación fue exitosa, retornamos true
        } catch (e: Exception) {
            false // Si ocurre un error (como un fallo de conexión), retornamos false
        }
    }
}