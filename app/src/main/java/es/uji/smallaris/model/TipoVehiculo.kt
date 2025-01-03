package es.uji.smallaris.model


enum class TipoVehiculo {
    Gasolina95 {
        override val arquetipoVehiculo: ArquetipoVehiculo = ArquetipoVehiculo.Combustible
    },
    Gasolina98 {
        override val arquetipoVehiculo: ArquetipoVehiculo = ArquetipoVehiculo.Combustible
    },
    Diesel {
        override val arquetipoVehiculo: ArquetipoVehiculo = ArquetipoVehiculo.Combustible
    },
    Desconocido {
        override val arquetipoVehiculo: ArquetipoVehiculo = ArquetipoVehiculo.Otro
    },
    Electrico {
        override val arquetipoVehiculo: ArquetipoVehiculo = ArquetipoVehiculo.Electrico
    },
    Pie {
        override val arquetipoVehiculo: ArquetipoVehiculo = ArquetipoVehiculo.Otro
    },
    Bici {
        override val arquetipoVehiculo: ArquetipoVehiculo = ArquetipoVehiculo.Otro
    };

    abstract val arquetipoVehiculo: ArquetipoVehiculo
    fun getArquetipo(): ArquetipoVehiculo {
        return arquetipoVehiculo
    }
}