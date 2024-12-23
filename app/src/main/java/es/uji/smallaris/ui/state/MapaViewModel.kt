package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import es.uji.smallaris.model.CalculadorRutasORS
import es.uji.smallaris.model.ErrorCategory
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.model.RepositorioFirebase
import es.uji.smallaris.model.Ruta
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.ServicioRutas
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.UbicationException
import es.uji.smallaris.model.Vehiculo

//@HiltViewModel
class MapaViewModel() : ViewModel() {
    constructor(cosaMapa : Int) : this() {
        this.cosaMapa = cosaMapa
    }
    var cosaMapa by mutableStateOf(1)

    private val servicioAPI: ServicioAPIs = ServicioAPIs

    suspend fun getToponimo(longitud: Double, latitud: Double):Pair<ErrorCategory,String>{
        try {
            return Pair(ErrorCategory.NotAnError,servicioAPI.getToponimoCercano(longitud, latitud))
        } catch (e: UbicationException) {
            e.printStackTrace()
            return Pair(ErrorCategory.FormatError,"Error: " + e.message)
        }catch (e: Exception){
            e.printStackTrace()
            return Pair(ErrorCategory.NotAnError,"Error inesperado")
        }
    }

    suspend fun getRuta(p1: Point, p2: Point):  Ruta {
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
//        val origen =
//            LugarInteres(
//                -0.067893,
//                39.991907,
//                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
//                "Castellón de la Plana"
//            )
//        val destino = LugarInteres(
//            0.013474,
//            39.971408,
//            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
//            "Castellón de la Plana"
//        )
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

    companion object{
        val Saver: Saver<MapaViewModel, *> = listSaver(
            save = { listOf(it.cosaMapa)},
            restore = {
                MapaViewModel(
                    cosaMapa = it[0]
                )
            }
        )
    }
}