package es.uji.smallaris.ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.ErrorCategory
import es.uji.smallaris.model.OrdenLugarInteres
import es.uji.smallaris.model.lugares.ServicioLugares
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.VehicleException
import es.uji.smallaris.model.lugares.UbicationException

//@HiltViewModel
class LugaresViewModel() : ViewModel() {
//    constructor(listState: Pair<Int,Int>) : this() {
//        listStateValues = listState
//    }

    private val servicioLugares: ServicioLugares = ServicioLugares.getInstance()

    private val servicioAPI: ServicioAPIs = ServicioAPIs

    var listState: LazyListState = LazyListState()
    // Lista observable
    var items: SnapshotStateList<LugarInteres> = mutableStateListOf<LugarInteres>()
    private var currentSorting: OrdenLugarInteres = OrdenLugarInteres.FAVORITO_THEN_NOMBRE

    fun sortItems(ordenLugar: OrdenLugarInteres = OrdenLugarInteres.FAVORITO_THEN_NOMBRE){
        currentSorting = ordenLugar
        sortItems()
    }
    private fun sortItems(){
        items.sortWith(currentSorting.comparator())
    }

    suspend fun addLugar(longitud: Double, latitud: Double, nombre: String = ""): String{
        try {
            servicioLugares.addLugar(longitud, latitud, nombre)
            updateList()
        }
        catch (e: ConnectionErrorException) {
            e.printStackTrace()
            return "Error al conectarse con el servidor"
        }
        catch (e: UbicationException){

            return e.message ?: "Fallo con el lugar, no se ha añadido"
        }
        return ""
    }
    suspend fun setLugarFavorito(lugaresInteres: LugarInteres, favorito: Boolean){
        try {
            if(servicioLugares.setLugarInteresFavorito(lugaresInteres, favorito))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun deleteLugar(lugaresInteres: LugarInteres): String{
        try {
            if(servicioLugares.deleteLugar(lugaresInteres)) {
                updateList()
            }
            return ""
        } catch (e: ConnectionErrorException) {
            return "Error al conectarse con el servidor"
        }
        catch (e: VehicleException) {
            return e.message?: "Error con el vehiculo"
        }
        catch (e: Exception) {
            return e.message?:"Fallo inesperado, prueba con otro momento"
        }
    }
    suspend fun debugFillList(){

            val lugarInteresTestData = listOf(
            LugarInteres(-0.03778, 39.98574, "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana"),
            LugarInteres(-2.934,43.268,  "Museo Guggenheim, Bilbao, País Vasco, España", "Bilbao"),
            LugarInteres(-4.02057, 39.85788, "El Alcázar, Toledo, Castilla-La Mancha, España", "Toledo"),
            LugarInteres(-0.47807, 38.34910, "Castillo de Santa Bárbara, Alicante, Comunidad Valenciana, España", "Alicante"),
            LugarInteres(-3.7038, 40.4168, "Parque de las Aves", "Madrid"),
            LugarInteres(2.1769, 41.3825, "Cascada de los Elfos", "Barcelona"),
            LugarInteres(-5.9845,37.3891,  "Bosque Encantado", "Sevilla"),
            LugarInteres(-4.4214,36.7213,  "Casa del Tiempo", "Málaga"),
            LugarInteres(-1.6323,42.6986,  "Torre de la Eternidad", "Pamplona"),
            LugarInteres(-0.8773,41.6561,  "Templo de los Milagros", "Zaragoza"),
            LugarInteres(-1.1280,37.9834,  "Camino de los Ancestros", "Murcia"),
            LugarInteres(-16.2546,28.4682,  "Isla de las Almas", "Santa Cruz de Tenerife"),
            LugarInteres(-3.7176849639172684, 40.965189327470604,  "Calle random de Gargantilla del Lozoya y Pinilla de Buitrago", "Gargantilla del Lozoya y Pinilla de Buitrago"),
//                LugarInteres(-0.57807, 38.24910, "Cerca de Castillo de Santa Bárbara, Alicante, Comunidad Valenciana, España", "Alicante"),
//                LugarInteres(-3.8038, 40.3168, "Cerca de Parque de las Aves", "Madrid"),
//                LugarInteres(2.2769, 41.2825, "Cerca de Cascada de los Elfos", "Barcelona"),
//                LugarInteres(-5.0845,37.2891,  "Cerca de Bosque Encantado", "Sevilla"),
//                LugarInteres(-4.5214,36.6213,  "Cerca de Casa del Tiempo", "Málaga"),
//                LugarInteres(-1.7323,42.5986,  "Cerca de Torre de la Eternidad", "Pamplona"),
//                LugarInteres(-0.9773,41.5561,  "Cerca de Templo de los Milagros", "Zaragoza"),
//                LugarInteres(-1.2280,37.8834,  "Cerca de Camino de los Ancestros", "Murcia"),
//                LugarInteres(-16.3546,28.3682,  "Cerca de Isla de las Almas", "Santa Cruz de Tenerife"),
        )
//        servicioLugares.addLugar(-0.03778, 39.98574, "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España")
            for (lugar in lugarInteresTestData) {
                addLugar(lugar.longitud, lugar.latitud, lugar.nombre)
            }
    }
    suspend fun initializeList(){
        servicioLugares.updateLugares()
        updateList()
    }
    suspend fun updateList(){
//        items.value = servicioLugares.getLugares()
        // Step 1: Add missing elements
        val nueva = servicioLugares.getLugares()
        nueva.forEach { element ->
            if (!items.contains(element)) {
                items.add(element)
            }
        }

        // Step 2: Remove extra elements
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (!nueva.contains(element)) {
                iterator.remove()
            }
        }

        // Step 3: Rearrange elements
        sortItems()
    }

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
    suspend fun getCoordenadas(toponimo: String):Pair<ErrorCategory,Pair<Double,Double>>{
        try {
            //(longitud, latitud)

            return Pair(ErrorCategory.NotAnError, servicioAPI.getCoordenadas(toponimo))
        } catch (e: UbicationException) {
            e.printStackTrace()
            return Pair(ErrorCategory.UnknownError ,Pair(-999.9,-999.9))
        }
    }
    companion object{
        val Saver: Saver<LugaresViewModel, *> =
//            mapSaver(
//            save = { hashMapOf(Pair("state",it.listState))},
//            restore = {LugaresViewModel(
//                it["state"] as LazyListState
//            )}
//        )
            listSaver(
            save = { listOf<Any>(it.listState.firstVisibleItemIndex,it.listState.firstVisibleItemScrollOffset)},
            restore = {
                LugaresViewModel()
            }
        )
    }
}