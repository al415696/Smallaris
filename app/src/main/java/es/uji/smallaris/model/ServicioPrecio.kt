package es.uji.smallaris.model

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import javax.xml.parsers.DocumentBuilderFactory
import com.google.gson.*

class ServicioPrecio{
    private val TTL_CARBURANTE = 30 * 60 * 1000L // 30 minutos en milisegundos
    private val TTL_ELECTRICIDAD = 24 * 60 * 60 * 1000L // 1 día en milisegundos
    private var lastCombustibleUpdate = 0L
    private var listAllCombustibles: List<Combustible> = listOf()


    suspend fun getPrecioCombustible(lugar: LugarInteres): Combustible {

        if ((System.currentTimeMillis() - lastCombustibleUpdate) >= TTL_CARBURANTE)
            updateListCombustibles()
        return getClosest(lugar)


    }

    suspend fun getPrecioElecticidad(): Float {
        val todaysElec = obtenerPrecioMedioElec()

        println(todaysElec?.date)
        println(todaysElec?.market)
        println(todaysElec?.units)
        println(todaysElec?.price)

        if (todaysElec != null) {
            return todaysElec.price.toFloat()
        }else{
            return -1F
        }
    }
    private suspend fun updateListCombustibles(){
        val resultadoPeticion = obtenerPreciosCarburantes()
        if (resultadoPeticion == null){
            throw ConnectionErrorException("No se ha obtenido resupuesta de REST carburantes")
        }else{
            listAllCombustibles = resultadoPeticion
            lastCombustibleUpdate = System.currentTimeMillis()
        }
    }

    private fun getClosest(givenLocation: LugarInteres): Combustible {
        var closest: Combustible = listAllCombustibles[0]
        var distanciaCloseset = givenLocation.distancia(closest.lugar)
        for (gas in listAllCombustibles) {
            if (givenLocation.distancia(gas.lugar) < distanciaCloseset) {
                closest = gas
                distanciaCloseset = givenLocation.distancia(closest.lugar)
            }
        }
        return closest
    }

