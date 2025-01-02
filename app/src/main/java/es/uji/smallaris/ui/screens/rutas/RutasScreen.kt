package es.uji.smallaris.ui.screens.rutas

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.OrdenRuta
import es.uji.smallaris.model.Ruta
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.state.RutasViewModel
import java.util.Locale


@Composable
fun RutasScreen(
    viewModel: RutasViewModel,
    onGoToRuta: (Double, Double) -> Unit = {_,_ ->}
){
    val modifier: Modifier = Modifier
    val items = viewModel.listRutas
    val currentContent = rememberSaveable { mutableStateOf(RutaScreenContent.Lista) }
    var currentViewedRuta: Ruta = rememberSaveable(saver = rutaSaver) { rutaDebug }
    var currentOrderIndex: Int = 0


    Surface(color = MaterialTheme.colorScheme.primary) {
        when(currentContent.value){
            RutaScreenContent.Lista ->
                RutasListContent(
                    modifier = modifier,
                    items = items,
                    state = viewModel.listState,
                    favoriteFuncion = { ruta: Ruta, favorito: Boolean ->  viewModel.setRutaFavorita(ruta, favorito) },
                    addFunction = {currentContent.value = RutaScreenContent.Add},
                    sortFunction =
                    {
                        currentOrderIndex = (currentOrderIndex+1) % OrdenRuta.entries.size
                        viewModel.sortItems(OrdenRuta.entries[currentOrderIndex])
                        OrdenRuta.entries[currentOrderIndex].getNombre()
                    }
                    ,
                    viewFunction = {ruta: Ruta ->
                        currentViewedRuta = ruta
                        currentContent.value = RutaScreenContent.Map
                     },
                    deleteFuncition =viewModel::deleteRuta
                )
            RutaScreenContent.Add ->
                RutasAddContent(
                    funAddRuta = {
                        nombreRuta: String, inicio: LugarInteres, fin: LugarInteres, vehiculo: Vehiculo, tipoRuta: TipoRuta ->
                        viewModel.addRuta(nombreRuta, inicio, fin, vehiculo, tipoRuta)},
                    onBack = {currentContent.value = RutaScreenContent.Lista },
                    funConseguirVehiculos = viewModel::getVehiculos,
                    funConseguirLugares = viewModel::getLugares,
                    funCalcRuta = viewModel::calcRuta,
                    funGetVehiculoPorDefecto = viewModel::getDefaultVehiculo,
                    funGetTipoRutaPorDefecto = viewModel::getDefaultTipoRuta,

                )

            RutaScreenContent.Map ->
                RutasMapContent(
                    onBack = {currentContent.value = RutaScreenContent.Lista},
                    ruta = currentViewedRuta,
                )
        }
    }
}

private enum class RutaScreenContent(){
    Lista,
    Add,
    Map
}



