package es.uji.smallaris.model

import com.google.gson.JsonParser
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import kotlin.jvm.Throws

class CalculadorRutasORS(
    private val servicioORS: ServicioAPIs
) : CalculadorRutas() {

    @Throws(RouteException::class)
    override fun calcularTrayecto(inicio: LugarInteres, fin: LugarInteres, tipoRuta: TipoRuta, tipoVehiculo: TipoVehiculo): Triple<LineString, Float, Float> {
        // Obtener el GeoJSON como String
        val geoJsonResponse = servicioORS.getRuta(inicio, fin, tipoRuta, tipoVehiculo)

        // Usar JsonParser para convertir el GeoJSON en un JsonElement
        val jsonElement = JsonParser.parseString(geoJsonResponse).asJsonObject

        // Extraer las coordenadas de la respuesta (GeoJSON)
        val coordinates = jsonElement
            .getAsJsonArray("features")
            .get(0)
            .asJsonObject
            .getAsJsonObject("geometry")
            .getAsJsonArray("coordinates")

        // Convertir las coordenadas a objetos Point de Mapbox
        val points = coordinates.map {
            val longitude = it.asJsonArray.get(0).asDouble // Obtener longitud
            val latitude = it.asJsonArray.get(1).asDouble // Obtener latitud
            Point.fromLngLat(longitude, latitude)
        }

        val lineString = LineString.fromLngLats(points)

        // Extraer la distancia y la duración (asumido que están en 'properties.segments')
        val distance = jsonElement
            .getAsJsonArray("features")
            .get(0)
            .asJsonObject
            .getAsJsonObject("properties")
            .getAsJsonArray("segments")
            .get(0)
            .asJsonObject
            .get("distance").asFloat

        val duration = jsonElement
            .getAsJsonArray("features")
            .get(0)
            .asJsonObject
            .getAsJsonObject("properties")
            .getAsJsonArray("segments")
            .get(0)
            .asJsonObject
            .get("duration").asFloat

        // Retornar el trayecto (LineString), la distancia(en KM) y la duración(en minutos)
        return Triple(lineString, distance / 1000, duration / 60)
    }
}