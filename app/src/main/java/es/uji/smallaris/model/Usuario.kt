package es.uji.smallaris.model

data class Usuario(val correo: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Usuario) return false

        return correo == other.correo
    }

    override fun hashCode(): Int {
        return correo.hashCode()
    }
}