package es.uji.smallaris.model

interface IServicioPrecios {
    suspend fun getPrecioCombustible(lugar: LugarInteres, tipoVehiculo: TipoVehiculo): Double
    suspend fun getPrecioElectrico(): Double
}