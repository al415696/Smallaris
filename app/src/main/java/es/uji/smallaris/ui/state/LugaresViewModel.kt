package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.ErrorCategory
import es.uji.smallaris.model.OrdenLugarInteres
import es.uji.smallaris.model.ServicioLugares
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.UbicationException

//@HiltViewModel
class LugaresViewModel() : ViewModel() {
    constructor( cosaLugares : Int) : this() {
        this.cosaLugares = cosaLugares
    }
    var cosaLugares by mutableStateOf(1)


    private val servicioLugares: ServicioLugares = ServicioLugares.getInstance()

    private val servicioAPI: ServicioAPIs = ServicioAPIs

    // Lista observable
//    var items: MutableState<List<Lugar>> = mutableStateOf(emptyList())
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

            return e.message ?: ""
        }
        return ""
    }
    suspend fun setLugarFavorito(lugaresInteres: LugarInteres, favorito: Boolean){
        try {
            if(servicioLugares.setFavorito(lugaresInteres, favorito))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun deleteLugar(lugaresInteres: LugarInteres){
        try {
            if(servicioLugares.deleteLugar(lugaresInteres))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
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
            LugarInteres(-3.7176849639172684, 40.965189327470604,  "Calle random de Gargantilla del Lozoya y Pinilla de Buitrago", "Gargantilla del Lozoya y Pinilla de Buitrago")
        )
//        servicioLugares.addLugar(-0.03778, 39.98574, "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España")
            for (lugar in lugarInteresTestData) {
                servicioLugares.addLugar(lugar.longitud, lugar.latitud, lugar.nombre)
            }
    }
    suspend fun updateList(){
//        items.value = servicioLugares.getLugares()
        // Step 1: Add missing elements to the items list
        val nueva = servicioLugares.getLugares()
        nueva.forEach { element ->
            if (!items.contains(element)) {
                items.add(element)
            }
        }

        // Step 2: Remove extra elements from the items list
        val iterator = items.iterator()
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
    suspend fun getCoordenadas(toponimo: String): Pair<Double,Double>{
        try {
            return servicioAPI.getCoordenadas(toponimo)
        } catch (e: UbicationException) {
            e.printStackTrace()
            return Pair(-999.9,-999.9)
        }
    }
    companion object{
        val Saver: Saver<LugaresViewModel, *> = listSaver(
            save = { listOf(it.cosaLugares)},
            restore = {
                LugaresViewModel(
                    cosaLugares = it[0]
                )
            }
        )
    }
}