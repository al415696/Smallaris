package es.uji.smallaris.model

import es.uji.smallaris.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request

object ServicioAPIs {

    private var servicioORS: ServicioORS = ServicioORS()
    private var servicioPrecios: IServicioPrecios = ProxyPrecios()

    // Métodos públicos para inyectar dependencias durante las pruebas
    fun setServicioMapa(servicioORS: ServicioORS) {
        this.servicioORS = servicioORS
    }
    fun setServicioPrecios(servicioPrecios: IServicioPrecios) {
        this.servicioPrecios = servicioPrecios
    }

    @Throws(UbicationException::class)
    suspend fun getToponimoCercano(longitud: Double, latitud: Double): String {
        return servicioORS.getToponimoCercano(longitud, latitud)
    }

    @Throws(RouteException::class)
    fun getRuta(inicio: LugarInteres, fin: LugarInteres, tipoRuta: TipoRuta, tipoVehiculo: TipoVehiculo): String {
        return servicioORS.getRuta(inicio, fin, tipoRuta, tipoVehiculo)
    }

    @Throws(UbicationException::class)
    suspend fun getCoordenadas(toponimo: String): Pair<Double, Double> {
        // Retornamos las coordenadas en un par (longitud, latitud)
        return servicioORS.getCoordenadas(toponimo)
    }

    suspend fun getPrecioCombustible(lugar: LugarInteres, tipoVehiculo: TipoVehiculo): Double {
        return when (tipoVehiculo) {
            TipoVehiculo.Electrico -> servicioPrecios.getPrecioElectrico()
            TipoVehiculo.Gasolina95, TipoVehiculo.Gasolina98, TipoVehiculo.Diesel -> servicioPrecios.getPrecioCombustible(lugar, tipoVehiculo)
            TipoVehiculo.Desconocido -> throw VehicleException("Tipo de vehículo no soportado")
            else -> throw VehicleException("No hace falta pedir el precio en bici o pie")
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