package es.uji.smallaris.model

class RepositorioFirebase : RepositorioVehiculos, RepositorioLugares, RepositorioUsuarios, Repositorio{
    override fun getVehiculos(): List<Vehiculo> {
        TODO("Not yet implemented")
    }

    override fun addVehiculos(nuevo: Vehiculo): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateVehiculos(viejo: Vehiculo, nuevo: Vehiculo): Boolean {
        TODO("Not yet implemented")
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

    override fun enFuncionamiento(): Boolean {
        return false
    }
}