package es.uji.smallaris.ui.screens.lugares

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
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.ui.components.BottomListActionBar
import es.uji.smallaris.ui.components.DeleteAlertDialogue
import es.uji.smallaris.ui.components.ObjetoListable
import java.util.Locale

@Composable
fun LugaresListContent(
    modifier: Modifier,
    items: List<LugarInteres> = listOf(),
    addFunction: () -> Unit = {},
    viewFunction: (lugar: LugarInteres) -> Unit = {},
    sortFunction: () -> String = {""},
    deleteFuncition: suspend (lugarInteres: LugarInteres) -> Unit = {},
    favoriteFuncion: suspend (lugarInteres: LugarInteres, favorito: Boolean) -> Unit = { _, _ ->},
    state: LazyListState = rememberLazyListState()

) {
    var lugarInteresSelected by remember {
        mutableStateOf(
            LugarInteres(0.0,0.0,"Nada", "Abismo")

        )
    }
    val firstItemVisible by remember {
        derivedStateOf {
            state.firstVisibleItemIndex == 0
        }
    }
//    var nombreOrdenActual by remember { mutableStateOf("") }

    Column {
//        if (nombreOrdenActual.isNotEmpty())
//            Surface(modifier= Modifier.fillMaxWidth(),color = MaterialTheme.colorScheme.secondary){
//                Text(text = nombreOrdenActual)
//            }
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
                            text = stringResource(R.string.sin_lugaresInteres_text),
                            textAlign = TextAlign.Center,
                            lineHeight = TextUnit(35f, TextUnitType.Sp)
                        )
                    }
                } else
                    LazyListLugarInteres(
                        modifier,
                        state = state,
                        items = items,
                        onSelect = { lug: LugarInteres ->
                            lugarInteresSelected = lug
                        },
                        checkSelected = { other: LugarInteres -> lugarInteresSelected.equals(other) },
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
//                    sortFunction = {nombreOrdenActual = "Ordenado por " + sortFunction() }
                    sortFunction = sortFunction
                )
            }
        }
    }
}
@Composable
fun LazyListLugarInteres(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    items: List<LugarInteres> = lugarInteresTestData,
    onSelect: (lug: LugarInteres) -> Unit,
    checkSelected: (otro: LugarInteres)-> Boolean,// = {otro: LugarInteres -> false}
    viewFunction: (lugar: LugarInteres) -> Unit = {},
    deleteFuncition: suspend (lugarInteres: LugarInteres) -> Unit = {},
    favoriteFuncion: suspend (lugarInteres: LugarInteres, favorito: Boolean) -> Unit = {lugarInteres,favorito ->},
) {
    val shouldShowDialog = remember { mutableStateOf(false )}
    val lugarInteresABorrar = remember { mutableStateOf<LugarInteres?>(null )}
    if (shouldShowDialog.value) {
        DeleteAlertDialogue(shouldShowDialog = shouldShowDialog,
            deleteFuncition = { lugarInteresABorrar.value?.let { deleteFuncition(it) } },
            nombreObjetoBorrado = "El lugar elegido"

        )
    }
    LazyColumn(
        
        modifier = modifier,
        state = state,
        verticalArrangement = Arrangement.spacedBy(4.dp),
//        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item{
            Spacer(Modifier.size(0.dp))
        }
        items(items) { item: LugarInteres ->
            lugarInteresListable(
                lugarInteres = item,
                onSelect = onSelect,
                selected = checkSelected(item),
                viewFunction = viewFunction,
                deleteFuncition = {lugarInteres ->
                    lugarInteresABorrar.value = lugarInteres
                    shouldShowDialog.value = true
                },
                favoriteFuncion = favoriteFuncion

            )
        }
        item{
            Spacer(Modifier.size(30.dp))
        }
    }

}
@Composable
fun lugarInteresListable(
    lugarInteres: LugarInteres,
    onSelect: (lug: LugarInteres) -> Unit,
    selected: Boolean,
    viewFunction: (lugar: LugarInteres) -> Unit = {},
    deleteFuncition: (lugarInteres: LugarInteres) -> Unit = {},
    favoriteFuncion: suspend (lugarInteres: LugarInteres, favorito: Boolean) -> Unit = {lugarInteres,favorito ->},

){
    var cambiandoFavorito by remember{ mutableStateOf(false)}
    if(cambiandoFavorito) {
        LaunchedEffect(Unit) {
            favoriteFuncion(lugarInteres, !lugarInteres.isFavorito())
            cambiandoFavorito = false
        }
    }

        ObjetoListable(
            primaryInfo = lugarInteres.nombre,
            secondaryInfo =  lugarInteres.municipio,
            terciaryInfo ="N ${lugarInteres.latitud.toReasonableString()}\nW ${ lugarInteres.longitud.toReasonableString()}",
            onGeneralClick = { onSelect(lugarInteres) },
            favoriteFuncion = { cambiandoFavorito = true },
            firstActionIcon = Icons.AutoMirrored.Filled.NotListedLocation,
            firstActionFunction = {viewFunction(lugarInteres)},
            secondActionFuncition = { deleteFuncition(lugarInteres)},
            favorito = lugarInteres.isFavorito(),
            selected = selected,
            ratioHiddenFields = 0.6f

        )
}

