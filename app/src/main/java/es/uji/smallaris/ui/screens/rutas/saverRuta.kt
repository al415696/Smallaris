package es.uji.smallaris.ui.screens.rutas

import androidx.compose.runtime.saveable.Saver
import com.mapbox.geojson.LineString
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.model.Ruta
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.Vehiculo

// Define cómo guardar y restaurar Ruta
val rutaSaver = Saver<Ruta, Map<String, Any>>(
    save = { ruta ->
        mapOf(
            "nombre" to ruta.getNombre(),
            "inicio" to ruta.getInicio().toString(),
            "fin" to ruta.getFin().toString(),
            "vehiculo" to ruta.getVehiculo().toString(), // Puedes usar una representación simple del vehiculo
            "tipo" to ruta.getTipo().toString(), // Similar para el tipo de ruta
            "trayecto" to ruta.getTrayecto().toJson(), // Serializamos el LineString a JSON
            "distancia" to ruta.getDistancia(),
            "duracion" to ruta.getDuracion(),
            "coste" to ruta.getCoste()
        )
    },
    restore = { savedState ->
        Ruta(
            inicio = LugarInteres.fromString(savedState["inicio"] as String),
            fin = LugarInteres.fromString(savedState["fin"] as String),
            vehiculo = Vehiculo.fromString(savedState["vehiculo"] as String), // Debes definir este método en Vehiculo
            tipo = TipoRuta.valueOf(savedState["tipo"] as String), // Asumiendo que TipoRuta es un enum
            trayecto = LineString.fromJson(savedState["trayecto"] as String), // Convertimos de JSON a LineString
            distancia = savedState["distancia"] as Float,
            duracion = savedState["duracion"] as Float,
            coste = savedState["coste"] as Double,
            nombre = savedState["nombre"] as String
        )
    }
)