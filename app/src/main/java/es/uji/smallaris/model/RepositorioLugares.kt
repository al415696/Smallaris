package es.uji.smallaris.model

interface RepositorioLugares: Repositorio {
    suspend fun getLugares(): List<LugarInteres>
    suspend fun addLugar(lugar: LugarInteres): Boolean
}