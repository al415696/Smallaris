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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R
import es.uji.smallaris.model.TipoRuta
import es.uji.smallaris.model.Vehiculo
import es.uji.smallaris.ui.components.DeleteAlertDialogue
import es.uji.smallaris.ui.components.ErrorBubble
import es.uji.smallaris.ui.components.ExternalEnumDropDown
import es.uji.smallaris.ui.components.ListDropDown
import es.uji.smallaris.ui.components.LoadingCircle
import es.uji.smallaris.ui.components.SmallarisTitle
import es.uji.smallaris.ui.screens.vehiculos.saverVehiculo
import es.uji.smallaris.ui.state.UsuarioViewModel

@Composable
fun UsuarioScreen(
    viewModel: UsuarioViewModel
) {
    UsuarioScreenContent(
        funCerrarSesion = viewModel::cerrarSesion,
        funEliminarCuenta = viewModel::eliminarCuenta,
        funGetNombreUsuario = viewModel::getNombreUsuarioActual,
        funConseguirVehiculos = viewModel::getVehiculos,
        funGetVehiculoPorDefecto = viewModel::getDefaultVehiculo,
        funSetVehiculoPorDefecto = viewModel::setDefaultVehiculo,
        funGetTipoRutaPorDefecto = viewModel::getDefaultTipoRuta,
        funSetTipoRutaPorDefecto = viewModel::setDefaultTipoRuta,
        funCambiarContrasena = viewModel::cambiarContrasenya
    )
}

