package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
            val vehiculosExistentes = (snapshot["vehículos"] as? List<*>)?.mapNotNull { it as? Map<*, *> } ?: emptyList()

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

    override suspend fun getLugares(): List<LugarInteres> {
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual.")

            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)

            val snapshot = userDocRef.get().await()
            val lugaresExistentes = (snapshot["lugares"] as? List<*>)?.mapNotNull { lugarMap ->
                (lugarMap as? Map<*, *>)?.let {
                    LugarInteres(
                        longitud = (it["longitud"] as? Double) ?: 0.0,
                        latitud = (it["latitud"] as? Double) ?: 0.0,
                        nombre = it["nombre"] as? String ?: "",
                        municipio = it["municipio"] as? String ?: ""
                    )
                }
            } ?: emptyList()

            return lugaresExistentes
        } catch (e: Exception) {
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

            val snapshot = userDocRef.get().await()
            val lugaresExistentes = (snapshot["lugares"] as? List<*>)?.mapNotNull { it as? Map<*, *> } ?: emptyList()

            val nuevoLugar = lugar.toMap()

            userDocRef.update("lugares", lugaresExistentes + nuevoLugar).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun setLugarInteresFavorito(lugar: LugarInteres, favorito: Boolean): Boolean {
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual.")

            val userDocRef = obtenerFirestore().collection("usuarios").document(currentUser.uid)
            val snapshot = userDocRef.get().await()
            val lugaresExistentes = (snapshot["lugares"] as? List<*>)?.mapNotNull { it as? Map<*, *> } ?: emptyList()

            val lugaresActualizados = lugaresExistentes.map {
                if (it["longitud"] == lugar.longitud && it["latitud"] == lugar.latitud) {
                    lugar.toMap() + ("favorito" to favorito) // Usar método toMap y agregar el campo favorito
                } else {
                    it
                }
            }

            userDocRef.update("lugares", lugaresActualizados).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun deleteLugar(lugar: LugarInteres): Boolean {
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual.")

            val userDocRef = obtenerFirestore().collection("usuarios").document(currentUser.uid)
            val snapshot = userDocRef.get().await()
            val lugaresExistentes = (snapshot["lugares"] as? List<*>)?.mapNotNull { it as? Map<*, *> } ?: emptyList()

            val lugaresActualizados = lugaresExistentes.filter {
                it["longitud"] != lugar.longitud || it["latitud"] != lugar.latitud
            }

            userDocRef.update("lugares", lugaresActualizados).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override suspend fun registrarUsuario(correo: String, contrasena: String): Usuario {
        val resultadoAutenticacion = obtenerAuth().createUserWithEmailAndPassword(correo, contrasena).await()
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

            // Datos predeterminados de lugares
            val lugarEjemplo = mapOf(
                "nombre" to "Lugar Ejemplo",
                "municipio" to "Ejemplo",
                "longitud" to 0.0,
                "latitud" to 0.0
            )

            val lugaresData = mapOf(
                "items" to listOf(lugarEjemplo)
            )

            // Crear subcolección 'lugares' con documento 'data' y array 'items'
            usuarioDocRef.collection("lugares").document("data").set(lugaresData).await()

            return Usuario(correo = usuario.email ?: "")
        } else {
            throw Exception("No se pudo crear el usuario y la colección asociada.")
        }
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): Usuario {
        val resultadoAutenticacion = obtenerAuth().signInWithEmailAndPassword(correo, contrasena).await()
        val usuario = resultadoAutenticacion.user

        if (usuario != null) {
            return Usuario(correo = usuario.email ?: "")
        } else {
            throw Exception("No se pudo iniciar sesión.")
        }
    }

    override suspend fun getRutas(): List<Ruta> {
        return listOf()
    }

    override suspend fun addRuta(ruta: Ruta): Boolean {
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

    override suspend fun cerrarSesion(): Usuario {
        val currentUser = obtenerUsuarioActual()
            ?: throw UnloggedUserException("No hay usuario autenticado actualmente.")

        val usuario = Usuario(correo = currentUser.email ?: "")

        obtenerAuth().signOut()

        return usuario
    }
}
