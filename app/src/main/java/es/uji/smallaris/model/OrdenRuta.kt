package es.uji.smallaris.model

enum class OrdenRuta{
    FAVORITO_THEN_NOMBRE{
        override fun comparator(): Comparator<Ruta>{
            return compareBy<Ruta>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.getNombre()
            }
        }
    },
    FAVORITO_THEN_DURACION{
        override fun comparator(): Comparator<Ruta>{
            return compareBy<Ruta>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.getDuracion()
            }
        }
    },
    FAVORITO_THEN_DISTANCIA{
        override fun comparator(): Comparator<Ruta>{
            return compareBy<Ruta>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.getDistancia()
            }
        }
    },
    FAVORITO_THEN_COSTE{
        override fun comparator(): Comparator<Ruta>{
            return compareBy<Ruta>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.getCoste()
            }
        }
    };
    abstract fun comparator(): Comparator<Ruta>
}
