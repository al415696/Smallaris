package es.uji.smallaris.model

interface RepositorioRutas: Repositorio {
    suspend fun getRutas(): List<Ruta>
    suspend fun addRuta(ruta: Ruta): Boolean
    suspend fun setRutaFavorita(ruta: Ruta, favorito:Boolean = true): Boolean
    suspend fun deleteLugar(ruta: Ruta): Boolean
}
