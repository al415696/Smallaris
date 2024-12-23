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
    suspend fun debugFillList(){

//        val lugarInteresTestData = listOf(
//            LugarInteres(-0.03778, 39.98574, "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana"),
//            LugarInteres(-2.934,43.268,  "Museo Guggenheim, Bilbao, País Vasco, España", "Bilbao"),
//            LugarInteres(-4.02057, 39.85788, "El Alcázar, Toledo, Castilla-La Mancha, España", "Toledo"),
//            LugarInteres(-0.47807, 38.34910, "Castillo de Santa Bárbara, Alicante, Comunidad Valenciana, España", "Alicante"),
//            LugarInteres(-3.7038, 40.4168, "Parque de las Aves", "Madrid"),
//            LugarInteres(2.1769, 41.3825, "Cascada de los Elfos", "Barcelona"),
//            LugarInteres(-5.9845,37.3891,  "Bosque Encantado", "Sevilla"),
//            LugarInteres(-4.4214,36.7213,  "Casa del Tiempo", "Málaga"),
//            LugarInteres(-1.6323,42.6986,  "Torre de la Eternidad", "Pamplona"),
//            LugarInteres(-0.8773,41.6561,  "Templo de los Milagros", "Zaragoza"),
//            LugarInteres(-1.1280,37.9834,  "Camino de los Ancestros", "Murcia"),
//            LugarInteres(-16.2546,28.4682,  "Isla de las Almas", "Santa Cruz de Tenerife"),
//            LugarInteres(-3.7176849639172684, 40.965189327470604,  "Calle random de Gargantilla del Lozoya y Pinilla de Buitrago", "Gargantilla del Lozoya y Pinilla de Buitrago"),
//            LugarInteres(-0.57807, 38.24910, "Cerca de Castillo de Santa Bárbara, Alicante, Comunidad Valenciana, España", "Alicante"),
//            LugarInteres(-3.8038, 40.3168, "Cerca de Parque de las Aves", "Madrid"),
//            LugarInteres(2.2769, 41.2825, "Cerca de Cascada de los Elfos", "Barcelona"),
//            LugarInteres(-5.0845,37.2891,  "Cerca de Bosque Encantado", "Sevilla"),
//            LugarInteres(-4.5214,36.6213,  "Cerca de Casa del Tiempo", "Málaga"),
//            LugarInteres(-1.7323,42.5986,  "Cerca de Torre de la Eternidad", "Pamplona"),
//            LugarInteres(-0.9773,41.5561,  "Cerca de Templo de los Milagros", "Zaragoza"),
//            LugarInteres(-1.2280,37.8834,  "Cerca de Camino de los Ancestros", "Murcia"),
//            LugarInteres(-16.3546,28.3682,  "Cerca de Isla de las Almas", "Santa Cruz de Tenerife"),
//        )
//        for (lugar in lugarInteresTestData) {
//            addRuta(lugar.longitud, lugar.latitud, lugar.nombre)
//        }
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