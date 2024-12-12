package es.uji.smallaris.model

class CosteBiciSimple: Strategy {
    override suspend fun calculaCoste(
        lugar: LugarInteres,
        vehiculo: Vehiculo,
        distancia: Float
    ): Double {
        return (distancia * vehiculo.consumo)
    }
}