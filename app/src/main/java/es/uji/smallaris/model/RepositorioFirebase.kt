package es.uji.smallaris.model

import android.accounts.NetworkErrorException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import es.uji.smallaris.model.lugares.LugarInteres
import com.google.firebase.firestore.SetOptions
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

    override suspend fun getVehiculos(): List<Vehiculo> {
        try {
            val currentUser = obtenerUsuarioActual() ?: return emptyList()
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("vehículos")
                .document("data")
            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                val items = document["items"] as? List<Map<String, Any>> ?: return emptyList()
                return items.mapNotNull { item ->
                    val nombre = item["nombre"] as? String
                    val consumo = item["consumo"] as? Double
                    val matricula = item["matricula"] as? String
                    val tipo = item["tipo"]?.let { TipoVehiculo.valueOf(it.toString()) } ?: TipoVehiculo.Desconocido
                    val favorito = item["favorito"] as? Boolean ?: false
                    if (nombre != null && matricula != null && consumo != null) {
                        Vehiculo(nombre, consumo, matricula, tipo, favorito)
                    } else {
                        null
                    }
                }
            }
            return emptyList()
        } catch (e: Exception) {
            println("Error al obtener vehículos: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun addVehiculos(nuevo: Vehiculo): Boolean {
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual.")

            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("vehículos")
                .document("data")

            // Primero, intentamos obtener el documento para verificar si el array "items" existe
            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                userDocRef.update("items", FieldValue.arrayUnion(nuevo.toMap())).await()
            } else {
                val initialData = mapOf("items" to listOf(nuevo.toMap()))
                userDocRef.set(initialData, SetOptions.merge()).await()
            }


            return true
        } catch (e: Exception) {
            // Manejo de errores
            println("Error al agregar vehículo: ${e.message}")
            return false
        }
    }

    override suspend fun updateVehiculos(viejo: Vehiculo, nuevo: Vehiculo): Boolean {
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual.")

            val userDocRef = obtenerFirestore().collection("usuarios").document(currentUser.uid)
            val snapshot = userDocRef.get().await()
            val vehiculosExistentes =
                (snapshot["vehículos"] as? List<*>)?.mapNotNull { it as? Map<*, *> } ?: emptyList()

            val vehiculosActualizados = vehiculosExistentes.map {
                if (it["nombre"] == viejo.nombre && it["matricula"] == viejo.matricula) {
                    nuevo.toMap()
                } else {
                    it
                }
            }

            userDocRef.update("vehículos", vehiculosActualizados).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun setVehiculoFavorito(vehiculo: Vehiculo, favorito: Boolean): Boolean {
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual")

            val userDocRef = obtenerFirestore().collection("usuarios").document(currentUser.uid)
            val snapshot = userDocRef.get().await()
            val vehiculosExistentes = (snapshot["vehículos"] as? List<*>)?.mapNotNull { it as? Map<*, *> } ?: emptyList()

            val vehiculosActualizados = vehiculosExistentes.map {
                if (it["nombre"] == vehiculo.nombre && it["matricula"] == vehiculo.matricula) {
                    mapOf(
                        "nombre" to vehiculo.nombre,
                        "consumo" to vehiculo.consumo,
                        "matricula" to vehiculo.matricula,
                        "tipo" to vehiculo.tipo.name,
                        "favorito" to favorito
                    )
                } else {
                    it
                }
            }

            userDocRef.update("vehículos", vehiculosActualizados).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun removeVehiculo(vehiculo: Vehiculo): Boolean {
        return true
    }

    override suspend fun getLugares(): List<LugarInteres> {
        try {
            val currentUser = obtenerUsuarioActual() ?: return emptyList()
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("lugares")
                .document("data")
            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                val items = document["items"] as? List<Map<String, Any>> ?: return emptyList()
                return items.mapNotNull { item ->
                    val nombre = item["nombre"] as? String
                    val municipio = item["municipio"] as? String
                    val latitud = item["latitud"] as? Double
                    val longitud = item["longitud"] as? Double
                    if (nombre != null && municipio != null && latitud != null && longitud != null) {
                        LugarInteres(longitud, latitud, nombre, municipio)
                    } else {
                        null
                    }
                }
            }
            return emptyList()
        } catch (e: Exception) {
            println("Error al obtener lugares: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun addLugar(lugar: LugarInteres): Boolean {
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual.")

            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("lugares")
                .document("data")

            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                userDocRef.update("items", FieldValue.arrayUnion(lugar.toMap())).await()
            } else {
                val initialData = mapOf("items" to listOf(lugar.toMap()))
                userDocRef.set(initialData, SetOptions.merge()).await()
            }

            return true
        } catch (e: Exception) {
            println("Error al agregar lugar: ${e.message}")
            return false
        }
    }

    override suspend fun setLugarInteresFavorito(lugar: LugarInteres, favorito: Boolean): Boolean {
        return true
    }

    override suspend fun deleteLugar(lugar: LugarInteres): Boolean {
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
            )

            // Crear el documento del usuario en la colección 'usuarios'
            val usuarioDocRef = obtenerFirestore().collection("usuarios").document(usuario.uid)
            usuarioDocRef.set(usuarioData).await()

            // Datos predeterminados de vehículos
            val vehiculoPie = mapOf(
                "nombre" to "A pie",
                "consumo" to 0.0,
                "matricula" to "Sin matrícula",
                "tipo" to "Pie",
                "favorito" to false
            )

            val vehiculoBici = mapOf(
                "nombre" to "Bicicleta",
                "consumo" to 0.0,
                "matricula" to "Sin matrícula",
                "tipo" to "Bici",
                "favorito" to false
            )

            val vehiculosData = mapOf(
                "items" to listOf(vehiculoPie, vehiculoBici)
            )

            // Crear subcolección 'vehículos' con documento 'data' y array 'items'
            usuarioDocRef.collection("vehículos").document("data").set(vehiculosData).await()


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

    override suspend fun deleteRuta(ruta: Ruta): Boolean {
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
    override suspend fun cerrarSesion(): Usuario {

        if (auth.currentUser == null) {
            throw UnloggedUserException("No se había iniciado sesión.")  // Error si no hay sesión activa
        }

        return try {
            val correo = auth.currentUser!!.email ?: "correoDesconocido"

            auth.signOut()  // Intentar cerrar sesión

            // Verificar que no haya ningún usuario autenticado
            if (auth.currentUser != null) {
                throw Exception("No se pudo cerrar sesión correctamente.")
            }

            Usuario(correo) // La sesión se cerró correctamente
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

    override suspend fun borrarUsuario(): Usuario {
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual.")

            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)

            val usuario = Usuario(correo = currentUser.email ?: "")

            userDocRef.delete().await()

            currentUser.delete().await()

            return usuario
        } catch (e: Exception) {
            throw UserException("No se pudo eliminar el usuario.")
        }
    }

    companion object{
        private lateinit var repositorioFirebase: RepositorioFirebase
        fun getInstance(): RepositorioFirebase{
            if (!this::repositorioFirebase.isInitialized){
                repositorioFirebase = RepositorioFirebase()
            }
            return repositorioFirebase
        }
    }
}