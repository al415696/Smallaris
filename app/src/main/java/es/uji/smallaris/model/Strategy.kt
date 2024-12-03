package es.uji.smallaris.model

interface Strategy {
    fun calculaCoste(lugar: LugarInteres, vehiculo: Vehiculo, distancia: Float): Double
}