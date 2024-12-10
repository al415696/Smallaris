package es.uji.smallaris.model

class RutaBuilderWrapper(private val servicio: ServicioRutas, private val calculorRuta: CalculadorRutas) {

    private val builder = RutaBuilder()

    // Valores que se introducen por el usuario
    fun setInicio(inicio: LugarInteres) = apply { builder.setInicio(inicio) }
    fun setFin(fin: LugarInteres) = apply { builder.setFin(fin) }
    fun setVehiculo(vehiculo: Vehiculo) = apply { builder.setVehiculo(vehiculo) }
    fun setTipo(tipo: TipoRuta) = apply { builder.setTipo(tipo) }
    fun setNombre(nombre: String) = apply { builder.setNombre(nombre) }

    // Método para terminar de construir y guardar la ruta
    @Throws(VehicleException::class)
    suspend fun buildAndSave(): Ruta {

        // Hacer los cálculos necesarios aquí
        when(builder.getVehiculo().tipo) {
            TipoVehiculo.Electrico -> calculorRuta.setStrategy(CosteElectricoSimple())
            TipoVehiculo.Pie -> calculorRuta.setStrategy(CostePieSimple())
            TipoVehiculo.Bici -> calculorRuta.setStrategy(CosteBiciSimple())
            TipoVehiculo.Gasolina95, TipoVehiculo.Gasolina98, TipoVehiculo.Diesel -> calculorRuta.setStrategy(CosteCarburanteSimple())
            else -> throw VehicleException("Tipo de vehículo no válido")
        }

        if (builder.getInicio().nombre == "" ) {
            throw UbicationException("El origen no puede estar vacío")
        }

        if (builder.getFin().nombre == "") {
            throw UbicationException("El destino no puede estar vacío")
        }

        calculorRuta.terminarRuta(builder)

        // Crear la ruta y guardarla
        val ruta = builder.getRuta()
        return ruta
    }

}