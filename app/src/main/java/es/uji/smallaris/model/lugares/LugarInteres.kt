package es.uji.smallaris.model.lugares

import es.uji.smallaris.model.Favoritable
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class LugarInteres(
    val longitud: Double,
    val latitud: Double,
    val nombre: String,
    val municipio: String
) : Favoritable() {

    fun toMap(): Map<String, Any> {
        return mapOf(
            "longitud" to longitud,
            "latitud" to latitud,
            "nombre" to nombre,
            "municipio" to municipio,
            "favorito" to this.isFavorito()
        )
    }

    private fun redondear(valor: Double): Double {
        val factor = 10.0.pow(5.0)
        return Math.round(valor * factor) / factor
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LugarInteres

        // Redondear longitud y latitud a 5 decimales
        if (redondear(longitud) != redondear(other.longitud)) return false
        if (redondear(latitud) != redondear(other.latitud)) return false
        return true
    }

    override fun toString(): String {
        return "LugarInteres(longitud=$longitud, latitud=$latitud, nombre='$nombre', municipio='$municipio')"
    }

    // Método companion que convierte un String a un objeto LugarInteres
    companion object {
        fun fromString(lugarString: String): LugarInteres {
            // Expresión regular para extraer los valores del String incluyendo municipio
            val regex =
                """LugarInteres\(longitud=(.*?), latitud=(.*?), nombre='(.*?)', municipio='(.*?)'\)""".toRegex()

            val matchResult = regex.find(lugarString)

            if (matchResult != null) {
                val (longitud, latitud, nombre, municipio) = matchResult.destructured

                // Crear un objeto LugarInteres con los valores extraídos
                return LugarInteres(
                    longitud = longitud.toDouble(),
                    latitud = latitud.toDouble(),
                    nombre = nombre,
                    municipio = municipio
                )
            } else {
                throw IllegalArgumentException("El formato del String no es válido.")
            }
        }
    }

    fun distancia(otro: LugarInteres): Double {
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

    override fun hashCode(): Int {
        var result = longitud.hashCode()
        result = 31 * result + latitud.hashCode()
        return result
    }
}
