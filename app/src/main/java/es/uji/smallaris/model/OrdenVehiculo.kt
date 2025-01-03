package es.uji.smallaris.model

enum class OrdenVehiculo {
    FAVORITO_THEN_NOMBRE {
        private val nombre = "Nombre"
        override fun comparator(): Comparator<Vehiculo> {
            return compareBy<Vehiculo> {
                if (it.isFavorito()) 0 else 1
            }.thenBy {
                it.nombre
            }
        }

        override fun getNombre(): String {
            return nombre
        }
    },
    FAVORITO_THEN_MATRICULA {
        private val nombre = "Matrícula"
        override fun comparator(): Comparator<Vehiculo> {
            return compareBy<Vehiculo> {
                if (it.isFavorito()) 0 else 1
            }.thenBy {
                it.matricula
            }
        }

        override fun getNombre(): String {
            return nombre
        }
    },
    FAVORITO_THEN_TIPO {
        private val nombre = "Tipo de vehículo"
        override fun comparator(): Comparator<Vehiculo> {
            return compareBy<Vehiculo> {
                if (it.isFavorito()) 0 else 1
            }.thenBy {
                it.tipo.name
            }
        }

        override fun getNombre(): String {
            return nombre
        }
    };

    abstract fun comparator(): Comparator<Vehiculo>
    abstract fun getNombre(): String
}
