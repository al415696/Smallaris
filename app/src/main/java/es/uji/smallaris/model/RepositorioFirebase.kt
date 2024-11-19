package es.uji.smallaris.model

class RepositorioFirebase : RepositorioLugares {
    override fun getLugares(): List<LugarInteres> {
        return mutableListOf()
    }

    override fun addLugar(lugar: LugarInteres): Boolean {
        return true
    }

}