@Composable
fun UsuarioScreenContent(
    funCerrarSesion: suspend () -> String = { "" },
    funEliminarCuenta: suspend () -> String = { "" },
    funGetNombreUsuario: () -> String = { "test@gmail.es" },
    funConseguirVehiculos: suspend () -> List<Vehiculo> = { emptyList() },
    funGetVehiculoPorDefecto: suspend () -> Vehiculo? = { null },
    funSetVehiculoPorDefecto: suspend (Vehiculo) -> Boolean = { false },
    funGetTipoRutaPorDefecto: suspend () -> TipoRuta? = { null },
    funSetTipoRutaPorDefecto: suspend (TipoRuta) -> Boolean = { false },
    funCambiarContrasena: suspend (vieja: String, nueva: String) -> String = {_,_ -> ""}
) {
    val listaTipoRuta = listOf(TipoRuta.Rapida, TipoRuta.Economica, TipoRuta.Corta)
    val currentDefaultTipoRuta = rememberSaveable { mutableStateOf(TipoRuta.Rapida) }
    val realDefaultTipoRuta: MutableState<TipoRuta?> = remember { mutableStateOf(null) }
    val currentDefaultVehiculo: MutableState<Vehiculo?> =
        rememberSaveable(stateSaver = saverVehiculo) { mutableStateOf(null) }
    val realDefaultVehiculo: MutableState<Vehiculo?> = remember { mutableStateOf(null) }
    val errorText: MutableState<String> = remember { mutableStateOf("") }
    val listVehiculos = remember { mutableStateListOf<Vehiculo>() }
    var initialLoadEnded by rememberSaveable { mutableStateOf(false) }
    val iniciadoCerrarSesion = remember { mutableStateOf(false) }
    val iniciadoEliminarCuenta = remember { mutableStateOf(false) }
    val iniciadoCambiarContrasena = remember { mutableStateOf(false) }
    if (iniciadoCerrarSesion.value)
        CerrarSesionAlertDialogue(
            iniciadoCerrarSesion,
            cerrarFunction = funCerrarSesion
        )
    if (iniciadoEliminarCuenta.value)
        DeleteAlertDialogue(
            iniciadoEliminarCuenta,
            deleteFuncition = funEliminarCuenta,
            "Tu cuenta junto a todos tus datos"
        )
    if (iniciadoCambiarContrasena.value)
        CambiarContrasenaAlertDialogue(
            iniciadoCambiarContrasena,
            funCambiarContrasena = funCambiarContrasena,
        )

    LaunchedEffect(Unit) {
        listVehiculos.addAll(funConseguirVehiculos())
        realDefaultVehiculo.value = funGetVehiculoPorDefecto()
        currentDefaultVehiculo.value = funGetVehiculoPorDefecto()
        realDefaultTipoRuta.value = funGetTipoRutaPorDefecto()
        currentDefaultTipoRuta.value = funGetTipoRutaPorDefecto() ?: TipoRuta.Rapida
        initialLoadEnded = true
    }
    if (currentDefaultVehiculo.value != realDefaultVehiculo.value) {
        LaunchedEffect(Unit) {
            currentDefaultVehiculo.value?.let {
                if (funSetVehiculoPorDefecto(it)) {
                    realDefaultVehiculo.value = currentDefaultVehiculo.value
                }
            }
        }

    }
    if (currentDefaultTipoRuta.value != realDefaultTipoRuta.value && initialLoadEnded) {
        LaunchedEffect(Unit) {
            if (funSetTipoRutaPorDefecto(currentDefaultTipoRuta.value)) {
                realDefaultTipoRuta.value = currentDefaultTipoRuta.value
            }

        }

    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        )
        {


            Surface(
                shape = MaterialTheme.shapes.large
            ) {

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
                ) {
                    SmallarisTitle()
                    Surface(
                        modifier = Modifier,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 10.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier) {
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
                            Button(modifier = Modifier.align(Alignment.End),
                                enabled = !iniciadoEliminarCuenta.value && !iniciadoCerrarSesion.value,
                                onClick = {iniciadoCambiarContrasena.value = true},
                                colors = ButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )){
                                Text(text = "Cambiar contraseña")
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            enabled = !iniciadoEliminarCuenta.value && !iniciadoCambiarContrasena.value,
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
                            enabled = !iniciadoCerrarSesion.value && !iniciadoCambiarContrasena.value,
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
                    Surface(
                        modifier = Modifier,
                        tonalElevation = 15.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(5.dp),
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
                                ExternalEnumDropDown(
                                    opciones = listaTipoRuta,
                                    elegida = currentDefaultTipoRuta,
                                    cargadoEnded = initialLoadEnded
                                )
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier,
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
    cerrarFunction: suspend () -> String
) {
    var confirmadoSalir by remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }

    if (confirmadoSalir)
        LaunchedEffect(Unit) {
            errorText.value = cerrarFunction()
            if (errorText.value.isEmpty())
                shouldShowDialog.value = false
            confirmadoSalir = false
        }
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                if (!confirmadoSalir)
                    shouldShowDialog.value = false
            },

            title = { Text(text = "¿Cerrar la sesión?") },
            text = {
                if (confirmadoSalir) {
                    Column {
                        Text(text = "Cerrando sesión...")
                        LoadingCircle(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                } else
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

@Composable
fun CambiarContrasenaAlertDialogue(
    shouldShowDialog: MutableState<Boolean>,
    funCambiarContrasena: suspend (vieja: String, nueva: String) -> String = {_,_ -> "" },
) {
    val vieja = remember { mutableStateOf("") }
    val nueva = remember { mutableStateOf("") }
    var confirmadoChange by remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }

    if (confirmadoChange)
        LaunchedEffect(Unit) {
            errorText.value = funCambiarContrasena(vieja.value, nueva.value)
            confirmadoChange = false
            if (errorText.value.isEmpty())
                shouldShowDialog.value = false
        }
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                if (!confirmadoChange)
                    shouldShowDialog.value = false
            },

            title = {
                Text(
                    text = "Cambiar la contraseña de la cuenta",
                    textAlign = TextAlign.Center

                )
            },
            text = {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(value = vieja.value,
                        onValueChange = { vieja.value = it },
                        supportingText = { Text(text = "Contraseña actual") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    TextField(value = nueva.value,
                        onValueChange = { nueva.value = it },
                        supportingText = { Text(text = "Contraseña nueva") },
                        visualTransformation = PasswordVisualTransformation()
                    )

                    if (confirmadoChange) {
                        Column {
                            Text(text = "Cambiando...")
                            LoadingCircle(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                    }
                    ErrorBubble(errorText = errorText)
                }
            },
            confirmButton = {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    onClick = {
                        confirmadoChange = true
                    },
                    colors = ButtonColors(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onTertiaryContainer,
                        MaterialTheme.colorScheme.surfaceDim,
                        MaterialTheme.colorScheme.onSurface,
                    ),
                ) {
                    Text(
                        text = "Confirmar",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun PreviewUsuarioScreenContent() {
    UsuarioScreenContent()
}