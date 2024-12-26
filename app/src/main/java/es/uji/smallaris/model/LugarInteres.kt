package es.uji.smallaris.model

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class LugarInteres(val longitud: Double, val latitud: Double, val nombre: String, val municipio: String) : Favoritable() {

    private fun redondear(valor: Double, decimales: Int): Double {
        val factor = Math.pow(10.0, decimales.toDouble())
        return Math.round(valor * factor) / factor
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LugarInteres

        // Redondear longitud y latitud a 5 decimales
        if (redondear(longitud, 5) != redondear(other.longitud, 5)) return false
        if (redondear(latitud, 5) != redondear(other.latitud, 5)) return false
        if (nombre != other.nombre) return false
        return true
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "longitud" to longitud,
            "latitud" to latitud,
            "nombre" to nombre,
            "municipio" to municipio,
            "favorito" to isFavorito()
        )
    }

    override fun toString(): String {
        return "LugarInteres(longitud=$longitud, latitud=$latitud, nombre='$nombre')"
    }

    fun distancia(otro: LugarInteres): Double{
        val theta = longitud - otro.longitud
        var dist = sin(deg2rad(latitud)) * sin(deg2rad(otro.latitud)) + cos(
            deg2rad(latitud)
        ) * cos(deg2rad(otro.latitud)) * cos(deg2rad(theta))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return (dist)
    }

    private fun deg2rad(deg: Double): Double {
        return (deg * Math.PI / 180.0)
    }

    private fun rad2deg(rad: Double): Double {
        return (rad * 180.0 / Math.PI)
    }
}
