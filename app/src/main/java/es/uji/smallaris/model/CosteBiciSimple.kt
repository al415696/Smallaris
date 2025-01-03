package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres

class CosteBiciSimple : Strategy {
    override suspend fun calculaCoste(
        lugar: LugarInteres,
        vehiculo: Vehiculo,
        distancia: Float
    ): Double {
        return (distancia * vehiculo.consumo)
    }
}