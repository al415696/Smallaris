package es.uji.smallaris.model

interface RepositorioVehiculos: Repositorio {
    suspend fun  getVehiculos() : List<Vehiculo>

    suspend fun  addVehiculos(nuevo: Vehiculo) : Boolean

    suspend fun  updateVehiculos(viejo: Vehiculo, nuevo: Vehiculo) : Boolean

    suspend fun setVehiculoFavorito(vehiculo: Vehiculo, favorito: Boolean) :Boolean
}