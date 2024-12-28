package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres
import kotlin.jvm.Throws

interface IServicioPrecioCombustible {
//    suspend fun obtenerPreciosCarburantes(): List<Combustible>?
//
//    suspend fun obtenerPreciosCarburantes(lugar: LugarInteres): List<Combustible>?
    @Throws(APIException::class, ConnectionErrorException::class)
    suspend fun getClosestCarburante(lugar: LugarInteres, scopeBusqueda: ScopePeticionAPI= ScopePeticionAPI.Municipal, tipoVehiculo: TipoVehiculo? = null): Combustible
}