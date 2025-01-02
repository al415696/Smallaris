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
import com.google.firebase.firestore.SetOptions
import com.mapbox.geojson.LineString
import es.uji.smallaris.model.lugares.LugarInteres
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date

class RepositorioFirebase : RepositorioVehiculos, RepositorioLugares, RepositorioUsuarios,
    RepositorioRutas,
    Repositorio {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var vehiculoPorDefecto: Vehiculo? = null
    private var tipoRutaPorDefecto: TipoRuta? = null

    override fun obtenerFirestore(): FirebaseFirestore {
        return db
    }

    override fun obtenerAuth(): FirebaseAuth {
        return auth
    }

    override fun obtenerUsuarioActual(): FirebaseUser? {
        return auth.currentUser
    }

    @Throws(VehicleException::class)
    override suspend fun establecerVehiculoPorDefecto(vehiculo: Vehiculo): Boolean {
        val currentUser = obtenerUsuarioActual()
            ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

        try {
            val userDocRef = obtenerFirestore().collection("usuarios").document(currentUser.uid)

            val document = userDocRef.get().await()
            val vehiculoActual = document["vehiculoPorDefecto"] as? Map<String, Any>

            if (vehiculoActual != null && vehiculoActual["matricula"] == vehiculo.matricula) {
                throw VehicleException("El vehículo ya está establecido como por defecto.")
            }

            userDocRef.update("vehiculoPorDefecto", vehiculo.toMap()).await()
            vehiculoPorDefecto = vehiculo

            return true
        } catch (e: VehicleException) {
            throw e
        } catch (e: Exception) {
            throw Exception("No se pudo establecer el vehículo por defecto.")
        }
    }

    override suspend fun obtenerVehiculoPorDefecto(): Vehiculo? {
        return vehiculoPorDefecto
    }

    override suspend fun establecerTipoRutaPorDefecto(tipoRuta: TipoRuta): Boolean {
        return false
    }

    override suspend fun obtenerTipoRutaPorDefecto(): TipoRuta? {
        return tipoRutaPorDefecto
    }

    override suspend fun getVehiculos(): List<Vehiculo> {
        val currentUser = obtenerUsuarioActual()
            ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

        try {
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
                ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

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
            val currentUser = obtenerUsuarioActual() ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("vehículos")
                .document("data")

            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                val items = document["items"] as? List<Map<String, Any>> ?: return false
                val index = items.indexOfFirst { it["matricula"] == viejo.matricula }
                if (index != -1) {
                    val updatedItems = items.toMutableList()
                    val updatedVehiculo = items[index].toMutableMap()

                    updatedVehiculo["nombre"] = nuevo.nombre
                    updatedVehiculo["consumo"] = nuevo.consumo
                    updatedVehiculo["matricula"] = nuevo.matricula
                    updatedVehiculo["tipo"] = nuevo.tipo.name
                    updatedVehiculo["favorito"] = nuevo.isFavorito() // Actualizamos el campo favorito

                    updatedItems[index] = updatedVehiculo
                    userDocRef.update("items", updatedItems).await()
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            println("Error al actualizar vehículo: ${e.message}")
            return false
        }
    }

    override suspend fun setVehiculoFavorito(vehiculo: Vehiculo, favorito: Boolean): Boolean {
        try {
            val currentUser = obtenerUsuarioActual() ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("vehículos")
                .document("data")

            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                val items = document["items"] as? List<Map<String, Any>> ?: return false
                val index = items.indexOfFirst { it["matricula"] == vehiculo.matricula }
                if (index != -1) {
                    val updatedItems = items.toMutableList()
                    val updatedVehiculo = items[index].toMutableMap()
                    updatedVehiculo["favorito"] = favorito
                    updatedItems[index] = updatedVehiculo
                    userDocRef.update("items", updatedItems).await()
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            println("Error al actualizar vehículo favorito: ${e.message}")
            return false
        }
    }

    override suspend fun removeVehiculo(vehiculo: Vehiculo): Boolean {
        try {
            val currentUser = obtenerUsuarioActual() ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("vehículos")
                .document("data")

            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                val items = document["items"] as? List<Map<String, Any>> ?: return false
                val updatedItems = items.filter { it["matricula"] != vehiculo.matricula }
                userDocRef.update("items", updatedItems).await()
                return true
            }
            return false
        } catch (e: Exception) {
            println("Error al eliminar vehículo: ${e.message}")
            return false
        }
    }

    override suspend fun getLugares(): List<LugarInteres> {
        val currentUser = obtenerUsuarioActual()
            ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

        try {
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
                ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

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
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

            val userDocRef = obtenerFirestore().collection("usuarios").document(currentUser.uid)
            val snapshot = userDocRef.get().await()
            val lugaresExistentes = (snapshot["lugares"] as? List<*>)?.mapNotNull { it as? Map<*, *> } ?: emptyList()

            val lugaresActualizados = lugaresExistentes.map {
                if (it["nombre"] == lugar.nombre && it["municipio"] == lugar.municipio) {
                    mapOf(
                        "nombre" to lugar.nombre,
                        "latitud" to lugar.latitud,
                        "longitud" to lugar.longitud,
                        "municipio" to lugar.municipio,
                        "favorito" to favorito
                    )
                } else {
                    it
                }
            }
            userDocRef.update("lugares", lugaresActualizados).await()
            return true
        } catch (e: Exception) {
            println("Error al actualizar lugar: ${e.message}")
            return false
        }
    }

    override suspend fun deleteLugar(lugar: LugarInteres): Boolean {
        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

            val userDocRef = obtenerFirestore().collection("usuarios").document(currentUser.uid)
            val snapshot = userDocRef.get().await()
            val lugaresExistentes = (snapshot["lugares"] as? List<*>)?.mapNotNull { it as? Map<*, *> } ?: emptyList()

            // Filtra los lugares para eliminar el especificado
            val lugaresActualizados = lugaresExistentes.filterNot {
                it["nombre"] == lugar.nombre && it["municipio"] == lugar.municipio
            }

            userDocRef.update("lugares", lugaresActualizados).await()
            return true
        } catch (e: Exception) {
            println("Error al eliminar lugar: ${e.message}")
            return false
        }
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
        } catch (e: FirebaseAuthException) {
            throw UnregisteredUserException("El usuario no está registrado.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw InvalidPasswordException("Contraseña incorrecta. ${e.errorCode}")
        } catch (e: Exception) {
            throw Exception("Ocurrió un error inesperado al iniciar sesión: ${e.message}")
        }
    }

    override suspend fun getRutas(): List<Ruta> {
        val currentUser = obtenerUsuarioActual()
            ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

        try {
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("rutas")
                .document("data")
            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                val items = document["items"] as? List<Map<String, Any>> ?: return emptyList()
                return items.mapNotNull { item ->
                    val nombre = item["nombre"] as? String
                    val inicioMap = item["inicio"] as? Map<String, Any>
                    val finMap = item["fin"] as? Map<String, Any>
                    val vehiculoMap = item["vehiculo"] as? Map<String, Any>
                    val tipo = item["tipo"]?.let { TipoRuta.valueOf(it.toString()) } ?: TipoRuta.Desconocida
                    val trayecto = item["trayecto"] as? String
                    val distancia = (item["distancia"] as? Number)?.toFloat()
                    val duracion = (item["duracion"] as? Number)?.toFloat()
                    val coste = (item["coste"] as? Number)?.toDouble()

                    if (nombre != null && inicioMap != null && finMap != null && vehiculoMap != null && trayecto != null && distancia != null && duracion != null && coste != null) {
                        val inicio = LugarInteres(
                            longitud = inicioMap["longitud"] as? Double ?: return@mapNotNull null,
                            latitud = inicioMap["latitud"] as? Double ?: return@mapNotNull null,
                            nombre = inicioMap["nombre"] as? String ?: return@mapNotNull null,
                            municipio = inicioMap["municipio"] as? String ?: return@mapNotNull null
                        )

                        val fin = LugarInteres(
                            longitud = finMap["longitud"] as? Double ?: return@mapNotNull null,
                            latitud = finMap["latitud"] as? Double ?: return@mapNotNull null,
                            nombre = finMap["nombre"] as? String ?: return@mapNotNull null,
                            municipio = finMap["municipio"] as? String ?: return@mapNotNull null
                        )

                        Ruta(
                            inicio = inicio,
                            fin = fin,
                            vehiculo = Vehiculo(
                                nombre = vehiculoMap["nombre"] as? String ?: "",
                                consumo = vehiculoMap["consumo"] as? Double ?: 0.0,
                                matricula = vehiculoMap["matricula"] as? String ?: "",
                                tipo = vehiculoMap["tipo"]?.let { TipoVehiculo.valueOf(it.toString()) } ?: TipoVehiculo.Desconocido,
                                favorito = vehiculoMap["favorito"] as? Boolean ?: false
                            ),
                            tipo = tipo,
                            trayecto = LineString.fromJson(trayecto),
                            distancia = distancia,
                            duracion = duracion,
                            coste = coste,
                            nombre = nombre
                        )
                    } else {
                        null
                    }
                }
            }
            return emptyList()
        } catch (e: Exception) {
            println("Error al obtener rutas: ${e.message}")
            return emptyList()
        }
    }

    override suspend fun addRuta(ruta: Ruta): Boolean {
        val currentUser = obtenerUsuarioActual()
            ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

        return try {
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("rutas")
                .document("data")

            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                val rutaMap = mapOf(
                    "nombre" to ruta.getNombre(),
                    "inicio" to ruta.getInicio().toMap(),
                    "fin" to ruta.getFin().toMap(),
                    "vehiculo" to mapOf(
                        "nombre" to ruta.getVehiculo().nombre,
                        "consumo" to ruta.getVehiculo().consumo,
                        "matricula" to ruta.getVehiculo().matricula,
                        "tipo" to ruta.getVehiculo().tipo.name
                    ),
                    "tipo" to ruta.getTipo().name,
                    "trayecto" to ruta.getTrayecto().toJson(),
                    "distancia" to ruta.getDistancia(),
                    "duracion" to ruta.getDuracion(),
                    "coste" to ruta.getCoste(),
                    "favorito" to ruta.isFavorito()
                )
                userDocRef.update("items", FieldValue.arrayUnion(rutaMap)).await()
            } else {
                val rutaMap = mapOf(
                    "nombre" to ruta.getNombre(),
                    "inicio" to ruta.getInicio().toMap(),
                    "fin" to ruta.getFin().toMap(),
                    "vehiculo" to mapOf(
                        "nombre" to ruta.getVehiculo().nombre,
                        "consumo" to ruta.getVehiculo().consumo,
                        "matricula" to ruta.getVehiculo().matricula,
                        "tipo" to ruta.getVehiculo().tipo.name
                    ),
                    "tipo" to ruta.getTipo().name,
                    "trayecto" to ruta.getTrayecto().toJson(),
                    "distancia" to ruta.getDistancia(),
                    "duracion" to ruta.getDuracion(),
                    "coste" to ruta.getCoste(),
                    "favorito" to ruta.isFavorito()
                )
                val initialData = mapOf("items" to listOf(rutaMap))
                userDocRef.set(initialData, SetOptions.merge()).await()
            }

            true
        } catch (e: Exception) {
            println("Error al agregar ruta: ${e.message}")
            false
        }
    }

    override suspend fun setRutaFavorita(ruta: Ruta, favorito: Boolean): Boolean {
        val currentUser = obtenerUsuarioActual()
            ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

        try {
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("rutas")
                .document("data")

            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                val items = document["items"] as? List<Map<String, Any>> ?: return false
                val index = items.indexOfFirst { it["nombre"] == ruta.getNombre() }
                if (index != -1) {
                    val updatedItems = items.toMutableList()
                    val updatedRuta = items[index].toMutableMap()
                    updatedRuta["favorito"] = favorito
                    updatedItems[index] = updatedRuta
                    userDocRef.update("items", updatedItems).await()
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            println("Error al actualizar ruta favorita: ${e.message}")
            return false
        }
    }

    override suspend fun deleteRuta(ruta: Ruta): Boolean {
        val currentUser = obtenerUsuarioActual()
            ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

        try {
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("rutas")
                .document("data")

            val document = userDocRef.get().await()

            if (document.exists() && document.contains("items")) {
                val items = document["items"] as? List<Map<String, Any>> ?: return false
                val updatedItems = items.filterNot { it["nombre"] == ruta.getNombre() }
                userDocRef.update("items", updatedItems).await()
                return true
            }
            return false
        } catch (e: Exception) {
            println("Error al eliminar ruta: ${e.message}")
            return false
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

    @Throws(UnloggedUserException::class)
    override suspend fun cerrarSesion(): Usuario {

        auth.currentUser ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

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

    override suspend fun cambiarContrasena(contrasenaVieja: String, contrasenaNueva: String): Boolean {
        val usuarioActual = auth.currentUser
            ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

        if (contrasenaNueva.length < 8 || contrasenaVieja == contrasenaNueva) {
            throw InvalidPasswordException("La nueva contraseña debe tener al menos 8 caracteres y ser distinta a la anterior.")
        }

        try {
            auth.signInWithEmailAndPassword(usuarioActual.email!!, contrasenaVieja).await().user
                ?: throw InvalidPasswordException("La contraseña actual es incorrecta.")

            usuarioActual.updatePassword(contrasenaNueva).await()
            return true
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            throw InvalidPasswordException("La contraseña actual es incorrecta.")
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun borrarUsuario(): Usuario {
        val currentUser = obtenerUsuarioActual()
            ?: throw UnloggedUserException("No hay un usuario logueado actualmente.")

        try {
            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)

            val usuario = Usuario(correo = currentUser.email ?: "")

            val subcolecciones = listOf("vehículos", "lugares", "rutas")
            for (subcoleccion in subcolecciones) {
                val subcoleccionRef = userDocRef.collection(subcoleccion)
                val documentos = subcoleccionRef.get().await()

                for (documento in documentos) {
                    subcoleccionRef.document(documento.id).delete().await()
                }
            }

            userDocRef.delete().await()
            currentUser.delete().await()

            return usuario
        } catch (e: Exception) {
            throw UserException("No se pudo eliminar el usuario o sus datos.")
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
   