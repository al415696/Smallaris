package es.uji.smallaris.model

interface RepositorioLugares: Repositorio {
    fun getLugares(): List<LugarInteres>
    fun addLugar(lugar: LugarInteres): Boolean
}