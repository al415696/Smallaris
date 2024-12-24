package es.uji.smallaris.model

import android.accounts.NetworkErrorException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import es.uji.smallaris.model.lugares.LugarInteres
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.jvm.Throws

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
        return true
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

    @Throws(UserAlreadyExistsException::class)
    override suspend fun registrarUsuario(correo: String, contrasena: String): Usuario {
        try {
            // Intenta crear un usuario con el correo y contraseña
            val resultadoAutenticacion =
                auth.createUserWithEmailAndPassword(correo, contrasena).await()
            val usuario = resultadoAutenticacion.user

            if (usuario != null) {
                val usuarioData = mapOf(
                    "correo" to usuario.email,
                    "uid" to usuario.uid
                )

                // Guarda los datos del usuario en Firestore
                db.collection("usuarios")
                    .document(usuario.uid)
                    .set(usuarioData)
                    .await()

                return Usuario(correo = usuario.email ?: "")
            } else {
                throw Exception("No se pudo crear el usuario y la colección asociada.")
            }
        } catch (e: FirebaseAuthWeakPasswordException) {
            throw Exception("La contraseña es demasiado débil. Por favor, usa una contraseña más segura.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw Exception("El correo electrónico está mal formado o es inválido.")
        } catch (e: FirebaseAuthUserCollisionException) {
            throw UserAlreadyExistsException("El correo electrónico ya está registrado.")
        } catch (e: FirebaseFirestoreException) {
            throw Exception("Error al guardar los datos del usuario en Firestore: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Ocurrió un error inesperado: ${e.message}")
        }
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {
        try {
            // Intentar iniciar sesión
            val resultadoAutenticacion = auth.signInWithEmailAndPassword(correo, contrasena).await()
            val usuario = resultadoAutenticacion.user

            if (usuario != null) {
                return Usuario(correo = usuario.email ?: "")
            } else {
                throw Exception("No se pudo iniciar sesión correctamente. Usuario no encontrado.")
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            throw UnregisteredUserException("El usuario no está registrado.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw UnregisteredUserException("Credenciales inválidas. ${e.errorCode}")
        } catch (e: Exception) {
            throw Exception("Ocurrió un error inesperado al iniciar sesión: ${e.message}")
        }
    }


    override suspend fun getRutas(): List<Ruta> {
        return listOf()
    }

    override suspend fun addRuta(ruta: Ruta): Boolean {
        return true
    }

    override suspend fun setRutaFavorita(ruta: Ruta, favorito: Boolean): Boolean {
        return true
    }

    override suspend fun deleteLugar(ruta: Ruta): Boolean {
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

    @Throws(UnloggedUserException::class)
    override suspend fun cerrarSesion(): Boolean {

        if (auth.currentUser == null) {
            throw UnloggedUserException("No se había iniciado sesión.")  // Error si no hay sesión activa
        }

        return try {

            auth.signOut()  // Intentar cerrar sesión

            // Verificar que no haya ningún usuario autenticado
            if (auth.currentUser != null) {
                throw Exception("No se pudo cerrar sesión correctamente.")
            }

            true // La sesión se cerró correctamente
        } catch (e: FirebaseAuthException) {
            // Manejar las excepciones específicas de Firebase
            throw Exception("Error de autenticación al cerrar sesión: ${e.localizedMessage}", e)
        } catch (e: NetworkErrorException) {
            // Manejar errores relacionados con la red, si fuera necesario
            throw Exception("Error de red al intentar cerrar sesión: ${e.localizedMessage}", e)
        } catch (e: Exception) {
            // Manejar cualquier otra excepción inesperada
            throw Exception("Error inesperado al cerrar sesión: ${e.localizedMessage}", e)
        }
    }
}