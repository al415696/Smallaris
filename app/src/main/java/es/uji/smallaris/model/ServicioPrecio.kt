package es.uji.smallaris.model

import kotlin.jvm.Throws


class ServicioPrecio{

    private val valueForNotPresent = -1.0 // valor negativo
    private val servicioPrecioCombustible: IServicioPrecioCombustible = ServicioPrecioCombustible(valueForNotPresent)
    private val servicioPrecioElectricidad: IServicioPrecioElectricidad = ServicioPrecioElectricidad(valueForNotPresent)

    @Throws(APIException::class, ConnectionErrorException::class)
    suspend fun getPrecioCombustible(lugar: LugarInteres, tipoVehiculo: TipoVehiculo? = null): Combustible {
        return try {
            servicioPrecioCombustible.getClosestCarburante(lugar = lugar, scopeBusqueda = ScopePeticionAPI.Municipal, tipoVehiculo= tipoVehiculo)
        } catch (e: APIException) {
            servicioPrecioCombustible.getClosestCarburante(lugar = lugar, scopeBusqueda = ScopePeticionAPI.Nacional, tipoVehiculo= tipoVehiculo)
        }
    }
    @Throws(APIException::class, ConnectionErrorException::class)
    suspend fun getPrecioElecticidad(): Double {
        return servicioPrecioElectricidad.obtenerPrecioMedioElecHoy().precio
    }
}




