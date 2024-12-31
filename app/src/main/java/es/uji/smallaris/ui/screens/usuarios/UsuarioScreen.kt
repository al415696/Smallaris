package es.uji.smallaris.ui.screens.usuarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.components.DeleteAlertDialogue
import es.uji.smallaris.ui.components.EnumDropDown
import es.uji.smallaris.ui.components.ErrorBubble
import es.uji.smallaris.ui.components.ListDropDown
import es.uji.smallaris.ui.components.LoadingCircle
import es.uji.smallaris.ui.state.UsuarioViewModel

@Composable
fun UsuarioScreen(
    viewModel: UsuarioViewModel
){
    UsuarioScreenContent(
        funCerrarSesion = viewModel::cerrarSesion,
        funEliminarCuenta =  viewModel::eliminarCuenta,
        funGetNombreUsuario = viewModel::getNombreUsuarioActual,
        funConseguirVehiculos = viewModel::getVehiculos,
        )
}

@Composable
fun UsuarioScreenContent(
    funCerrarSesion: suspend () -> String = {""},
    funEliminarCuenta: suspend () -> String = {""},
    funGetNombreUsuario: () -> String = { "test@gmail.es" },
    funConseguirVehiculos: suspend () -> List<Vehiculo> = { emptyList() },
    ){
    val listaTipoRuta = listOf(TipoRuta.Rapida, TipoRuta.Economica, TipoRuta.Corta)
    val currentDefaultTipoRuta = remember { mutableStateOf(TipoRuta.Rapida) }
    val currentDefaultVehiculo: MutableState<Vehiculo?> = remember { mutableStateOf(null) }
    val errorText: MutableState<String> = remember{ mutableStateOf("")}
    val listVehiculos = remember { mutableStateListOf<Vehiculo>() }
    var initialLoadEnded by remember { mutableStateOf(false) }
    val iniciadoCerrarSesion = remember { mutableStateOf(false) }
    val iniciadoEliminarCuenta = remember { mutableStateOf(false) }
    if (iniciadoCerrarSesion.value)
        CerrarSesionAlertDialogue(
            iniciadoCerrarSesion,
            cerrarFunction = { funCerrarSesion() }
        )
    if (iniciadoEliminarCuenta.value)
        DeleteAlertDialogue(
            iniciadoEliminarCuenta,
            deleteFuncition = {funEliminarCuenta()},
            "Tu cuenta junto a todos tus datos"
        )

    LaunchedEffect(Unit) {
        listVehiculos.addAll(funConseguirVehiculos())
        initialLoadEnded = true
    }
    Surface(modifier = Modifier.fillMaxSize(),
        color= MaterialTheme.colorScheme.primary) {

            Box(
                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(20.dp)
                ,
                contentAlignment = Alignment.Center
            )
            {


                Surface(
                    shape = MaterialTheme.shapes.large
                ) {

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
//                    .fillMaxSize()
                            .padding(20.dp)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
                    ) {
                        Surface(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 10.dp,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    modifier = Modifier.size(50.dp),
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = stringResource(R.string.default_description_text)
                                )
                                Column(
                                    modifier = Modifier,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .align(Alignment.Start),
                                        text = "Email:",
                                        textAlign = TextAlign.Start
                                    )
                                    Text(
                                        modifier = Modifier.fillMaxWidth(0.8f),
                                        text = funGetNombreUsuario(),
                                        textAlign = TextAlign.Center
                                    )
                                }

                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                enabled = !iniciadoEliminarCuenta.value,
                                onClick = { iniciadoCerrarSesion.value = true },
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,

                                    )
                            ) {
                                Text(text = "Cerrar sesión")
                            }
                            Button(
                                enabled = !iniciadoCerrarSesion.value,
                                onClick = { iniciadoEliminarCuenta.value = true },
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,

                                    )
                            ) {
                                Text(text = "Eliminar cuenta")
                            }
                        }
                        ErrorBubble(errorText)
                        Surface(modifier = Modifier,
                            tonalElevation = 15.dp,
                            shape = MaterialTheme.shapes.medium
                            ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(0.8f).padding(5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.Start),
                                    text = "Tipo de ruta por defecto"
                                )
                                Surface(
                                    modifier = Modifier.fillMaxWidth(0.4f),
                                    tonalElevation = 75.dp
                                ) {
                                    EnumDropDown(
                                        opciones = listaTipoRuta,
                                        elegida = currentDefaultTipoRuta
                                    )
                                }
                            }
                        }
                        Surface(modifier = Modifier,
                            tonalElevation = 15.dp,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(5.dp)) {
                                Text(text = "Medio de transporte por defecto")
                                Surface(
                                    modifier = Modifier.fillMaxWidth(0.8f),
                                    tonalElevation = 75.dp
                                ) {

                                    ListDropDown(
                                        modifier = Modifier.fillMaxWidth(),
                                        opciones = listVehiculos,
                                        elegida = currentDefaultVehiculo,
                                        shownValue = { objeto: Vehiculo? ->
                                            objeto?.nombre
                                                ?: if (initialLoadEnded) "Ningún vehiculo registrado" else "Cargando..."
                                        },
                                        notSelectedText = "Ningún vehiculo seleccionado"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        
    }
}
@Composable
fun CerrarSesionAlertDialogue(
    shouldShowDialog: MutableState<Boolean>,
    cerrarFunction:suspend () -> Unit
) {
    var confirmadoSalir by remember{ mutableStateOf(false) }
    if(confirmadoSalir)
        LaunchedEffect(Unit) {
            cerrarFunction()
            shouldShowDialog.value = false
        }
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },

            title = { Text(text = "¿Cerrar la sesión?") },
            text = {
                if (confirmadoSalir) {
                    Column {
                        Text(text = "Cerrando sesión...")
                        LoadingCircle(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
                else
                    Text(text = "Estás a punto de salir de la sesión")
            },
            confirmButton = {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    onClick = {
                        confirmadoSalir = true
                    }
                ) {
                    Text(
                        text = "Cerrar sesión",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        )
    }
}
@Preview
@Composable
fun PreviewUsuarioScreenContent(){
    UsuarioScreenContent()
}