package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres

interface IServicioPrecioCombustible {
    @Throws(APIException::class, ConnectionErrorException::class)
    suspend fun getClosestCarburante(
        lugar: LugarInteres,
        scopeBusqueda: ScopePeticionAPI = ScopePeticionAPI.Municipal,
        tipoVehiculo: TipoVehiculo? = null
    ): Combustible
}