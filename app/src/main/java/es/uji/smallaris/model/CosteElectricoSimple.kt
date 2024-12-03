package es.uji.smallaris.model

class CosteElectricoSimple: Strategy {
    override fun calculaCoste(
        lugar: LugarInteres,
        vehiculo: Vehiculo,
        distancia: Float
    ): Double {
        val precioElectrico = ServicioAPIs.getPrecioCombustible(lugar, vehiculo.tipo)
        return ((distancia / 100) * vehiculo.consumo * precioElectrico/1000)
    }

}