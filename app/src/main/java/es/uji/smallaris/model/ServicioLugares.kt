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

        val lugar = LugarInteres(0F, 0F, "No implementado")
        lugares.add(lugar)
        repositorioLugares.addLugar(lugar)
        // Devolvemos el lugar creado como indicador de que se ha guardado correctamente
        return lugar
    }

    @Throws(ConnectionErrorException::class)
    fun getLugares(): List<LugarInteres> {
        return listOf()
    }

}
