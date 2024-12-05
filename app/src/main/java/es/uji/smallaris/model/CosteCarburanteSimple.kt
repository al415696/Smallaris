package es.uji.smallaris.model

class CosteCarburanteSimple : Strategy {
    override suspend fun calculaCoste(
        lugar: LugarInteres,
        vehiculo: Vehiculo,
        distancia: Float
    ): Double {
        val precioCombustible = ServicioAPIs.getPrecioCombustible(lugar, vehiculo.tipo)
        return ((distancia / 100) * vehiculo.consumo * precioCombustible)
    }

}