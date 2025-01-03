package es.uji.smallaris.model

enum class ArquetipoVehiculo {
    Combustible {
        override val unidad: String = "L/100km"
    },
    Electrico {
        override val unidad: String = "kWh/100 km"
    },
    Otro {
        override val unidad: String = "Cal"
    };

    abstract val unidad: String

    fun getAllOfArquetipo(): List<TipoVehiculo> {
        val list = mutableListOf<TipoVehiculo>()
        for (tipo in TipoVehiculo.entries) {
            if (tipo.getArquetipo() == this)
                list.add(tipo)
        }
        return list
    }
}