    private data class ResponseData(
        @SerializedName("Fecha") val fecha: String,
        @SerializedName("ListaEESSPrecio") val gasolineras: List<Combustible>,
    )
    private suspend fun obtenerPreciosCarburantes(): List<Combustible>? {
        return withContext(Dispatchers.IO) {  // Ejecutar en el hilo de entrada/salida
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    if (responseBody != null) {
                        var builder = GsonBuilder()
                        builder =
                            builder.registerTypeAdapter(
                                Combustible::class.java,
                                CombustibleDeserializer()
                            )
                        val gson = builder.create()
                        val data = gson.fromJson(responseBody, ResponseData::class.java)
                        return@withContext data.gasolineras
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null  // En caso de error o fallo en la solicitud
        }
    }
    private class CombustibleDeserializer : JsonDeserializer<Combustible> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Combustible {
            val jsonObject = json.asJsonObject
            val longitud = jsonObject.get("Longitud (WGS84)").asString.replace(",",".").toDouble()
            val latitud = jsonObject.get("Latitud").asString.replace(",",".").toDouble()
            val nombre =
                jsonObject.get("Municipio").asString + ", " + jsonObject.get("Localidad").asString + ", " + jsonObject.get(
                    "Rótulo"
                ).asString
            val gasolina95 : Double
            val gasolina98 : Double
            val diesel : Double
            jsonObject.get("Precio Gasolina 95 E10").asString.let{
                gasolina95 = if(it.isEmpty()) -1.0 else it.replace(",",".").toDouble()
            }
            jsonObject.get("Precio Gasolina 98 E10").asString.let{
                gasolina98 =if (it.isEmpty()) -1.0 else it.replace(",", ".").toDouble()
            }
            jsonObject.get("Precio Gasoleo A").asString.let{
                diesel = if (it.isEmpty()) -1.0 else it.replace(",", ".")
                        .toDouble()
            }
            return Combustible(
                lugar = LugarInteres(
                    longitud = longitud,
                    latitud = latitud,
                    nombre = nombre,
                    municipio = "municipio",
                ),
                gasolina95 = gasolina95,
                gasolina98 = gasolina98,
                diesel = diesel
            )
            /*
            cp = jsonObject.get("C.P."),
            direccion = jsonObject.get("Dirección"),
            horario = jsonObject.get("Horario"),
            latitud = jsonObject.get("Latitud"),
            localidad = jsonObject.get("Localidad"),
            longitud = jsonObject.get("Longitud (WGS84)"),
            margen = jsonObject.get("Margen"),
            municipio = jsonObject.get("Municipio"),
            precioBiodiesel = jsonObject.get("Precio_x0020_Biodiesel"),
            precioBioetanol = jsonObject.get("Precio_x0020_Bioetanol"),
            precioGasNaturalComprimido = jsonObject.get("Precio_x0020_Gas_x0020_Natural_x0020_Comprimido"),
            precioGasNaturalLicuado = jsonObject.get("Precio_x0020_Gas_x0020_Natural_x0020_Licuado"),
            precioGLP = jsonObject.get("Precio_x0020_Gases_x0020_licuados_x0020_del_x0020_petróleo"),
            precioGasoleoA = jsonObject.get("Precio_x0020_Gasoleo_x0020_A"),
            precioGasoleoB = jsonObject.get("Precio_x0020_Gasoleo_x0020_B"),
            precioGasoleoPremium = jsonObject.get("Precio_x0020_Gasoleo_x0020_Premium"),
            precioGasolina95E10 = jsonObject.get("Precio_x0020_Gasolina_x0020_95_x0020_E10"),
            precioGasolina95E5 = jsonObject.get("Precio_x0020_Gasolina_x0020_95_x0020_E5"),
            precioGasolina95E5Premium = jsonObject.get("Precio_x0020_Gasolina_x0020_95_x0020_E5_x0020_Premium"),
            precioGasolina98E10 = jsonObject.get("Precio_x0020_Gasolina_x0020_98_x0020_E10"),
            precioGasolina98E5 = jsonObject.get("Precio_x0020_Gasolina_x0020_98_x0020_E5"),
            precioHidrogeno = jsonObject.get("Precio_x0020_Hidrogeno"),
            provincia = jsonObject.get("Provincia"),
            remision = jsonObject.get("Remisión"),
            rotulo = jsonObject.get("Rótulo"),
            tipoVenta = jsonObject.get("Tipo_x0020_Venta"),
            porcentajeBioetanol = jsonObject.get("_x0025__x0020_BioEtanol"),
            porcentajeEsterMetilico = jsonObject.get("_x0025__x0020_Éster_x0020_metílico"),
            ideess = jsonObject.get("IDEESS"),
            idMunicipio = jsonObject.get("IDMunicipio"),
            idProvincia = jsonObject.get("IDProvincia"),
            idCCAA = jsonObject.get("IDCCAA")
             */
        }
    }

    data class Elec(
        @SerializedName("date") val date: String,
        @SerializedName("market") val market: String,
        @SerializedName("price") val price: Double,
        @SerializedName("units") val units: String

    )

    // simple deserializer that always returns object with value x = 4444
    class ElecDeserializer : JsonDeserializer<Elec> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Elec? {

            var jsonObject = json?.asJsonObject

            if (jsonObject != null) {
                val date: String = jsonObject.get("date").asString
                val market: String = jsonObject.get("market").asString
                val price: Double = jsonObject.get("price").asString.replace(",", ".").toDouble()
                val units: String = jsonObject.get("units").asString
                return Elec(
                    date,
                    market,
                    price,
                    units
                )

            }
            return null
        }
    }

    // Función suspendida para realizar la petición HTTP y obtener los datos
    suspend fun obtenerPrecioMedioElec(): Elec? {
        return withContext(Dispatchers.IO) {  // Ejecutar en el hilo de entrada/salida
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.preciodelaluz.org/v1/prices/avg?zone=PCB") // Precio elec medio
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        var builder = GsonBuilder()
                        builder =
                            builder.registerTypeAdapter(
                                Elec::class.java,
                                ElecDeserializer()
                            )
                        val gson = builder.create()
                        val data = gson.fromJson(responseBody, Elec::class.java)

                        return@withContext data
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            println("Nada obtenido")
            return@withContext null  // En caso de error o fallo en la solicitud
        }
    }

}