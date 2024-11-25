package es.uji.smallaris.model

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RepositorioFirebase : RepositorioVehiculos, RepositorioLugares, RepositorioUsuarios, Repositorio{

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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

    override fun registrarUsuario(correo: String, contrasena: String): Usuario {
        TODO("Not yet implemented")
    }

    override fun iniciarSesion(correo: String, contrasena: String): Usuario {
        TODO("Not yet implemented")
    }

    // Función suspendida que verifica si Firestore está funcionando correctamente
    suspend override fun enFuncionamiento(): Boolean {
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