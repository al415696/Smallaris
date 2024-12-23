package es.uji.smallaris.model

enum class OrdenLugarInteres{
    FAVORITO_THEN_NOMBRE{
        private val nombre = "Nombre"

        override fun comparator(): Comparator<LugarInteres>{
            return compareBy<LugarInteres>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.nombre
            }
        }
        override fun getNombre(): String {
            return nombre
        }
    },
    FAVORITO_THEN_MUNICIPIO{
        private val nombre = "Municipio"

        override fun comparator(): Comparator<LugarInteres>{
            return compareBy<LugarInteres>{
                if (it.isFavorito()) 0 else 1
            }.thenBy{
                it.municipio
            }
        }
        override fun getNombre(): String {
            return nombre
        }
    };
    abstract fun comparator(): Comparator<LugarInteres>
    abstract fun getNombre(): String

}
