package es.uji.smallaris.model

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat

import java.util.Date

class RepositorioFirebase : RepositorioVehiculos, RepositorioLugares, RepositorioUsuarios, Repositorio{

    private val db: FirebaseFirestore = Firebase.firestore

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

    override fun enFuncionamiento(): Boolean {
        val fechaActual = Date()
        val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val fechaFormateada = formato.format(fechaActual)
        return runBlocking {
            try {
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
}