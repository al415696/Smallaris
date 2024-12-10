package es.uji.smallaris.model

interface IServicioPrecioElectricidad {
    suspend fun obtenerPrecioMedioElecHoy(): Electricidad

}