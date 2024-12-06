package es.uji.smallaris.model

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type


class ServicioPrecio{

    private val VALUE_FOR_NOT_PRESENT = -1.0 // 1 día en milisegundos
    private var lastCombustibleUpdate = 0L
    private var listAllCombustibles: List<Combustible> = listOf()
    private lateinit var mapIdMunicipio : Map<String, String>

    suspend fun getPrecioCombustible(lugar: LugarInteres, tipoVehiculo: TipoVehiculo? = null): Combustible {
        if (!this::mapIdMunicipio.isInitialized)
            mapIdMunicipio = initializeMapIdMunicipio()
        val resultadoPeticion = obtenerPreciosCarburantes(lugar)
            ?: throw ConnectionErrorException("No se ha obtenido resupuesta de REST carburantes")
        return if (tipoVehiculo == null)
            getClosest(resultadoPeticion, lugar)
        else
            getClosest(resultadoPeticion, lugar, tipoVehiculo)


    }

    suspend fun getPrecioElecticidad(): Float {
        val todaysElec = obtenerPrecioMedioElec()

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
    private fun getClosest(listCombustible: List<Combustible>, givenLocation: LugarInteres, tipoVehiculo: TipoVehiculo): Combustible {
        var closest: Combustible = listCombustible[0]
        var distanciaCloseset = givenLocation.distancia(closest.lugar)
        for (gas in listCombustible) {
            if (getCarburanteFromTipoVehiculo(gas,tipoVehiculo) != VALUE_FOR_NOT_PRESENT && givenLocation.distancia(gas.lugar) < distanciaCloseset) {
                closest = gas
                distanciaCloseset = givenLocation.distancia(closest.lugar)
            }
        }
        return closest
    }
    private fun  getCarburanteFromTipoVehiculo(combustible: Combustible, tipoVehiculo: TipoVehiculo): Double{
        when (tipoVehiculo) {
            TipoVehiculo.Gasolina95 -> return combustible.gasolina95
            TipoVehiculo.Gasolina98 -> return combustible.gasolina98
            TipoVehiculo.Diesel -> return combustible.diesel
            else -> {
                return VALUE_FOR_NOT_PRESENT
            }
        }
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
    private suspend fun obtenerPreciosCarburantes(lugar: LugarInteres): List<Combustible>? {
        val url: String
        if (mapIdMunicipio.containsKey(lugar.municipio)){
            url = "https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/EstacionesTerrestres/FiltroMunicipio/"+ mapIdMunicipio[lugar.municipio]
        }else{
            return obtenerPreciosCarburantes()
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


    private data class ArrayOfMunicipio (
        @SerializedName("ArrayOfMunicipio") val municipios: List<Municipio>
    )
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
                    var builder = GsonBuilder()
                    val gson = builder.create()
                    val groupListType = object : TypeToken<ArrayList<Municipio?>?>(){}::class.java
                    val data = gson.fromJson(responseBody , Array<Municipio>::class.java).toList()
//                    val data = gson.fromJson(responseBody, ArrayOfMunicipio::class.java)
                    return@withContext data
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null  // En caso de error o fallo en la solicitud
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