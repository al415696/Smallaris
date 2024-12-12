package es.uji.smallaris.model

interface RepositorioVehiculos: Repositorio {
    fun  getVehiculos() : List<Vehiculo>

    fun  addVehiculos(nuevo: Vehiculo) : Boolean

    fun  updateVehiculos(viejo: Vehiculo, nuevo: Vehiculo) : Boolean

    fun removeVehiculo(vehiculo: Vehiculo) :Boolean

    fun setVehiculoFavorito(vehiculo: Vehiculo, favorito: Boolean) :Boolean
}