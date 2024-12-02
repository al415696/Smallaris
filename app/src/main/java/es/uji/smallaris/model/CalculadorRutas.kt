package es.uji.smallaris.model

import com.mapbox.geojson.LineString

abstract class CalculadorRutas {

    fun terminarRuta(builder: RutaBuilder) {
        val (trayecto, distancia, duracion) = calcularTrayecto()
        builder.setTrayecto(trayecto)
        builder.setDistancia(distancia)
        builder.setDuracion(duracion)
        val coste: Float = calcularCoste()
        builder.setCoste(coste)
    }

    abstract fun calcularTrayecto(): Triple<LineString, Float, Float>

    abstract fun calcularCoste(): Float
}