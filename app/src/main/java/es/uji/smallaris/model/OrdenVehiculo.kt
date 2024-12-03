package es.uji.smallaris.model

enum class OrdenVehiculo{
    FAVORITO_THEN_NOMBRE{
        override fun comparator(): Comparator<Vehiculo>{
            return compareBy<Vehiculo>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.nombre
            }
        }
    },
    FAVORITO_THEN_MATRICULA{
        override fun comparator(): Comparator<Vehiculo>{
            return compareBy<Vehiculo>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.matricula
            }
        }
    },
    FAVORITO_THEN_TIPO{
        override fun comparator(): Comparator<Vehiculo>{
            return compareBy<Vehiculo>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.tipo
            }
        }
    };
    abstract fun comparator(): java.util.Comparator<in Vehiculo>
}
