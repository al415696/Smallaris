package es.uji.smallaris.ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.OrdenRuta
import es.uji.smallaris.model.RouteException
import es.uji.smallaris.model.Ruta
import es.uji.smallaris.model.ServicioRutas
import es.uji.smallaris.model.ServicioUsuarios
import es.uji.smallaris.model.ServicioVehiculos
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.lugares.ServicioLugares
import es.uji.smallaris.model.lugares.UbicationException

class RutasViewModel : ViewModel() {

    private val servicioLugares: ServicioLugares = ServicioLugares.getInstance()
    private val servicioVehiculos: ServicioVehiculos = ServicioVehiculos.getInstance()
    private val servicioUsuarios: ServicioUsuarios = ServicioUsuarios.getInstance()

    suspend fun getVehiculos(): List<Vehiculo> {
        return servicioVehiculos.getVehiculos()
    }

    suspend fun getLugares(): List<LugarInteres> {
        return servicioLugares.getLugares()
    }


    private val servicioRutas: ServicioRutas = ServicioRutas.getInstance()
    var listState: LazyListState = LazyListState()

    // Lista observable
    var listRutas: SnapshotStateList<Ruta> = mutableStateListOf<Ruta>()
    private var currentSorting: OrdenRuta = OrdenRuta.FAVORITO_THEN_NOMBRE

    fun sortItems(ordenLugar: OrdenRuta = OrdenRuta.FAVORITO_THEN_NOMBRE) {
        currentSorting = ordenLugar
        sortItems()
    }

    private fun sortItems() {
        listRutas.sortWith(currentSorting.comparator())
    }

    suspend fun addRuta(
        nombreRuta: String,
        inicio: LugarInteres,
        fin: LugarInteres,
        vehiculo: Vehiculo,
        tipoRuta: TipoRuta
    ): String {
        try {
            val rutaAAnyadir = servicioRutas.builder().setNombre(nombreRuta).setInicio(inicio)
                .setFin(fin).setVehiculo(vehiculo)
                .setTipo(tipoRuta).build()
            servicioRutas.addRuta(rutaAAnyadir)
            updateList()
        } catch (e: ConnectionErrorException) {
            e.printStackTrace()
            return "Error al conectarse con el servidor"
        } catch (e: UbicationException) {

            return e.message ?: "Fallo con los lugares de intérs, no se ha añadido"
        } catch (e: RouteException) {

            return e.message ?: "Fallo con la ruta, no se ha añadido"
        }
        return ""
    }

    suspend fun calcRuta(
        nombreRuta: String,
        inicio: LugarInteres,
        fin: LugarInteres,
        vehiculo: Vehiculo,
        tipoRuta: TipoRuta
    ): Pair<String, Ruta> {
        val builder = servicioRutas.builder().setNombre(nombreRuta).setInicio(inicio)
            .setFin(fin).setVehiculo(vehiculo)
            .setTipo(tipoRuta)
        try {

            return Pair("", builder.build())
        } catch (e: ConnectionErrorException) {
            e.printStackTrace()
            return Pair(e.message ?: "Error de conexión", builder.getRuta())
        } catch (e: UbicationException) {
            e.printStackTrace()
            return Pair(e.message ?: "Error de ubicación de lugares", builder.getRuta())
        } catch (e: RouteException) {
            e.printStackTrace()
            return Pair(e.message ?: "Error de construcción de ruta", builder.getRuta())
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair("Error inesperado", builder.getRuta())
        }
    }

    suspend fun setRutaFavorita(ruta: Ruta, favorito: Boolean) {
        try {
            if (servicioRutas.setFavorito(ruta, favorito))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteRuta(ruta: Ruta): String {
        try {
            if (servicioRutas.deleteRuta(ruta))
                updateList()
            return ""
        } catch (e: ConnectionErrorException) {
            return "Error al conectarse con el servidor"
        } catch (e: RouteException) {
            return e.message ?: "Error con la ruta"
        } catch (e: Exception) {
            return e.message ?: "Fallo inesperado, prueba con otro momento"
        }
    }

    suspend fun getDefaultVehiculo(): Vehiculo? {
        return try {
            servicioUsuarios.obtenerVehiculoPorDefecto()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getDefaultTipoRuta(): TipoRuta? {
        return try {
            servicioUsuarios.obtenerTipoRutaPorDefecto()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun initializeList() {
        try {
            servicioRutas.updateRutas()
            updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun updateList() {
        try {// Step 1: Add missing elements
            val nueva = servicioRutas.getRutas()
            nueva.forEach { element ->
                if (!listRutas.contains(element)) {
                    listRutas.add(element)
                }
            }

            // Step 2: Remove extra elements
            val iterator = listRutas.iterator()
            while (iterator.hasNext()) {
                val element = iterator.next()
                if (!nueva.contains(element)) {
                    iterator.remove()
                }
            }

            // Step 3: Rearrange elements
            sortItems()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {
        val Saver: Saver<RutasViewModel, *> = listSaver(
            save = { listOf<Any>() },
            restore = {
                RutasViewModel(

                )
            }
        )
    }
}