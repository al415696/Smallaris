package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres

class CostePieSimple : Strategy {
    override suspend fun calculaCoste(
        lugar: LugarInteres,
        vehiculo: Vehiculo,
        distancia: Float
    ): Double {
        return (distancia * vehiculo.consumo)
    }
}