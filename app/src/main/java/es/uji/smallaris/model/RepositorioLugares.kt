package es.uji.smallaris.model

interface RepositorioLugares: Repositorio {
    suspend fun getLugares(): List<LugarInteres>
    suspend fun addLugar(lugar: LugarInteres): Boolean
    suspend fun setLugarInteresFavorito(lugar: LugarInteres, favorito: Boolean = true): Boolean
    suspend fun deleteLugar(lugar: LugarInteres): Boolean
}