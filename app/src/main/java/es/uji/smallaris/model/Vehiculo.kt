package es.uji.smallaris.model

class Vehiculo : Favoritable {
    lateinit var nombre: String
    var consumo: Double = 0.0
    lateinit var matricula: String
    lateinit var tipo: TipoVehiculo

    constructor(
        nombre: String,
        consumo: Double = -1.0,
        matricula: String,
        tipo: TipoVehiculo,
        favorito: Boolean = false
    ) {
        this.nombre = nombre
        when (tipo) {
            TipoVehiculo.Pie -> {
                this.consumo = 50.0
            }

            TipoVehiculo.Bici -> {
                this.consumo = 30.0
            }

            else -> {
                this.consumo = consumo
            }
        }
        this.matricula = matricula
        this.tipo = tipo
        this.setFavorito(favorito)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vehiculo

        if (nombre != other.nombre) return false
        if (consumo != other.consumo) return false
        if (matricula != other.matricula) return false
        if (tipo != other.tipo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nombre.hashCode()
        result = 31 * result + consumo.hashCode()
        result = 31 * result + matricula.hashCode()
        result = 31 * result + tipo.hashCode()
        return result
    }


}