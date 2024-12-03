package es.uji.smallaris.model

import com.google.gson.JsonParser
import es.uji.smallaris.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class ServicioORS {
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

    @Throws(Exception::class)
    fun getRuta(inicio: LugarInteres, fin: LugarInteres, tipo: TipoRuta): String {
        // URL base y clave API
        val baseUrl = "https://api.openrouteservice.org/v2/directions"
        val apiKey =  BuildConfig.OPENROUTESERVICE_API_KEY // Reemplaza esto con tu clave de API de ORS

        // Determinar el perfil de transporte según el tipo
        val profile = when (tipo) {
            TipoRuta.Rapida -> "driving-car"
            TipoRuta.Corta -> "driving-car"
            TipoRuta.Economica -> "driving-car"
            else -> {"driving-car"}
        }

        // Crear parámetros adicionales según el tipo de ruta
        val preference = when (tipo) {
            TipoRuta.Rapida -> "fastest"

            TipoRuta.Corta -> "shortest"

            TipoRuta.Economica -> "shortest"

            else -> "recommended"
        }

        // Crear el cliente HTTP
        val client = OkHttpClient()

        // Crear el JSON del cuerpo de la solicitud
        val requestBody = """
        {
          "coordinates": [
            [${inicio.longitud}, ${inicio.latitud}],
            [${fin.longitud}, ${fin.latitud}]
          ],
          "preference": "$preference"
        }
    """.trimIndent().toRequestBody("application/json".toMediaType())

        // Crear la solicitud HTTP
        val request = Request.Builder()
            .url("$baseUrl/$profile/geojson")
            .addHeader("Authorization", apiKey)
            .post(requestBody)
            .build()

        // Ejecutar la solicitud y procesar la respuesta
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: throw Exception("Respuesta vacía de la API ORS para la ruta")
            return responseBody  // Retorna el GeoJSON completo como String
        } else {
            throw Exception("Error en la solicitud: ${response.code} - ${response.message}")
        }
    }
}