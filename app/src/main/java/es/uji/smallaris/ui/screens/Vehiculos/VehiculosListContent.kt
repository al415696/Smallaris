package es.uji.smallaris.ui.screens.Vehiculos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.components.ObjetoListable

@Composable
fun VehiculosListContent(
    modifier: Modifier,
    items: List<Vehiculo> = listOf(),
    addFunction: () -> Unit = {},
    deleteFuncition: (vehiculo: Vehiculo) -> Unit = {},
    favoriteFuncion: (vehiculo: Vehiculo, favorito: Boolean) -> Unit = { vehiculo, favorito ->},
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
    Surface(color = MaterialTheme.colorScheme.primary) {

        Column(
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = modifier
                    .height(55.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,

                ) {

                IconButton(onClick = {}, modifier = modifier.size(75.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
//                    ImageVector.vectorResource(R.drawable.directions_car_24px),
                        stringResource(R.string.default_description_text),
                        modifier = modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                IconButton(onClick = addFunction, modifier = modifier.size(75.dp)) {
                    Icon(
                        Icons.Filled.AddCircle,
//                    ImageVector.vectorResource(R.drawable.directions_car_24px),
                        stringResource(R.string.default_description_text),
                        modifier = modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

            }
            LazyListVehiculos(
                modifier.weight(1F),
                items = items,
                onSelect= {
                        veh: Vehiculo -> vehiculoSelected = veh
                    println(vehiculoSelected)
                },
                checkSelected = {other: Vehiculo -> vehiculoSelected.equals(other) },
                updateFunction = updateFunction
            )


        }
    }
}
@Composable
fun LazyListVehiculos(
    modifier: Modifier = Modifier,
    items: List<Vehiculo> = vehiculoTestData,
    onSelect: (veh: Vehiculo) -> Unit,
    checkSelected: (otro: Vehiculo)-> Boolean,// = {otro: Vehiculo -> false}
    addFuncion: (vehiculo: Vehiculo) -> Unit = {},
    deleteFuncition: (vehiculo: Vehiculo) -> Unit = {},
    favoriteFuncion: (vehiculo: Vehiculo, favorito: Boolean) -> Unit = {vehiculo,favorito ->},
    updateFunction:(viejo:Vehiculo) -> Unit = {}
) {
    val shouldShowDialog = remember { mutableStateOf(false )}
    val vehiculoABorrar = remember { mutableStateOf<Vehiculo?>(null )}
    if (shouldShowDialog.value) {
        DeleteAlertDialogue(shouldShowDialog = shouldShowDialog,
            deleteFuncition = { vehiculoABorrar.value?.let { deleteFuncition(it) } }

        )
    }
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
//        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items) { item: Vehiculo ->
            vehiculoListable(
                vehiculo = item,
                onSelect = onSelect,
                selected = checkSelected(item),
                deleteFuncition = {vehiculo ->
                    vehiculoABorrar.value = vehiculo
                    shouldShowDialog.value = true
                },
                updateFunction = updateFunction

            )
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
    favoriteFuncion: (vehiculo: Vehiculo, favorito: Boolean) -> Unit = {vehiculo,favorito ->},
    updateFunction:(viejo:Vehiculo) -> Unit = {}

){
    ObjetoListable(
        nombre = vehiculo.nombre,
        secondaryInfo = vehiculo.consumo.toString(),
        onGeneralClick = { onSelect(vehiculo) },
        selected = selected,
        favoriteFuncion = { favoriteFuncion(vehiculo,!vehiculo.isFavorito()) },
        deleteFuncition = { deleteFuncition(vehiculo) },
        updateFunction = {updateFunction(vehiculo)},
        favorito = vehiculo.isFavorito()

    )
}
@Composable
fun DeleteAlertDialogue(
    shouldShowDialog: MutableState<Boolean>,
    deleteFuncition: () -> Unit
) {
    if (shouldShowDialog.value) { // 2
        AlertDialog( // 3
            onDismissRequest = { // 4
                shouldShowDialog.value = false
            },
            // 5
            title = { Text(text = "¿Seguro que quieres borrar?") },
            text = { Text(text = "El vehiculo se borrará permantentemente") },
            confirmButton = { // 6
                Button(
                    onClick = {
                        shouldShowDialog.value = false
                        deleteFuncition()
                    }
                ) {
                    Text(
                        text = "Borrar"
                    )
                }
            }
        )
    }
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
private fun previewVehiculoSinTerciary() {
    ObjetoListable(
        nombre = "Prueba",
        secondaryInfo = "Prueba2"
    )
}

@Preview
@Composable
private fun previewVehiculoConTerciary() {
    ObjetoListable(
        nombre = "Prueba",
        secondaryInfo = "Prueba2",
        terciaryInfo = "Prueba3"
    )
}
@Preview
@Composable
private fun previewListaVehiculos() {
    LazyListVehiculos(onSelect =  {},
        checkSelected = {true})
}