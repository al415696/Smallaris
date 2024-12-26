package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres

data class Combustible(
    val lugar: LugarInteres,
//    val gasolina95: Double,
//    val gasolina98: Double,
//    val diesel: Double,
    val timestamp: Long = System.currentTimeMillis()
){
    private val tiposCombustible : MutableMap<TipoVehiculo, Double> =
        mutableMapOf(
            TipoVehiculo.Gasolina95 to -1.0,
            TipoVehiculo.Gasolina98 to -1.0,
            TipoVehiculo.Diesel to -1.0,
            )
    operator fun get(key: TipoVehiculo) : Double{
        return tiposCombustible[key] ?: -1.0
    }
    operator fun set(key: TipoVehiculo, price: Double): Combustible{
        tiposCombustible[key] = price
        return this
    }
}

