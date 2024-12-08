package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking
import kotlin.jvm.Throws

class ServicioLugares(
    private val repositorioLugares: RepositorioLugares,
    private val apiObtenerNombres: ServicioAPIs
) {

    private val lugares = mutableListOf<LugarInteres>()

    init {
        runBlocking {
            inicializarLugares()
        }
    }

    private suspend fun inicializarLugares() {
        this.lugares.addAll(repositorioLugares.getLugares())
    }

    @Throws(ConnectionErrorException::class, UbicationException::class)
    suspend fun addLugar(longitud: Double, latitud: Double, nombre: String = ""): LugarInteres {

        if ( !repositorioLugares.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        // Regla de negocio: Cada POI tiene un nombre identificativo que corresponde a:
        // 1. Nombre dado por el usuario
        // 2. Topónimo más cercano obtenido por el usuario
        // 3. Longitud, latitud

        val toponimo = apiObtenerNombres.getToponimoCercano(longitud, latitud)
        val municipio = toponimo.split(",")[1]
        var identificador = nombre
        if (nombre.isEmpty()) {
            identificador = toponimo
            if (identificador.split(",")[0] == "Desconocido") {
                identificador = "$longitud, $latitud"
            }
        } else {
            identificador = nombre
        }

        val lugar = LugarInteres(longitud, latitud, identificador, municipio)

        // Regla de negocio: No se pueden dar de alta dos lugares con la misma ubicación
        if (lugares.contains(lugar)) {
            throw UbicationException("Ya existe un lugar con la misma ubicación")
        }

        lugares.add(lugar)
        repositorioLugares.addLugar(lugar)
        // Devolvemos el lugar creado como indicador de que se ha guardado correctamente
        return lugar
    }

    @Throws(ConnectionErrorException::class)
    suspend fun getLugares(ordenLugares: OrdenLugarInteres = OrdenLugarInteres.FAVORITO_THEN_NOMBRE): List<LugarInteres> {
        if ( !repositorioLugares.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        return lugares.sortedWith(
            ordenLugares.comparator()
        )
    }
    @Throws(UbicationException::class)
    suspend fun setFavorito(lugarInteres: LugarInteres, favorito: Boolean = true): Boolean {
        if ( !repositorioLugares.enFuncionamiento() )
            throw ConnectionErrorException("Firebase no está disponible")
        if (lugarInteres.isFavorito() == favorito)
            return false
        lugarInteres.setFavorito(favorito)
        if (lugares.contains(lugarInteres)) {
            return repositorioLugares.setLugarInteresFavorito(lugarInteres,favorito)
        }
        return false
    }
}