package es.uji.smallaris.model

interface IServicioPrecios {
    suspend fun getPrecioGasolina95(lugar: LugarInteres): Float
    suspend fun getPrecioGasolina98(lugar: LugarInteres): Float
    suspend fun getPrecioDiesel(lugar: LugarInteres): Float
    suspend fun getPrecioElectrico(): Float
}