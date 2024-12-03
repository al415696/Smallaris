package es.uji.smallaris.model

data class Combustible(
    val lugar: LugarInteres,
    val gasolina95: Double,
    val gasolina98: Double,
    val diesel: Double,
    val timestamp: Long = System.currentTimeMillis()
)

