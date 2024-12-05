package es.uji.smallaris.model

interface Strategy {
    suspend fun calculaCoste(lugar: LugarInteres, vehiculo: Vehiculo, distancia: Float): Double
}