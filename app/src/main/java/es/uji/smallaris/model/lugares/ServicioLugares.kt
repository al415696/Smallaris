package es.uji.smallaris.model.lugares

import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.OrdenLugarInteres
import es.uji.smallaris.model.RepositorioFirebase
import es.uji.smallaris.model.RepositorioLugares
import es.uji.smallaris.model.RouteException
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.ServicioRutas
import kotlinx.coroutines.runBlocking

class ServicioLugares(
    private val repositorioLugares: RepositorioLugares,
    private val apiObtenerNombres: ServicioAPIs
) {

    private var lugares = mutableListOf<LugarInteres>()

    init {
        runBlocking {
            updateLugares()
        }
    }

    suspend fun updateLugares() {
        this.lugares = repositorioLugares.getLugares().toMutableList()
    }

    @Throws(ConnectionErrorException::class, UbicationException::class)
    suspend fun addLugar(longitud: Double, latitud: Double, nombre: String = ""): LugarInteres {
        val longitudCorrecta: Boolean = (longitud < -180 || longitud > 180)
        val latitudCorrecta: Boolean = (latitud < -90 || latitud > 90)
        if (longitudCorrecta || latitudCorrecta) {
            val errorMessage = StringBuilder("Las coordenadas deben estar ")
            if (longitudCorrecta) {
                errorMessage.append("entre -180 y 180 grados de longitud")
                if (latitudCorrecta)
                    errorMessage.append("y entre -90 y 90 grados de latitud")
            } else
                errorMessage.append("estar entre -90 y 90 grados de latitud")

            throw UbicationException(errorMessage.toString())

        }

        if (!repositorioLugares.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")

        val lugarBarato = LugarInteres(longitud, latitud, nombre, "")

        // Regla de negocio: No se pueden dar de alta dos lugares con la misma ubicación
        if (lugares.contains(lugarBarato)) {
            throw UbicationException("Ya existe un lugar con la misma ubicación")
        }

        // Regla de negocio: Cada POI tiene un nombre identificativo que corresponde a:
        // 1. Nombre dado por el usuario
        // 2. Topónimo más cercano obtenido por el usuario
        // 3. Longitud, latitud

        val toponimo = apiObtenerNombres.getToponimoCercano(longitud, latitud)
        val municipio = toponimo.split(",").map { it.trim() }[1]
        var identificador: String
        if (nombre.isEmpty()) {
            identificador = toponimo
            if (identificador.split(",").map { it.trim() }[0] == "Desconocido") {
                identificador = "$longitud, $latitud"
            }
        } else {
            identificador = nombre
        }

        val lugar = LugarInteres(longitud, latitud, identificador, municipio)

        if (repositorioLugares.addLugar(lugar))
            lugares.add(lugar)
        else
            throw UbicationException("No se pudo añadir el lugar por un problema remoto")
        // Devolvemos el lugar creado como indicador de que se ha guardado correctamente
        return lugar
    }

    @Throws(ConnectionErrorException::class)
    suspend fun getLugares(ordenLugares: OrdenLugarInteres = OrdenLugarInteres.FAVORITO_THEN_NOMBRE): List<LugarInteres> {
        if (!repositorioLugares.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")
        return lugares.sortedWith(
            ordenLugares.comparator()
        )
    }

    @Throws(ConnectionErrorException::class, UbicationException::class)
    suspend fun deleteLugar(
        lugarInteres: LugarInteres,
        servicioRutas: ServicioRutas = ServicioRutas.getInstance()
    ): Boolean {

        if (!repositorioLugares.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")

        if (lugarInteres.isFavorito()) {
            throw UbicationException("Ubicación favorita no se puede borrar")
        }

        val rutasConElLugar = servicioRutas.contains(lugarInteres)
        if (rutasConElLugar.isNotEmpty()) {
            val mensajeError = StringBuilder("No se puede borrar porque se usa en ")
            mensajeError.append("la ruta ${rutasConElLugar[0].getNombre().take(50)}")
            if (rutasConElLugar.size != 1) {
                mensajeError.append(" y en ${if (rutasConElLugar.size == 2) "una más" else "${rutasConElLugar.size - 1} otras"}")
            }
            throw RouteException(mensajeError.toString())
        }

        if (repositorioLugares.deleteLugar(lugarInteres)) {
            lugares.remove(lugarInteres)
            return true
        }
        return false
    }

    @Throws(UbicationException::class)
    suspend fun setLugarInteresFavorito(
        lugarInteres: LugarInteres,
        favorito: Boolean = true
    ): Boolean {
        if (!repositorioLugares.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")
        if (lugarInteres.isFavorito() == favorito) {
            return false
        }
        if (lugares.contains(lugarInteres)) {
            lugarInteres.setFavorito(favorito)
            return repositorioLugares.setLugarInteresFavorito(lugarInteres, favorito)
        }
        return false
    }

    companion object {
        private lateinit var servicio: ServicioLugares
        fun getInstance(): ServicioLugares {
            if (!this::servicio.isInitialized) {
                servicio = ServicioLugares(
                    repositorioLugares = RepositorioFirebase.getInstance(),
                    apiObtenerNombres = ServicioAPIs
                )
            }
            return servicio
        }
    }
}