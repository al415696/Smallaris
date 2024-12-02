package es.uji.smallaris.model

import com.google.gson.JsonParser
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import es.uji.smallaris.model.ServicioAPIs.getRuta

class CalculadorRutasORS: CalculadorRutas() {
    override fun calcularTrayecto(builder: RutaBuilder): Triple<LineString, Float, Float> {
        // Obtener el GeoJSON como String
        val geoJsonResponse = getRuta(builder.getInicio(), builder.getFin(), builder.getTipo())

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

        // Retornar el trayecto (LineString), la distancia y la duración
        return Triple(lineString, distance, duration)
    }

    override fun calcularCoste(): Float {
        return 0.0f
    }
}