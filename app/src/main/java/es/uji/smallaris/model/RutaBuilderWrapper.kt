package es.uji.smallaris.model

class RutaBuilderWrapper(private val servicio: ServicioRutas, private val calculorRuta: CalculadorRutas) {

    private val builder = RutaBuilder()

    // Valores que se introducen por el usuario
    fun setInicio(inicio: LugarInteres) = apply { builder.setInicio(inicio) }
    fun setFin(fin: LugarInteres) = apply { builder.setFin(fin) }
    fun setVehiculo(vehiculo: Vehiculo) = apply { builder.setVehiculo(vehiculo) }
    fun setTipo(tipo: TipoRuta) = apply { builder.setTipo(tipo) }

    // Método para construir y guardar la ruta
    @Throws(IllegalArgumentException::class)
    fun buildAndSave(): Ruta {

        // Hacer los cálculos necesarios aquí
        calculorRuta.terminarRuta(builder)

        // Crear la ruta y guardarla
        val ruta = builder.getRuta()
        servicio.addRuta(ruta)
        return ruta
    }

}