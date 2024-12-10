package es.uji.smallaris.model

import es.uji.smallaris.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request

object ServicioAPIs {

    private val servicioORS: ServicioORS = ServicioORS()
    private val servicioPrecios: IServicioPrecios = ProxyPrecios()

    @Throws(UbicationException::class)
    fun getToponimoCercano(longitud: Double, latitud: Double): String {
        return servicioORS.getToponimoCercano(longitud, latitud)
    }

    @Throws(RouteException::class)
    fun getRuta(inicio: LugarInteres, fin: LugarInteres, tipoRuta: TipoRuta, tipoVehiculo: TipoVehiculo): String {
        return servicioORS.getRuta(inicio, fin, tipoRuta, tipoVehiculo)
    }

    @Throws(UbicationException::class)
    fun getCoordenadas(toponimo: String): Pair<Double, Double> {
        return servicioORS.getCoordenadas(toponimo)
    }

    suspend fun getPrecioCombustible(lugar: LugarInteres, tipoVehiculo: TipoVehiculo): Double {
        return when (tipoVehiculo) {
            TipoVehiculo.Desconocido -> throw VehicleException("Tipo de vehículo no soportado")
            TipoVehiculo.Electrico -> servicioPrecios.getPrecioElectrico()
            else -> servicioPrecios.getPrecioCombustible(lugar, tipoVehiculo)
        }
    }


    fun apiEnFuncionamiento(servicio: API): Boolean {
        return when (servicio) {
            API.TOPONIMO -> compruebaToponimos()
            API.RUTA -> compruebaRutas()
            API.COSTE -> compruebaCoste()
            API.COORDS -> compruebaCoords()
        }
    }

    private fun compruebaToponimos(): Boolean {
        val client = OkHttpClient()
        val apiKey = BuildConfig.OPENROUTESERVICE_API_KEY
        val url =
            "https://api.openrouteservice.org/geocode/reverse?api_key=$apiKey&point.lat=0.0&point.lon=0.0&lang=es"

        // Realizamos la solicitud
        return try {
            val request = Request.Builder().url(url).build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun compruebaRutas(): Boolean {
        return true // Si cambiamos a nuestra propia instancia, se podría comprobar con una solicitud HTTP a https://api.openrouteservice.org/health

    }

    private fun compruebaCoste(): Boolean {
        return true // Quizás se pueda probar, para más adelante
    }

    private fun compruebaCoords(): Boolean {
        return true // Quizás se pueda probar, para más adelante
    }
}