package es.uji.smallaris.model

class ServicioPrecio{
    fun getPrecioCombustible(lugar: LugarInteres): Combustible {
        return Combustible(
            lugar,
            gasolina95 = 1.5,
            gasolina98 = 1.7,
            diesel = 1.8,
            timestamp = System.currentTimeMillis()
        )

    }

    fun getPrecioElecticidad(): Float {
        return 100.0f
    }
}