package es.uji.smallaris.model

import es.uji.smallaris.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.JsonParser

class ServicioToponimos {
    fun getToponimoCercano(longitud: Double, latitud: Double): String {

        if (longitud < -180 || longitud > 180 || latitud < -90 || latitud > 90) {
            throw UbicationErrorException("Las coordenadas deben estar entre -180 y 180 grados de longitud y -90 y 90 grados de latitud")
        }

        val client = OkHttpClient()
        val apiKey = BuildConfig.OPENROUTESERVICE_API_KEY
        val url = "https://api.openrouteservice.org/geocode/reverse?api_key=$apiKey&point.lat=$latitud&point.lon=$longitud&lang=es"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string()
            if (responseBody != null) {
                // Parseamos el JSON para obtener la respuesta en un formato "tratable"
                val jsonElement = JsonParser.parseString(responseBody)
                val features = jsonElement.asJsonObject.getAsJsonArray("features")
                // Si obtenemos opciones, es decir, algún topónimo relacionado a las coordenadas
                if (features.size() > 0) {
                    // Nos quedamos con las propiedades de la ubicación más representativa
                    val properties = features[0].asJsonObject.getAsJsonObject("properties")
                    // El campo label es una combinación de info del lugar, por ejemplo. Puerta del Sol, Madrid, Spain
                    return properties.get("label").asString
                }
            }
        }
        return ""
    }
}