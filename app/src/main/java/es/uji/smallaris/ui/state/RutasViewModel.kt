package es.uji.smallaris.ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.model.OrdenLugarInteres
import es.uji.smallaris.model.OrdenRuta
import es.uji.smallaris.model.Ruta
import es.uji.smallaris.model.RutaBuilder
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.ServicioLugares
import es.uji.smallaris.model.ServicioRutas
import es.uji.smallaris.model.ServicioVehiculos
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.UbicationException
import es.uji.smallaris.model.Vehiculo

//@HiltViewModel
class RutasViewModel() : ViewModel() {

    private val servicioLugares: ServicioLugares = ServicioLugares.getInstance()
    private val servicioVehiculos: ServicioVehiculos = ServicioVehiculos.getInstance()

    suspend fun getVehiculos(): List<Vehiculo> {
        return servicioVehiculos.getVehiculos()
    }

    suspend fun getLugares(): List<LugarInteres> {
        return servicioLugares.getLugares()
    }


    private val servicioRutas: ServicioRutas = ServicioRutas.getInstance()

    private val servicioAPI: ServicioAPIs = ServicioAPIs

    var listState: LazyListState = LazyListState()
    // Lista observable
    var listRutas: SnapshotStateList<Ruta> = mutableStateListOf<Ruta>()
    private var currentSorting: OrdenRuta = OrdenRuta.FAVORITO_THEN_NOMBRE

    fun sortItems(ordenLugar: OrdenRuta = OrdenRuta.FAVORITO_THEN_NOMBRE){
        currentSorting = ordenLugar
        sortItems()
    }
    private fun sortItems(){
        listRutas.sortWith(currentSorting.comparator())
    }
//    var dummyRuta: Ruta = RutaBuilder()
//        .setNombre("Ruta por Aionios")
//        .setInicio(LugarInteres(-999.03778, 999.98574, "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana"))
//        .setFin(LugarInteres(999.934,-999.268,  "Museo Guggenheim, Bilbao, País Vasco, España", "Bilbao"),)
//        .setVehiculo(Vehiculo("Vacío", 2.2, "9999ÑÑÑ",TipoVehiculo.Desconocido))
//        .setTipo(TipoRuta.Corta).getRutaCalculada()
//    fun getDummyRuta(): Ruta{
//        return
//    }

    suspend fun addRuta(
        nombreRuta: String,
        inicio:LugarInteres,
        fin: LugarInteres,
        vehiculo: Vehiculo,
        tipoRuta: TipoRuta
    ): String{
        try {
            val rutaAAnyadir = servicioRutas.builder().setNombre(nombreRuta).setInicio(inicio)
                .setFin(fin).setVehiculo(vehiculo)
                .setTipo(tipoRuta).build()
            servicioRutas.addRuta(rutaAAnyadir)
            updateList()
        }
        catch (e: ConnectionErrorException) {
            e.printStackTrace()
            return "Error al conectarse con el servidor"
        }
        catch (e: UbicationException){

            return e.message ?: ""
        }
        return ""
    }
    suspend fun setRutaFavorita(ruta: Ruta, favorito: Boolean){
        try {
            if(servicioRutas.setFavorito(ruta, favorito))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun deleteRuta(ruta: Ruta){
        try {
            if(servicioRutas.deleteRuta(ruta))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
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

        servicioRutas.addRuta(ruta1)
        servicioRutas.addRuta(ruta2)
    }




    suspend fun updateList(){
//        items.value = servicioLugares.getLugares()
        // Step 1: Add missing elements to the items list
        val nueva = servicioRutas.getRutas()
        nueva.forEach { element ->
            if (!listRutas.contains(element)) {
                listRutas.add(element)
            }
        }

        // Step 2: Remove extra elements from the items list
        val iterator = listRutas.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!nueva.contains(element)) {
                iterator.remove()
            }
        }

        // Step 3: Rearrange elements in the items list to match the nueva list
        sortItems()
//        val orderMap = nueva.withIndex().associate { it.value to it.index }
//        items.sortBy { orderMap[it] }
    }




    companion object{
        val Saver: Saver<RutasViewModel, *> = listSaver(
            save = { listOf<Any>()},
            restore = {
                RutasViewModel(

                )
            }
        )
    }
}