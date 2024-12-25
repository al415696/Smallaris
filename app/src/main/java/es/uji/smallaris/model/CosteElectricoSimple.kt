package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres

class CosteElectricoSimple(private val servicioPrecios: ServicioAPIs) : Strategy {
    override suspend fun calculaCoste(
        lugar: LugarInteres,
        vehiculo: Vehiculo,
        distancia: Float
    ): Double {
        val precioElectrico = servicioPrecios.getPrecioCombustible(lugar, vehiculo.tipo)
        return ((distancia / 100) * vehiculo.consumo * precioElectrico / 1000)
    }
}