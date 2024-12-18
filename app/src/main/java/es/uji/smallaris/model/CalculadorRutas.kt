package es.uji.smallaris.model

import com.mapbox.geojson.LineString

abstract class CalculadorRutas {

    private lateinit var strategy: Strategy

    fun setStrategy(strategy: Strategy) {
        this.strategy = strategy
    }

    suspend fun terminarRuta(builder: RutaBuilder) {
        // 1º paso: calcular trayecto, distancia y duración --> ORS
        val (trayecto, distancia, duracion) = calcularTrayecto(
            builder.getInicio(),
            builder.getFin(),
            builder.getTipo()
        )
        builder.setTrayecto(trayecto)
        builder.setDistancia(distancia) // Para que sea en KM
        builder.setDuracion(duracion)

        // 2º paso: calcular coste --> API de precios
        val vehiculo = builder.getVehiculo()
        val lugar = builder.getInicio()
        val coste: Double = calcularCoste(lugar, distancia, vehiculo)
        builder.setCoste(coste)
    }


    abstract fun calcularTrayecto(
        inicio: LugarInteres,
        fin: LugarInteres,
        tipo: TipoRuta
    ): Triple<LineString, Float, Float>

    suspend fun calcularCoste(
        lugar: LugarInteres,
        distancia: Float,
        vehiculo: Vehiculo
    ): Double {
        return strategy.calculaCoste(lugar, vehiculo, distancia)
    }
}