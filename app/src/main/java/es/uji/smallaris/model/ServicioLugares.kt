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
        // Regla de negocio, si unas coordenadas no tienen un topónimo asociado,
        // le asignamos las propias coordenadas como el nombre identificativo de la ubicación
        var nombreLugar: String = nombre
        if (nombre.isBlank()) {
            nombreLugar = apiObtenerNombres.getToponimoCercano(longitud, latitud)
            if (nombreLugar.isBlank()) {
                nombreLugar = "$longitud, $latitud"
            }
        }
        // Creamos el objeto LugarInteres y lo guardamos en la lista de lugares,
        // además de en el repositorio
        val lugar = LugarInteres(longitud, latitud, nombreLugar)
        lugares.add(lugar)
        repositorioLugares.addLugar(lugar)
        // Devolvemos el lugar creado como indicador de que se ha guardado correctamente
        return lugar
    }

    fun getLugares(): List<LugarInteres> {
        return lugares.toList()
    }

}
