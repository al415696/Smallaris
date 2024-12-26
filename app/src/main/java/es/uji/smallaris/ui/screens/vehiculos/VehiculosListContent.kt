package es.uji.smallaris.ui.screens.vehiculos

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
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.components.BottomListActionBar
import es.uji.smallaris.ui.components.DeleteAlertDialogue
import es.uji.smallaris.ui.components.ObjetoListable
import es.uji.smallaris.ui.screens.toCleanString

@Composable
fun VehiculosListContent(
    modifier: Modifier,
    items: List<Vehiculo> = listOf(),
    addFunction: () -> Unit = {},
    sortFunction: () -> String = {""},
    deleteFuncition: suspend (vehiculo: Vehiculo) -> Unit = {},
    favoriteFuncion: suspend (vehiculo: Vehiculo, favorito: Boolean) -> Unit = { vehiculo, favorito ->},
    updateFunction:(viejo: Vehiculo) -> Unit = {}

) {
    var vehiculoSelected by remember {
        mutableStateOf(
            Vehiculo(
                "Coche",
                7.1,
                "1234BBB",
                TipoVehiculo.Gasolina95
            )
        )
    }
    val state = rememberLazyListState()
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
                            text = stringResource(R.string.sin_vehiculos_text),
                            textAlign = TextAlign.Center,
                            lineHeight = TextUnit(35f, TextUnitType.Sp)
                        )
                    }
                } else
                    LazyListVehiculos(
                        modifier,
                        state = state,
                        items = items,
                        onSelect = { veh: Vehiculo ->
                            vehiculoSelected = veh
                        },
                        checkSelected = { other: Vehiculo -> vehiculoSelected.equals(other) },
                        updateFunction = updateFunction,
                        deleteFuncition = deleteFuncition,
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
fun LazyListVehiculos(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    items: List<Vehiculo> = vehiculoTestData,
    onSelect: (veh: Vehiculo) -> Unit,
    checkSelected: (otro: Vehiculo)-> Boolean,// = {otro: Vehiculo -> false}
    deleteFuncition: suspend (vehiculo: Vehiculo) -> Unit = {},
    favoriteFuncion: suspend (vehiculo: Vehiculo, favorito: Boolean) -> Unit = {vehiculo,favorito ->},
    updateFunction:(viejo:Vehiculo) -> Unit = {}
) {
    val shouldShowDialog = remember { mutableStateOf(false )}
    val vehiculoABorrar = remember { mutableStateOf<Vehiculo?>(null )}
    if (shouldShowDialog.value) {
        DeleteAlertDialogue(shouldShowDialog = shouldShowDialog,
            deleteFuncition = { vehiculoABorrar.value?.let { deleteFuncition(it) } },
            nombreObjetoBorrado = "El vehículo elegido"

        )
    }
    LazyColumn(
        
        modifier = modifier,
        state = state,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item{
            Spacer(Modifier.size(0.dp))
        }
        items(items) { item: Vehiculo ->
            vehiculoListable(
                vehiculo = item,
                onSelect = onSelect,
                selected = checkSelected(item),
                deleteFuncition = {vehiculo ->
                    vehiculoABorrar.value = vehiculo
                    shouldShowDialog.value = true
                },
                updateFunction = updateFunction,
                favoriteFuncion = favoriteFuncion,


            )
        }
        item{
            Spacer(Modifier.size(30.dp))
        }
    }

}
@Composable
fun vehiculoListable(
    vehiculo: Vehiculo,
    onSelect: (veh: Vehiculo) -> Unit,
    selected: Boolean,
    addFuncion: (vehiculo: Vehiculo) -> Unit = {},
    deleteFuncition: (vehiculo: Vehiculo) -> Unit = {},
    favoriteFuncion: suspend (vehiculo: Vehiculo, favorito: Boolean) -> Unit = {vehiculo,favorito ->},
    updateFunction:(viejo:Vehiculo) -> Unit = {}

){
    var cambiandoFavorito by remember{ mutableStateOf(false)}
    if(cambiandoFavorito) {
        LaunchedEffect(Unit) {
            favoriteFuncion(vehiculo, !vehiculo.isFavorito())
            cambiandoFavorito = false
        }
    }

        ObjetoListable(
            primaryInfo = vehiculo.nombre,
            secondaryInfo = vehiculo.matricula,
            terciaryInfo =  "${vehiculo.consumo.toCleanString()} ${ArquetipoVehiculo.Combustible.getUnidad(vehiculo.tipo)}",
            onGeneralClick = { onSelect(vehiculo) },
            favoriteFuncion = { cambiandoFavorito = true },
            secondActionFuncition = { deleteFuncition(vehiculo) },
            firstActionFunction = {updateFunction(vehiculo)},
            favorito = vehiculo.isFavorito(),
            selected = selected,
            ratioHiddenFields = 0.4F

        )
}

val vehiculoTestData = listOf(
    Vehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95),
    Vehiculo("Unicornio", 77.7, "7777LLL", TipoVehiculo.Bici),
    Vehiculo(
        nombre = "Zyxcrieg",
        consumo = 6.66,
        matricula = "4444XXX",
        tipo = TipoVehiculo.Electrico,
        favorito = true
    ),
    Vehiculo(
        nombre = "Abobamasnow",
        consumo = 1.36,
        matricula = "1234DPP",
        tipo = TipoVehiculo.Gasolina95,
        favorito = false
    ),Vehiculo("MotoGP", 3.5, "6789MOT", TipoVehiculo.Gasolina98),
    Vehiculo("PatinElectrico", 1.1, "9999PAT", TipoVehiculo.Electrico),
    Vehiculo(nombre = "Cargobike", consumo = 0.5, matricula = "BIKE001", tipo = TipoVehiculo.Bici),
    Vehiculo(nombre = "MonsterTruck", consumo = 25.0, matricula = "TRUCK99", tipo = TipoVehiculo.Diesel),
    Vehiculo("Helicoptero", 120.0, "HELI007", TipoVehiculo.Gasolina98),
    Vehiculo(nombre = "SegwayMax", consumo = 0.2, matricula = "SEGWAYX", tipo = TipoVehiculo.Electrico),
    Vehiculo("JetSki", 50.0, "JSKI420", TipoVehiculo.Gasolina98),
    Vehiculo(nombre = "CamionMan", consumo = 35.0, matricula = "CAM5678", tipo = TipoVehiculo.Diesel),
    Vehiculo("TeslaCyber", 0.0, "CYBRTRK", TipoVehiculo.Electrico),
    Vehiculo("Zamboni", 8.0, "ICE9999", TipoVehiculo.Diesel),
    Vehiculo(nombre = "TractorRojo", consumo = 15.0, matricula = "TRC2345", tipo = TipoVehiculo.Gasolina95),
    Vehiculo(nombre = "GoKart", consumo = 4.2, matricula = "KART123", tipo = TipoVehiculo.Gasolina98),
    Vehiculo("CarrozaReal", 0.0, "REINA00", TipoVehiculo.Bici),
    Vehiculo(nombre = "Submarino", consumo = 300.0, matricula = "SUBMAR1", tipo = TipoVehiculo.Desconocido),
    Vehiculo("Furgoneta", 10.2, "FURG005", TipoVehiculo.Diesel)

)

@Preview
@Composable
private fun vehiculosListContentPreview() {
    val modifier: Modifier = Modifier
    VehiculosListContent(modifier, vehiculoTestData)
}
@Preview
@Composable
private fun vehiculosListContentVacioPreview() {
    val modifier: Modifier = Modifier
    VehiculosListContent(modifier, emptyList())
}

@Preview
@Composable
private fun previewListaVehiculos() {
    LazyListVehiculos(onSelect =  {},
        checkSelected = {true})
}