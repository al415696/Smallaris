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
import kotlin.jvm.Throws

class ServicioPrecioCombustible(private val valueForNotPresent :Double = -1.0) : IServicioPrecioCombustible{
    private lateinit var mapIdMunicipio : Map<String, String>

    @Throws(APIException::class, ConnectionErrorException::class)
    override suspend fun getClosestCarburante(lugar: LugarInteres,spcopeBusqueda: ScopePeticionAPI, tipoVehiculo: TipoVehiculo?) : Combustible{
        var resultadoPeticion:List<Combustible>
        when (spcopeBusqueda) {
            ScopePeticionAPI.Municipal -> {
                if (!this::mapIdMunicipio.isInitialized)
                    mapIdMunicipio = initializeMapIdMunicipio()
                resultadoPeticion = obtenerPreciosCarburantes(lugar)?: throw ConnectionErrorException("No se ha obtenido resupuesta de REST carburantes")
            }
            ScopePeticionAPI.Nacional -> {
                resultadoPeticion = obtenerPreciosCarburantes()?: throw ConnectionErrorException("No se ha obtenido resupuesta de REST carburantes")
            }

        }
        if (resultadoPeticion.isEmpty()) throw APIException("No hay gasolineras en el scope desigando")
        return if (tipoVehiculo == null)
            getClosest(resultadoPeticion, lugar)
        else
            getClosest(resultadoPeticion, lugar, tipoVehiculo)

    }

    suspend fun obtenerPreciosCarburantes(): List<Combustible>? {
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
    suspend fun obtenerPreciosCarburantes(lugar: LugarInteres): List<Combustible>? {
        val url: String
        if (mapIdMunicipio.containsKey(lugar.municipio)){
            url = "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipio/"+ mapIdMunicipio[lugar.municipio]
        }else{
            throw APIException("Municipio actual no presente de dicionario")
        }
        return withContext(Dispatchers.IO) {  // Ejecutar en el hilo de entrada/salida
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
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

    private fun getClosest(listCombustible: List<Combustible>,givenLocation: LugarInteres): Combustible {
        var closest: Combustible = listCombustible[0]
        var distanciaCloseset = givenLocation.distancia(closest.lugar)
        for (gas in listCombustible) {
            if (givenLocation.distancia(gas.lugar) < distanciaCloseset) {
                closest = gas
                distanciaCloseset = givenLocation.distancia(closest.lugar)
            }
        }
        return closest
    }
    @Throws(APIException::class)
    private fun getClosest(listCombustible: List<Combustible>, givenLocation: LugarInteres, tipoVehiculo: TipoVehiculo): Combustible {
        var closest: Combustible = listCombustible[0]
        var distanciaCloseset = givenLocation.distancia(closest.lugar)
        for (gas in listCombustible) {
            if (gas[tipoVehiculo] != valueForNotPresent && givenLocation.distancia(gas.lugar) < distanciaCloseset) {
                closest = gas
                distanciaCloseset = givenLocation.distancia(closest.lugar)
            }
        }
        if (closest[tipoVehiculo] == -1.0)
            throw APIException("No se ha encontrado ninguna gasolinera con el combustible $tipoVehiculo en el scope designado")
        return closest
    }

    private data class ResponseData(
        @SerializedName("Fecha") val fecha: String,
        @SerializedName("ListaEESSPrecio") val gasolineras: List<Combustible>,
    )

    private class CombustibleDeserializer : JsonDeserializer<Combustible> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): Combustible {
            val jsonObject = json.asJsonObject
            val longitud = jsonObject.get("Longitud (WGS84)").asString.replace(",",".").toDouble()
            val latitud = jsonObject.get("Latitud").asString.replace(",",".").toDouble()
            val municipio = jsonObject.get("Municipio").asString
            val nombre =
                municipio + ", " + jsonObject.get("Localidad").asString + ", " + jsonObject.get(
                    "Rótulo"
                ).asString
            val gasolina95 : Double
            val gasolina98 : Double
            val diesel : Double
            jsonObject.get("Precio Gasolina 95 E5").asString.let{
                gasolina95 = getDoubleFromJSONObjectString(it)
            }
            jsonObject.get("Precio Gasolina 98 E5").asString.let{
                gasolina98 = getDoubleFromJSONObjectString(it)
            }
            jsonObject.get("Precio Gasoleo A").asString.let{
                diesel = getDoubleFromJSONObjectString(it)
            }
            return Combustible(
                lugar = LugarInteres(
                    longitud = longitud,
                    latitud = latitud,
                    nombre = nombre,
                    municipio = municipio,
                ),
            ).set(TipoVehiculo.Gasolina95, gasolina95)
                .set(TipoVehiculo.Gasolina98,gasolina98)
                .set(TipoVehiculo.Diesel, diesel)
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
        private fun getDoubleFromJSONObjectString(string: String): Double{
            return if (string.isEmpty()) -1.0 else string.replace(",", ".").toDouble()
        }
    }

    private suspend fun initializeMapIdMunicipio(): Map<String, String> {
        val finalMap = mutableMapOf<String, String>()
        val municipios = getMunicipios() ?: listOf()

        for (municipio: Municipio in municipios){
            if (municipio.nombre.contains("/")){
                for (posibleNombre: String in municipio.nombre.split("/")){
                    finalMap[posibleNombre] = municipio.id
                }
            }else{
                finalMap[municipio.nombre] = municipio.id
            }
        }
        return  finalMap
    }

    private data class Municipio(
        @SerializedName("Municipio") val nombre: String,
        @SerializedName("IDMunicipio") val id: String,
    )

    private suspend fun getMunicipios() : List<Municipio>? {
        return withContext(Dispatchers.IO) {  // Ejecutar en el hilo de entrada/salida
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/Listados/Municipios/")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val builder = GsonBuilder()
                        val gson = builder.create()
                        val data = gson.fromJson(responseBody , Array<Municipio>::class.java).toList()
                        return@withContext data
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null  // En caso de error o fallo en la solicitud
        }
    }

}