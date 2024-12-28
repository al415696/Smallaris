package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres

interface IServicioPrecios {
    suspend fun getPrecioCombustible(lugar: LugarInteres, tipoVehiculo: TipoVehiculo): Double
    suspend fun getPrecioElectrico(): Double
}