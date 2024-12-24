package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres

interface Strategy {
    suspend fun calculaCoste(lugar: LugarInteres, vehiculo: Vehiculo, distancia: Float): Double
}