package es.uji.smallaris.model

import com.mapbox.geojson.LineString

abstract class CalculadorRutas {

    fun terminarRuta(builder: RutaBuilder) {
        val (trayecto, distancia, duracion) = calcularTrayecto(builder)
        builder.setTrayecto(trayecto)
        builder.setDistancia(distancia/1000) // Para que sea en KM
        builder.setDuracion(duracion)
        val coste: Float = calcularCoste()
        builder.setCoste(coste)
    }

    abstract fun calcularTrayecto(builder: RutaBuilder): Triple<LineString, Float, Float>

    abstract fun calcularCoste(): Float
}