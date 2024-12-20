package es.uji.smallaris.ui.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.OrdenLugarInteres
import es.uji.smallaris.model.ServicioLugares
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.model.UbicationException
import es.uji.smallaris.ui.screens.lugares.lugarInteresTestData
import kotlinx.coroutines.launch

//@HiltViewModel
class LugaresViewModel() : ViewModel() {
    constructor( cosaLugares : Int) : this() {
        this.cosaLugares = cosaLugares
    }
    var cosaLugares by mutableStateOf(1)


    val servicioLugares: ServicioLugares = ServicioLugares.getInstance()

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
        viewModelScope.launch {
        val lugarInteresTestData = listOf(
            LugarInteres(15.8567, 22.5188, "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana"),
            LugarInteres(40.4168, -3.7038, "Puerta del Sol, Madrid, España", "Madrid"),
            LugarInteres(43.2630, -2.9350, "Museo Guggenheim, Bilbao, País Vasco, España", "Bilbao"),
//            LugarInteres(39.8628, -4.0273, "El Alcázar, Toledo, Castilla-La Mancha, España", "Toledo"),
//            LugarInteres(38.3452, -0.4811, "Castillo de Santa Bárbara, Alicante, Comunidad Valenciana, España", "Alicante"),
//            LugarInteres(40.4168, -3.7038, "Parque de las Aves", "Madrid"),
//            LugarInteres(41.3825, 2.1769, "Cascada de los Elfos", "Barcelona"),
//            LugarInteres(37.3891, -5.9845, "Bosque Encantado", "Sevilla"),
//            LugarInteres(36.7213, -4.4214, "Casa del Tiempo", "Málaga"),
//            LugarInteres(39.8628, -4.0273, "Monte de los Suspiros", "Toledo"),
//            LugarInteres(42.6986, -1.6323, "Torre de la Eternidad", "Pamplona"),
//            LugarInteres(41.6561, -0.8773, "Templo de los Milagros", "Zaragoza"),
//            LugarInteres(37.9834, -1.1280, "Camino de los Ancestros", "Murcia"),
//            LugarInteres(28.4682, -16.2546, "Isla de las Almas", "Santa Cruz de Tenerife")
        )

            for (lugar in lugarInteresTestData) {
                servicioLugares.addLugar(lugar.longitud, lugar.latitud, lugar.nombre)
            }
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