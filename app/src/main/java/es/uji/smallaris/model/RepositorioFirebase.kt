package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date

class RepositorioFirebase : RepositorioVehiculos, RepositorioLugares, RepositorioUsuarios,
    RepositorioRutas,
    Repositorio {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun obtenerFirestore(): FirebaseFirestore {
        return db
    }

    override fun obtenerAuth(): FirebaseAuth {
        return auth
    }

    override fun obtenerUsuarioActual(): FirebaseUser? {
        return auth.currentUser
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

    override fun removeVehiculo(vehiculo: Vehiculo): Boolean {
        return true
    }

    override fun setVehiculoFavorito(vehiculo: Vehiculo, favorito: Boolean): Boolean {
        return true
    }

    override suspend fun getLugares(): List<LugarInteres> {
        return mutableListOf()
    }

    override suspend fun addLugar(lugar: LugarInteres): Boolean {
        return true
    }


    override suspend fun setLugarInteresFavorito(lugar: LugarInteres, favorito: Boolean): Boolean {
        return true
    }

    override fun deleteLugar(lugar: LugarInteres): Boolean {
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

            return Usuario(correo = usuario.email ?: "")
        } else {
            throw Exception("No se pudo crear el usuario y la colección asociada.")
        }
    }


    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {

        // Intentar iniciar sesión
        val resultadoAutenticacion = auth.signInWithEmailAndPassword(correo, contrasena).await()
        val usuario = resultadoAutenticacion.user

        if (usuario != null) {
            return Usuario(correo = usuario.email ?: "")
        } else {
            throw Exception("No se pudo iniciar sesión correctamente")
        }
    }

    override suspend fun getRutas(): List<Ruta> {
        return listOf()
    }

    override suspend fun addRuta(ruta: Ruta): Boolean {
        return true
    }

    override suspend fun setRutaFavorita(ruta: Ruta, favorito: Boolean) :Boolean{
        return true
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

    override suspend fun cerrarSesion(): Boolean {
        auth.signOut()

        if (auth.currentUser != null) {
            throw Exception("No se pudo cerrar sesión correctamente.")
        }
        return true // Sesión cerrada con éxito
    }
}