package es.uji.smallaris.model

class RepositorioFirebase : RepositorioVehiculos, RepositorioLugares, RepositorioUsuarios{
    override fun getVehiculos(): List<Vehiculo> {
        return mutableListOf()
    }

    override fun addVehiculos(nuevo: Vehiculo): Boolean {
        return true
    }

    override fun updateVehiculos(viejo: Vehiculo, nuevo: Vehiculo): Boolean {
        return false
    }

    override fun getLugares(): List<LugarInteres> {
        return mutableListOf()
    }

    override fun addLugar(lugar: LugarInteres): Boolean {
        return true
    }

    override fun registrarUsuario(correo: String, contrasena: String): Usuario {
        TODO("Not yet implemented")
    }

    override fun iniciarSesion(correo: String, contrasena: String): Usuario {
        TODO("Not yet implemented")
    }
}