package es.uji.smallaris.model

import com.mapbox.geojson.LineString

class Ruta(
    private val inicio: LugarInteres,
    private val fin: LugarInteres,
    private val vehiculo: Vehiculo,
    private val tipo: TipoRuta,
    private val trayecto: LineString,
    private val distancia: Float,
    private val duracion: Float,
    private val coste: Double
) {
    fun getTrayecto(): LineString {
        return trayecto
    }

    fun getDistancia(): Float {
        return distancia
    }

    fun getDuracion(): Float {
        return duracion
    }

    fun getCoste(): Double {
        return coste
    }
}