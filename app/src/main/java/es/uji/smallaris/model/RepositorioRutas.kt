package es.uji.smallaris.model

interface RepositorioRutas: Repositorio {
    suspend fun getRutas(): List<Ruta>
    suspend fun addRuta(ruta: Ruta): Boolean
}
