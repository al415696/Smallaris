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
import es.uji.smallaris.model.RouteException
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.VehicleException
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.lugares.ServicioLugares
import es.uji.smallaris.model.lugares.UbicationException

class LugaresViewModel : ViewModel() {

    private val servicioLugares: ServicioLugares = ServicioLugares.getInstance()

    private val servicioAPI: ServicioAPIs = ServicioAPIs

    var listState: LazyListState = LazyListState()

    // Lista observable
    var items: SnapshotStateList<LugarInteres> = mutableStateListOf<LugarInteres>()
    private var currentSorting: OrdenLugarInteres = OrdenLugarInteres.FAVORITO_THEN_NOMBRE

    fun sortItems(ordenLugar: OrdenLugarInteres = OrdenLugarInteres.FAVORITO_THEN_NOMBRE) {
        currentSorting = ordenLugar
        sortItems()
    }

    private fun sortItems() {
        items.sortWith(currentSorting.comparator())
    }

    suspend fun addLugar(longitud: Double, latitud: Double, nombre: String = ""): String {
        try {
            servicioLugares.addLugar(longitud, latitud, nombre)
            updateList()
        } catch (e: ConnectionErrorException) {
            e.printStackTrace()
            return "Error al conectarse con el servidor"
        } catch (e: UbicationException) {

            return e.message ?: "Fallo con el lugar, no se ha añadido"
        }
        return ""
    }

    suspend fun setLugarFavorito(lugaresInteres: LugarInteres, favorito: Boolean) {
        try {
            if (servicioLugares.setLugarInteresFavorito(lugaresInteres, favorito))
                updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteLugar(lugaresInteres: LugarInteres): String {
        try {
            if (servicioLugares.deleteLugar(lugaresInteres)) {
                updateList()
            }
            return ""
        } catch (e: ConnectionErrorException) {
            return "Error al conectarse con el servidor"
        } catch (e: VehicleException) {
            return e.message ?: "Error con el lugar"
        } catch (e: RouteException) {
            return e.message ?: "El lugar está en alguna ruta, no se puede borrar"
        } catch (e: Exception) {
            return e.message ?: "Fallo inesperado, prueba con otro momento"
        }
    }

    suspend fun initializeList() {
        try {
            servicioLugares.updateLugares()
            updateList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun updateList() {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

    suspend fun getCoordenadas(toponimo: String): Pair<ErrorCategory, Pair<Double, Double>> {
        try {
            //(longitud, latitud)
            return Pair(ErrorCategory.NotAnError, servicioAPI.getCoordenadas(toponimo))
        } catch (e: UbicationException) {
            e.printStackTrace()
            return Pair(ErrorCategory.UnknownError, Pair(-999.9, -999.9))
        }
    }

    companion object {
        val Saver: Saver<LugaresViewModel, *> =
            listSaver(
                save = {
                    listOf<Any>(
                        it.listState.firstVisibleItemIndex,
                        it.listState.firstVisibleItemScrollOffset
                    )
                },
                restore = {
                    LugaresViewModel()
                }
            )
    }
}