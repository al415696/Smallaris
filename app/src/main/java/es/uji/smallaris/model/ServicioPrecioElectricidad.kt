package es.uji.smallaris.model

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ServicioPrecioElectricidad(private val valueForNotPresent: Double = -1.0) :
    IServicioPrecioElectricidad {
    override suspend fun obtenerPrecioMedioElecHoy(): Electricidad {
        val listElectricidades: List<Electricidad> = obtenerPreciosElecHoy()
            ?: throw ConnectionErrorException("No se ha podido conectar con el API de precio de la Luz")
        if (listElectricidades.isEmpty()) {
            throw APIException("No se han obtenido valores del precio de la luz de hoy")
        } else {
            val electricidadMedia: Electricidad
            var sumPrecioElectricidad = 0.0
            for (elec: Electricidad in listElectricidades) {
                sumPrecioElectricidad += elec.precio
            }
            electricidadMedia = Electricidad(
                precio = sumPrecioElectricidad / listElectricidades.size,
                timestamp = listElectricidades[0].timestamp
            )
            return electricidadMedia
        }
    }

    private data class ElecResponse(
        @SerializedName("included") val included: Array<ElecIncluded>
    )

    private data class ElecIncluded(
        @SerializedName("attributes") val attributes: ElecIncludedAttributes
    )

    private data class ElecIncludedAttributes(
        @SerializedName("values") val values: Array<Electricidad>
    )

    private class ElecIncludedAttributesDeserializer : JsonDeserializer<ElecIncludedAttributes> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): ElecIncludedAttributes? {

            val jsonObject = json?.asJsonObject

            if (jsonObject != null) {
                val builder = GsonBuilder()
                    .registerTypeAdapter(
                        Electricidad::class.java,
                        ElecDeserializer()
                    )
                val gson = builder.create()

                val values = gson.fromJson(
                    jsonObject.get("values").asJsonArray,
                    Array<Electricidad>::class.java
                )

                return ElecIncludedAttributes(
                    values = values
                )
            }
            return null
        }

    }

    private class ElecDeserializer : JsonDeserializer<Electricidad> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Electricidad? {

            val jsonObject = json?.asJsonObject

            if (jsonObject != null) {
                val precio: Double = jsonObject.get("value").asDouble
                val timestamp: Long =
                    getTimeStampFromJSONObjectString(jsonObject.get("datetime").asString)
                return Electricidad(
                    precio = precio,
                    timestamp = timestamp
                )
            }
            return null
        }

        private fun getTimeStampFromJSONObjectString(string: String): Long {
            val editedString = string.replace("T", " ").dropLast(6)

            return Timestamp.valueOf(editedString).time
        }
    }

    // Función suspendida para realizar la petición HTTP y obtener los datos
    private suspend fun obtenerPreciosElecHoy(): List<Electricidad>? {
        return withContext(Dispatchers.IO) {  // Ejecutar en el hilo de entrada/salida
            var dateHoy: String
            try {
                val date = Date()
                val calendar = Calendar.getInstance()
                calendar.setTimeInMillis(date.time)
                dateHoy = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
            } catch (e: Exception) {
                dateHoy = "2024-12-15"
                e.printStackTrace()
            }
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://apidatos.ree.es/es/datos/mercados/precios-mercados-tiempo-real?start_date=${dateHoy}T00:00&end_date=${dateHoy}T23:59&time_trunc=hour") // Precio elec medio
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        var builder = GsonBuilder()
                        builder =
                            builder.registerTypeAdapter(
                                Electricidad::class.java,
                                ElecDeserializer()
                            ).registerTypeAdapter(
                                ElecIncludedAttributes::class.java,
                                ElecIncludedAttributesDeserializer()
                            )
                        val gson = builder.create()
                        val data = gson.fromJson(responseBody, ElecResponse::class.java)

                        return@withContext data.included[0].attributes.values.toList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null  // En caso de error o fallo en la solicitud
        }
    }
}