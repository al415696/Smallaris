package es.uji.smallaris.model

data class Usuario(
    val uid: String, // UID que proporciona Firebase
    val correo: String,
    val preferencias: Map<String, Any>? = null // Datos que queremos almacenar en la base de datos
) {
    override fun equals(other: Any?): Boolean { // No verifico el uid para poder realizar los tests
        if (this === other) return true
        if (other !is Usuario) return false

        return correo == other.correo && preferencias == other.preferencias
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}