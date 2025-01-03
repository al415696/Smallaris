package es.uji.smallaris.model

enum class OrdenRuta {
    FAVORITO_THEN_NOMBRE {
        private val nombre = "Nombre"

        override fun comparator(): Comparator<Ruta> {
            return compareBy<Ruta> {
                if (it.isFavorito()) 0 else 1
            }.thenBy {
                it.getNombre()
            }
        }

        override fun getNombre(): String {
            return nombre
        }
    },
    FAVORITO_THEN_DURACION {
        private val nombre = "Duracion"

        override fun comparator(): Comparator<Ruta> {
            return compareBy<Ruta> {
                if (it.isFavorito()) 0 else 1
            }.thenBy {
                it.getDuracion()
            }
        }

        override fun getNombre(): String {
            return nombre
        }
    },
    FAVORITO_THEN_DISTANCIA {
        private val nombre = "Distancia"

        override fun comparator(): Comparator<Ruta> {
            return compareBy<Ruta> {
                if (it.isFavorito()) 0 else 1
            }.thenBy {
                it.getDistancia()
            }
        }

        override fun getNombre(): String {
            return nombre
        }
    },
    FAVORITO_THEN_COSTE {
        private val nombre = "Coste"

        override fun comparator(): Comparator<Ruta> {
            return compareBy<Ruta> {
                if (it.isFavorito()) 0 else 1
            }.thenBy {
                it.getCoste()
            }
        }

        override fun getNombre(): String {
            return nombre
        }
    };

    abstract fun comparator(): Comparator<Ruta>
    abstract fun getNombre(): String

}
