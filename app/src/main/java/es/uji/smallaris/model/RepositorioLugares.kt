package es.uji.smallaris.model

interface RepositorioLugares {
    fun getLugares(): List<LugarInteres>
    fun addLugar(lugar: LugarInteres): Boolean
}