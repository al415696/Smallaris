package es.uji.smallaris.ui.screens.lugares

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import es.uji.smallaris.model.OrdenLugarInteres
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.ui.state.LugaresViewModel

@Composable
fun LugaresScreen(
    viewModel: LugaresViewModel
) {
    val modifier: Modifier = Modifier
    val items = viewModel.items
    val currentContent = rememberSaveable { mutableStateOf(LugarScreenContent.Lista) }
    var currentViewedLugar: LugarInteres =
        rememberSaveable(saver = saverLugarInteres) {
            if (items.isEmpty()) LugarInteres(-666.0, -777777.7, "Nada", "Abismo")
            else items[0]

        }
    var currentOrderIndex = 0


    Surface(color = MaterialTheme.colorScheme.primary) {
        when (currentContent.value) {
            LugarScreenContent.Lista ->
                LugaresListContent(
                    modifier = modifier,
                    items = items,
                    state = viewModel.listState,
                    favoriteFuncion = { lugarInteres: LugarInteres, favorito: Boolean ->
                        viewModel.setLugarFavorito(
                            lugarInteres,
                            favorito
                        )
                    },
                    addFunction = { currentContent.value = LugarScreenContent.Add },
                    sortFunction =
                    {
                        currentOrderIndex = (currentOrderIndex + 1) % OrdenLugarInteres.entries.size
                        viewModel.sortItems(OrdenLugarInteres.entries[currentOrderIndex])
                        OrdenLugarInteres.entries[currentOrderIndex].getNombre()
                    },
                    viewFunction = { lugar: LugarInteres ->
                        currentViewedLugar = lugar
                        currentContent.value = LugarScreenContent.Map
                    },
                    deleteFuncition = viewModel::deleteLugar

                )

            LugarScreenContent.Add ->
                LugaresAddContent(
                    funAddLugar = { longitud: Double, latitud: Double, nombre: String ->
                        viewModel.addLugar(
                            longitud,
                            latitud,
                            nombre
                        )
                    },
                    onBack = { currentContent.value = LugarScreenContent.Lista },
                    funConseguirToponimos = viewModel::getToponimo,
                    funConseguirCoordenadas = viewModel::getCoordenadas
                )

            LugarScreenContent.Map ->
                LugaresMapContent(
                    onBack = { currentContent.value = LugarScreenContent.Lista },
                    marker = Point.fromLngLat(
                        currentViewedLugar.longitud,
                        currentViewedLugar.latitud
                    )
                )
        }
    }
}

private enum class LugarScreenContent {
    Lista,
    Add,
    Map
}
