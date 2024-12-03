package es.uji.smallaris.model

interface IServicioPrecios {
    fun getPrecioGasolina95(lugar: LugarInteres): Float
    fun getPrecioGasolina98(lugar: LugarInteres): Float
    fun getPrecioDiesel(lugar: LugarInteres): Float
    fun getPrecioElectrico(): Float
}