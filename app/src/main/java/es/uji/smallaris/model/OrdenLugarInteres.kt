package es.uji.smallaris.model

enum class OrdenLugarInteres{
    FAVORITO_THEN_NOMBRE{
        override fun comparator(): Comparator<LugarInteres>{
            return compareBy<LugarInteres>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.nombre
            }
        }
    },
    NOMBRE{
        override fun comparator(): Comparator<LugarInteres>{
            return compareBy<LugarInteres>{
                it.nombre
            }
        }
    };
    abstract fun comparator(): Comparator<LugarInteres>
}
