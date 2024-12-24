package es.uji.smallaris.model

import com.google.gson.JsonParser
import es.uji.smallaris.BuildConfig
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.lugares.UbicationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

open class ServicioORS {
    suspend fun getToponimoCercano(longitud: Double, latitud: Double): String {
        // Verificamos que las coordenadas estén en el rango correcto
        if (longitud < -180 || longitud > 180 || latitud < -90 || latitud > 90) {
            throw UbicationException("Las coordenadas deben estar entre -180 y 180 grados de longitud y -90 y 90 grados de latitud")
        }

        val client = OkHttpClient()
        val apiKey = BuildConfig.OPENROUTESERVICE_API_KEY
        val url = "https://api.openrouteservice.org/geocode/reverse?api_key=$apiKey&point.lat=$latitud&point.lon=$longitud&lang=es"
        val request = Request.Builder().url(url).build()

        // Ejecutamos la operación de red en un hilo de fondo con Dispatchers.IO
        return withContext(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()

                response.use { resp ->
                    val responseBody = resp.body?.string()
                    if (responseBody != null) {
                        // Parseamos el JSON
                        val jsonElement = JsonParser.parseString(responseBody)
                        val features = jsonElement.asJsonObject.getAsJsonArray("features")

                        // Si encontramos un topónimo relacionado con las coordenadas
                        if (features.size() > 0) {
                            val properties = features[0].asJsonObject.getAsJsonObject("properties")
                            val name = properties.get("name")?.asString ?: "Desconocido"
                            val municipio = properties.get("localadmin")?.asString ?: "Municipio desconocido"
                            val region = properties.get("macroregion")?.asString ?: properties.get("region")?.asString ?: "Región desconocida"
                            val pais = properties.get("country")?.asString ?: "País desconocido"

                            // Retornamos el topónimo formateado
                            return@withContext "$name, $municipio, $region, $pais"
                        } else {
                            // Si no encontramos información, devolvemos una cadena vacía
                            return@withContext "Desconocido, Municipio desconocido, Región desconocida, País desconocido"
                        }
                    } else {
                        // Si la respuesta está vacía, devolvemos una cadena vacía
                        return@withContext "Desconocido, Municipio desconocido, Región desconocida, País desconocido"
                    }
                }
            } catch (e: Exception) {
                // Si ocurre un error, se captura y se lanza una excepción
                return@withContext "Desconocido, Municipio desconocido, Región desconocida, País desconocido"
            }
        }
    }

    suspend fun getCoordenadas(toponimo: String): Pair<Double, Double> {
        val apiKey = BuildConfig.OPENROUTESERVICE_API_KEY  // Sustituye esto con tu clave de API de OpenRouteService
        val client = OkHttpClient()
        val url = "https://api.openrouteservice.org/geocode/search?api_key=$apiKey&text=$toponimo"

        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            // Realizamos la llamada HTTP de forma asíncrona en el contexto de IO
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            // Verifica que la respuesta sea exitosa
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    // Parsear el JSON de la respuesta
                    val jsonElement = JsonParser.parseString(responseBody)
                    val features = jsonElement.asJsonObject.getAsJsonArray("features")

                    // Si encontramos resultados, extraemos las coordenadas
                    if (features.size() > 0) {
                        val coordinates = features[0].asJsonObject
                            .getAsJsonObject("geometry")
                            .getAsJsonArray("coordinates")

                        val lon = coordinates[0].asDouble
                        val lat = coordinates[1].asDouble

                        // Retornamos las coordenadas en un par (longitud, latitud)
                        return Pair(lon, lat)
                    }
                }
            }
            // Si no se encuentra ninguna coordenada, lanzamos una excepción
            throw UbicationException("No se encontraron coordenadas para el topónimo $toponimo")
        } catch (e: Exception) {
            throw UbicationException("Error al obtener las coordenadas de $toponimo: ${e.message}")
        }
    }

    @Throws(RouteException::class, VehicleException::class)
    suspend fun getRuta(inicio: LugarInteres, fin: LugarInteres, tipoRuta: TipoRuta, tipoVehiculo: TipoVehiculo): String {
        // URL base y clave API
        val baseUrl = "https://api.openrouteservice.org/v2/directions"
        val apiKey =
            BuildConfig.OPENROUTESERVICE_API_KEY

        // Determinar el perfil de transporte según el tipo
        val profile = when (tipoVehiculo) {
            TipoVehiculo.Gasolina95 -> "driving-car"
            TipoVehiculo.Gasolina98 -> "driving-car"
            TipoVehiculo.Diesel -> "driving-car"
            TipoVehiculo.Electrico -> "driving-car"
            TipoVehiculo.Pie -> "foot-walking"
            TipoVehiculo.Bici -> "cycling-regular"
            TipoVehiculo.Desconocido -> VehicleException("No se puede calcular una ruta sin un vehiculo adecuado")
        }

        // Crear parámetros adicionales según el tipo de ruta
        val preference = when (tipoRuta) {
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

        // Ejecutar la solicitud de manera asincrónica
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                    ?: throw RouteException("Respuesta vacía de la API ORS para la ruta")

                // Analizar el JSON y verificar si contiene datos de ruta
                val jsonObject = JSONObject(responseBody)
                val features = jsonObject.optJSONArray("features")

                if (features == null || features.length() == 0) {
                    throw RouteException("No se encontró una ruta válida entre los puntos proporcionados")
                }

                responseBody  // Retorna el GeoJSON completo como String
            } else {
                throw RouteException("Error en la solicitud: ${response.code} - ${response.message}")
            }
        }
    }
}