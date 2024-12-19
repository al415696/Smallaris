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
    private val coste: Double,
    private val nombre: String
) : Favoritable() {
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
    fun getNombre(): String {
        return nombre
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ruta

        if (inicio != other.inicio) return false
        if (fin != other.fin) return false
        if (vehiculo != other.vehiculo) return false
        if (tipo != other.tipo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = inicio.hashCode()
        result = 31 * result + fin.hashCode()
        result = 31 * result + vehiculo.hashCode()
        result = 31 * result + tipo.hashCode()
        return result
    }
}