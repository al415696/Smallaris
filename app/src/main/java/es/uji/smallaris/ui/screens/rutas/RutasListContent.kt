package es.uji.smallaris.ui.screens.rutas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NotListedLocation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R
import es.uji.smallaris.model.Ruta
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.ui.components.BottomListActionBar
import es.uji.smallaris.ui.components.DeleteAlertDialogue
import es.uji.smallaris.ui.components.ObjetoListable
import es.uji.smallaris.ui.screens.toCleanCost
import es.uji.smallaris.ui.screens.toCleanDistance
import es.uji.smallaris.ui.screens.toTimeFormat


@Composable
fun RutasListContent(
    modifier: Modifier,
    items: List<Ruta> = listOf(),
    addFunction: () -> Unit = {},
    viewFunction: (ruta: Ruta) -> Unit = {},
    sortFunction: () -> String = { "" },
    deleteFuncition: suspend (ruta: Ruta) -> String = { "" },
    favoriteFuncion: suspend (ruta: Ruta, favorito: Boolean) -> Unit = { _, _ -> },
    state: LazyListState = rememberLazyListState()

) {
    var rutaSelected: Ruta? by remember {
        mutableStateOf(null)
    }
    val firstItemVisible by remember {
        derivedStateOf {
            state.firstVisibleItemIndex == 0
        }
    }


    Column {

        Surface(color = MaterialTheme.colorScheme.primary) {

            Box(
                modifier = modifier.fillMaxHeight(),
            ) {

                if (items.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(30.dp))
                        Text(
                            style = MaterialTheme.typography.titleLarge,
                            text = stringResource(R.string.sin_rutas_text),
                            textAlign = TextAlign.Center,
                            lineHeight = TextUnit(35f, TextUnitType.Sp)
                        )
                    }
                } else
                    LazyListRuta(
                        modifier,
                        state = state,
                        items = items,
                        onSelect = { rut: Ruta ->
                            rutaSelected = rut
                        },
                        checkSelected = { other: Ruta -> rutaSelected?.equals(other) ?: false },
                        deleteFuncition = deleteFuncition,
                        viewFunction = viewFunction,
                        favoriteFuncion = favoriteFuncion
                    )

                BottomListActionBar(
                    modifier = Modifier
                        .height(60.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    showBar = firstItemVisible,
                    showTextOnSort = true,
                    addFunction = addFunction,
                    sortFunction = sortFunction
                )
            }
        }
    }
}

@Composable
fun LazyListRuta(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    items: List<Ruta> = emptyList(),
    onSelect: (rut: Ruta) -> Unit,
    checkSelected: (otro: Ruta) -> Boolean,
    viewFunction: (ruta: Ruta) -> Unit = {},
    deleteFuncition: suspend (ruta: Ruta) -> String = { "" },
    favoriteFuncion: suspend (ruta: Ruta, favorito: Boolean) -> Unit = { _, _ -> },
) {
    val shouldShowDialog = remember { mutableStateOf(false) }
    val rutaABorrar = remember { mutableStateOf<Ruta?>(null) }
    if (shouldShowDialog.value) {
        DeleteAlertDialogue(
            shouldShowDialog = shouldShowDialog,
            deleteFuncition = { rutaABorrar.value?.let { deleteFuncition(it) } ?: "" },
            nombreObjetoBorrado = "El ruta elegido"

        )
    }
    LazyColumn(

        modifier = modifier,
        state = state,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item {
            Spacer(Modifier.size(0.dp))
        }
        items(items) { item: Ruta ->
            RutaListable(
                ruta = item,
                onSelect = onSelect,
                selected = checkSelected(item),
                viewFunction = viewFunction,
                deleteFuncition = { ruta ->
                    rutaABorrar.value = ruta
                    shouldShowDialog.value = true
                },
                favoriteFuncion = favoriteFuncion

            )
        }
        item {
            Spacer(Modifier.size(30.dp))
        }
    }

}

@Composable
fun RutaListable(
    ruta: Ruta,
    onSelect: (rut: Ruta) -> Unit,
    selected: Boolean,
    viewFunction: (ruta: Ruta) -> Unit = {},
    deleteFuncition: (ruta: Ruta) -> Unit = {},
    favoriteFuncion: suspend (ruta: Ruta, favorito: Boolean) -> Unit = { _, _ -> },

    ) {
    var cambiandoFavorito by remember { mutableStateOf(false) }
    if (cambiandoFavorito) {
        LaunchedEffect(Unit) {
            favoriteFuncion(ruta, !ruta.isFavorito())
            cambiandoFavorito = false
        }
    }


    ObjetoListable(
        primaryInfo = ruta.getNombre(),
        secondaryInfo = (if (ruta.getVehiculo().tipo == TipoVehiculo.Pie) "A pie" else "Con " + ruta.getVehiculo().nombre) +
                "\n\n" +
                ruta.getCoste().toCleanCost(ruta.getVehiculo().tipo.getArquetipo()),
        terciaryInfo = ruta.getDistancia().toCleanDistance() + "\n\n" + ruta.getDuracion()
            .toTimeFormat(),
        onGeneralClick = { onSelect(ruta) },
        favoriteFuncion = { cambiandoFavorito = true },
        firstActionIcon = Icons.AutoMirrored.Filled.NotListedLocation,
        firstActionFunction = { viewFunction(ruta) },
        secondActionFuncition = { deleteFuncition(ruta) },
        favorito = ruta.isFavorito(),
        selected = selected,
        ratioHiddenFields = 0.6f

    )
}


@Preview
@Composable
private fun RutaListContentPreview() {
    val modifier: Modifier = Modifier
    RutasListContent(modifier, emptyList())
}

@Preview
@Composable
private fun RutaListContentVacioPreview() {
    val modifier: Modifier = Modifier
    RutasListContent(modifier, emptyList())
}

@Preview
@Composable
private fun PreviewListaRuta() {
    LazyListRuta(onSelect = {},
        checkSelected = { true })
}