val lugarInteresTestData = listOf(
    LugarInteres(15.8567, 92.5188, "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España", "Castellón de la Plana"),
    LugarInteres(40.4168, -3.7038, "Puerta del Sol, Madrid, España", "Madrid"),
    LugarInteres(41.3825, 2.1769, "Sagrada Familia, Barcelona, Cataluña, España", "Barcelona"),
    LugarInteres(37.3891, -5.9845, "La Giralda, Sevilla, Andalucía, España", "Sevilla"),
    LugarInteres(39.4699, -0.3763, "Ciudad de las Artes y las Ciencias, Valencia, Comunidad Valenciana, España", "Valencia"),
    LugarInteres(43.2630, -2.9350, "Museo Guggenheim, Bilbao, País Vasco, España", "Bilbao"),
    LugarInteres(36.7213, -4.4214, "La Alcazaba, Málaga, Andalucía, España", "Málaga"),
    LugarInteres(39.8628, -4.0273, "El Alcázar, Toledo, Castilla-La Mancha, España", "Toledo"),
    LugarInteres(42.8584, -2.6819, "San Juan de Gaztelugatxe, Bermeo, País Vasco, España", "Bermeo"),
    LugarInteres(38.3452, -0.4811, "Castillo de Santa Bárbara, Alicante, Comunidad Valenciana, España", "Alicante"),
    LugarInteres(40.4168, -3.7038, "Parque de las Aves", "Madrid"),
    LugarInteres(41.3825, 2.1769, "Cascada de los Elfos", "Barcelona"),
    LugarInteres(37.3891, -5.9845, "Bosque Encantado", "Sevilla"),
    LugarInteres(39.4699, -0.3763, "Puente del Dragón", "Valencia"),
    LugarInteres(43.2630, -2.9350, "Lago de Cristal", "Bilbao"),
    LugarInteres(36.7213, -4.4214, "Casa del Tiempo", "Málaga"),
    LugarInteres(39.8628, -4.0273, "Monte de los Suspiros", "Toledo"),
    LugarInteres(42.8584, -2.6819, "Cueva del Relámpago", "Bermeo"),
    LugarInteres(38.3452, -0.4811, "Palacio de las Sombras", "Alicante"),
    LugarInteres(40.9631, -5.6698, "Jardines del Silencio", "Salamanca"),
    LugarInteres(42.6986, -1.6323, "Torre de la Eternidad", "Pamplona"),
    LugarInteres(41.6561, -0.8773, "Templo de los Milagros", "Zaragoza"),
    LugarInteres(37.9834, -1.1280, "Camino de los Ancestros", "Murcia"),
    LugarInteres(28.4682, -16.2546, "Isla de las Almas", "Santa Cruz de Tenerife")
)


@Preview
@Composable
private fun lugarInteresListContentPreview() {
    val modifier: Modifier = Modifier
    LugaresListContent(modifier, lugarInteresTestData)
}
@Preview
@Composable
private fun lugarInteresListContentVacioPreview() {
    val modifier: Modifier = Modifier
    LugaresListContent(modifier, emptyList())
}

@Preview
@Composable
private fun previewListaLugarInteres() {
    LazyListLugarInteres(onSelect =  {},
        checkSelected = {true})
}
fun Double.toReasonableString(): String {
    return String.format(Locale.US,"%.5f", this)

}