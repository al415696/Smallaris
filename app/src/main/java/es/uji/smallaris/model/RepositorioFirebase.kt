package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
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

    override suspend fun getVehiculos(): List<Vehiculo> {
        /*try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual.")

            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)

            val snapshot = userDocRef.get().await()

            val vehiculosExistentes = (snapshot["vehículos"] as? List<*>)?.mapNotNull { vehiculoMap ->
                (vehiculoMap as? Map<*, *>)?.let {
                    Vehiculo(
                        nombre = it["nombre"] as? String ?: "",
                        consumo = (it["consumo"] as? Double) ?: 0.0,
                        matricula = it["matricula"] as? String ?: "",
                        tipo = (it["tipo"] as? String)?.let { tipo ->
                            TipoVehiculo.valueOf(tipo)
                        } ?: TipoVehiculo.Desconocido
                    )
                }
            } ?: emptyList()

            return vehiculosExistentes
        } catch (e: Exception) {
            return emptyList()
        }
        */
        return emptyList()
    }

    override suspend fun addVehiculos(nuevo: Vehiculo): Boolean {
        var retorno = true

        try {
            val currentUser = obtenerUsuarioActual()
                ?: throw ConnectionErrorException("No se pudo obtener el usuario actual.")

            val userDocRef = obtenerFirestore()
                .collection("usuarios")
                .document(currentUser.uid)
                .collection("vehículos")
                .document("data")

            

            userDocRef.update("items", FieldValue.arrayUnion(nuevo))
                .addOnFailureListener { e ->
                    retorno = false
                }


            return retorno
        } catch (e: Exception) {
            retorno = false
            return retorno
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
                "uid" to usuario.uid
            )

            obtenerFirestore().collection("usuarios")
                .document(usuario.uid)
                .set(usuarioData)
                .await()

            val vehiculoPie = mapOf(
                "nombre" to "A pie",
                "consumo" to 50.0,
                "matricula" to "Sin matrícula",
                "tipo" to "Pie",
                "favorito" to false
            )

            val vehiculoBici = mapOf(
                "nombre" to "Bicicleta",
                "consumo" to 30.0,
                "matricula" to "Sin matrícula",
                "tipo" to "Bici",
                "favorito" to false
            )

            val vehiculosData = mapOf(
                "items" to listOf(vehiculoPie, vehiculoBici)
            )

            obtenerFirestore().collection("usuarios")
                .document(usuario.uid)
                .collection("vehículos")
                .document("data")
                .set(vehiculosData)
                .await()

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

    override suspend fun cerrarSesion(): Boolean {
        obtenerAuth().signOut()

        if (obtenerAuth().currentUser != null) {
            throw Exception("No se pudo cerrar sesión correctamente.")
        }
        return true
    }
}
