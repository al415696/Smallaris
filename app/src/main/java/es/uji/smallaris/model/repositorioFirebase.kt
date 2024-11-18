package es.uji.smallaris.model

class repositorioFirebase : RepositorioLugares {
    override fun getLugares(): List<LugarInteres> {
        return mutableListOf()
    }

    override fun addLugar(lugar: LugarInteres): Boolean {
        return true
    }

}