package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import es.uji.smallaris.model.CalculadorRutasORS
import com.mapbox.maps.CameraState
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import es.uji.smallaris.model.ErrorCategory
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.RepositorioFirebase
import es.uji.smallaris.model.Ruta
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.ServicioRutas
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.lugares.UbicationException
import es.uji.smallaris.model.Vehiculo

//@HiltViewModel
class MapaViewModel() : ViewModel() {
    constructor(cosaMapa: Int) : this() {
        this.cosaMapa = cosaMapa
    }

    var cosaMapa by mutableStateOf(1)
    private val servicioRutas: ServicioRutas = ServicioRutas.getInstance()

    val listaRutas: SnapshotStateList<Ruta> = mutableStateListOf()

    suspend fun debugFillList() {
        val builder = servicioRutas.builder()

        // Esta ruta es dummy, no tiene ni trayecto ni duración ni distancia
        // La diferencia esta en la última llamada a getRuta()
        val ruta1 = builder.setNombre("Ruta 1").setInicio(
            LugarInteres(
                -0.03778,
                39.98574,
                "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        ).setFin(
            LugarInteres(-2.934, 43.268, "Museo Guggenheim, Bilbao, País Vasco, España", "Bilbao")
        ).setVehiculo(Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95))
            .setTipo(TipoRuta.Economica).getRuta()

        // Esta ruta es completa, tiene trayecto, duración y distancia
        // La diferencia esta en la última llamada a build()
        val ruta2 = builder.setNombre("Ruta 2").setInicio(
            LugarInteres(
                -0.03778,
                39.98574,
                "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        ).setFin(
            LugarInteres(-2.934, 43.268, "Museo Guggenheim, Bilbao, País Vasco, España", "Bilbao")
        ).setVehiculo(Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95))
            .setTipo(TipoRuta.Corta).build()

        // Lo comento por qué cada vez que entras al mapa genércio recrea la ruta y lanza excepción
        //servicioRutas.addRuta(ruta1)

        listaRutas.add(ruta1)
        listaRutas.add(ruta2)
    }


    var addMapState: MapViewportState = MapViewportState(
        CameraState(
            Point.fromLngLat(
                -0.068547,
                39.994259
            ),
            EdgeInsets(0.0, 0.0, 0.0, 0.0),
            15.0, // Ajusta el nivel de zoom según lo que desees mostrar.
            0.0,
            0.0
        )
    )

    private val servicioAPI: ServicioAPIs = ServicioAPIs

    suspend fun getToponimo(longitud: Double, latitud: Double): Pair<ErrorCategory, String> {
        try {
            return Pair(ErrorCategory.NotAnError, servicioAPI.getToponimoCercano(longitud, latitud))
        } catch (e: UbicationException) {
            e.printStackTrace()
            return Pair(ErrorCategory.FormatError, "Error: " + e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(ErrorCategory.NotAnError, "Error inesperado")
        }
    }

    suspend fun getRuta(p1: Point, p2: Point): Ruta {
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen = LugarInteres(p1.longitude(), p1.latitude(), "Origen", "Origen")
        val destino = LugarInteres(p2.longitude(), p2.latitude(), "Destino", "Destino")
        val servicioRutas =
            ServicioRutas(CalculadorRutasORS(servicioAPI), RepositorioFirebase(), servicioAPI)

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Economica).build()

        return ruta

    }

    companion object {
        val Saver: Saver<MapaViewModel, *> = listSaver(
            save = { listOf(it.cosaMapa) },
            restore = {
                MapaViewModel(
                    cosaMapa = it[0]
                )
            }
        )
    }
}