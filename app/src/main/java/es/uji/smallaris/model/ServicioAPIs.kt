package es.uji.smallaris.model

import es.uji.smallaris.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request

object ServicioAPIs {

    private val servicioToponimos: ServicioToponimos = ServicioToponimos()

    fun getToponimoCercano(longitud: Double, latitud: Double): String {
        return servicioToponimos.getToponimoCercano(longitud, latitud)
    }

    fun apiEnFuncionamiento(servicio: API): Boolean {
        when (servicio) {
            API.TOPONIMO -> return compruebaToponimos()
        }
    }

    private fun compruebaToponimos(): Boolean {
        val client = OkHttpClient()
        val apiKey = BuildConfig.OPENROUTESERVICE_API_KEY
        val url = "https://api.openrouteservice.org/geocode/reverse?api_key=$apiKey&point.lat=0.0&point.lon=0.0&lang=es"

        // Realizamos la solicitud
        return try {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }
}