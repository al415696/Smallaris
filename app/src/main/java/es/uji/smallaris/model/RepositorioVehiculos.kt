package es.uji.smallaris.model

interface RepositorioVehiculos {
    fun  getVehiculos() : List<Vehiculo>

    fun  addVehiculos(nuevo: Vehiculo) : Boolean

    fun  updateVehiculos(viejo: Vehiculo, nuevo: Vehiculo) : Boolean
}