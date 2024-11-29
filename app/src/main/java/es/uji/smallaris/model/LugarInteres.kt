package es.uji.smallaris.model

class LugarInteres(val longitud: Double, val latitud: Double, val nombre: String) : Favoritable() {

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

    override fun toString(): String {
        return "LugarInteres(longitud=$longitud, latitud=$latitud, nombre='$nombre')"
    }
}
