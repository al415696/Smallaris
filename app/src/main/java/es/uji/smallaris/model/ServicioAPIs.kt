package es.uji.smallaris.model

import es.uji.smallaris.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object ServicioAPIs {

    private val servicioORS: ServicioORS = ServicioORS()
    private val servicioPrecios: IServicioPrecios = ProxyPrecios()


    fun getToponimoCercano(longitud: Double, latitud: Double): String {
        return servicioORS.getToponimoCercano(longitud, latitud)
    }

    fun getRuta(inicio: LugarInteres, fin: LugarInteres, tipo: TipoRuta): String {
        return servicioORS.getRuta(inicio, fin, tipo)
    }

    suspend fun getPrecioCombustible(lugar: LugarInteres, tipoVehiculo: TipoVehiculo): Float {
        when (tipoVehiculo) {
            TipoVehiculo.Gasolina95 -> return servicioPrecios.getPrecioGasolina95(lugar)
            TipoVehiculo.Gasolina98 -> return servicioPrecios.getPrecioGasolina98(lugar)
            TipoVehiculo.Diesel -> return servicioPrecios.getPrecioDiesel(lugar)
            TipoVehiculo.Electrico -> return servicioPrecios.getPrecioElectrico()
            else -> throw VehicleException("Tipo de vehículo no soportado")
        }
    }

    fun apiEnFuncionamiento(servicio: API): Boolean {
        when (servicio) {
            API.TOPONIMO -> return compruebaToponimos()
            API.RUTA -> return compruebaRutas()
            API.COSTE -> return compruebaCoste()
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
}