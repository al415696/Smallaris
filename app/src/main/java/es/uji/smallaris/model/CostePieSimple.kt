package es.uji.smallaris.model

class CostePieSimple: Strategy {
    override suspend fun calculaCoste(
        lugar: LugarInteres,
        vehiculo: Vehiculo,
        distancia: Float
    ): Double {
        return (distancia * vehiculo.consumo)
    }
}