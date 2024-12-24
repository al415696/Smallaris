package es.uji.smallaris.model

class RutaBuilderWrapper(
    private val calculadorRutas: CalculadorRutas,
    private val servicioRutasYCoste: ServicioAPIs,
    private val servicioRutas: ServicioRutas
) {

    private val builder = RutaBuilder()

    // Valores que se introducen por el usuario
    fun setInicio(inicio: LugarInteres) = apply { builder.setInicio(inicio) }
    fun setFin(fin: LugarInteres) = apply { builder.setFin(fin) }
    fun setVehiculo(vehiculo: Vehiculo) = apply { builder.setVehiculo(vehiculo) }
    fun setTipo(tipo: TipoRuta) = apply { builder.setTipo(tipo) }
    fun setNombre(nombre: String) = apply { builder.setNombre(nombre) }

    // Método para terminar de construir y guardar la ruta
    @Throws(VehicleException::class, UbicationException::class, RouteException::class)
    suspend fun build(): Ruta {

        if (builder.getInicio().nombre == "") {
            throw UbicationException("El origen no puede estar vacío")
        }

        if (builder.getFin().nombre == "") {
            throw UbicationException("El destino no puede estar vacío")
        }

        if (builder.getVehiculo().tipo == TipoVehiculo.Desconocido) {
            throw VehicleException("Debes configurar un vehiculo")
        }

        if (builder.getTipo() == TipoRuta.Desconocida) {
            throw RouteException("Debes configurar un tipo de ruta")
        }

        if (builder.getRuta().getNombre() == "") {
            throw RouteException("Debes configurar un nombre para la ruta")
        }

        if (servicioRutas.contains(builder.getRuta())) {
            throw RouteException("La ruta ya existe")
        }


        // Determinar la estrategia para obtener el coste de la ruta
        when (builder.getVehiculo().tipo) {
            TipoVehiculo.Pie -> calculadorRutas.setStrategy(CostePieSimple())
            TipoVehiculo.Bici -> calculadorRutas.setStrategy(CosteBiciSimple())
            TipoVehiculo.Electrico -> calculadorRutas.setStrategy(
                CosteElectricoSimple(
                    servicioRutasYCoste
                )
            )
            TipoVehiculo.Gasolina95, TipoVehiculo.Gasolina98, TipoVehiculo.Diesel -> calculadorRutas.setStrategy(
                CosteCarburanteSimple(servicioRutasYCoste)
            )

            else -> throw VehicleException("Tipo de vehículo no válido")
        }

        calculadorRutas.terminarRuta(builder)

        // Crear la ruta y devolverla
        return builder.getRutaCalculada()
    }

    fun getRuta(): Ruta {
        return builder.getRuta()
    }

}