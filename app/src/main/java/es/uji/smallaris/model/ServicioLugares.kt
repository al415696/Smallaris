package es.uji.smallaris.model

import kotlin.jvm.Throws

class ServicioLugares(
    private val repositorioLugares: RepositorioLugares,
    private val apiObtenerNombres: ServicioAPIs
) {

    private val lugares = mutableListOf<LugarInteres>()

    init {
        this.lugares.addAll(repositorioLugares.getLugares())
    }

    @Throws(UbicationErrorException::class)
    fun addLugar(longitud: Float, latitud: Float, nombre: String = ""): LugarInteres {

        // Regla de negocio: Cada POI tiene un nombre identificativo que corresponde a:
        // 1. Nombre dado por el usuario
        // 2. Topónimo más cercano obtenido por el usuario
        // 3. Longitud, latitud
        var identificador = nombre
        if (identificador.isEmpty()) {
            identificador = apiObtenerNombres.getToponimoCercano(longitud, latitud)
            if (identificador.isEmpty()) {
                identificador = "$longitud, $latitud"
            }
        }

        val lugar = LugarInteres(longitud, latitud, identificador)

        // Regla de negocio: No se pueden dar de alta dos lugares con la misma ubicación
        if (lugares.contains(lugar)) {
            throw UbicationErrorException("Ya existe un lugar con la misma ubicación")
        }

        lugares.add(lugar)
        repositorioLugares.addLugar(lugar)
        // Devolvemos el lugar creado como indicador de que se ha guardado correctamente
        return lugar
    }

    @Throws(ConnectionErrorException::class)
    fun getLugares(): List<LugarInteres> {
        return lugares
    }

}
