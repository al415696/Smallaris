package es.uji.smallaris.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import es.uji.smallaris.ui.screens.LoadingScreen
import es.uji.smallaris.ui.screens.lugares.LugaresScreen
import es.uji.smallaris.ui.screens.rutas.RutasScreen
import es.uji.smallaris.ui.screens.usuarios.LoginScreen
import es.uji.smallaris.ui.screens.usuarios.UsuarioScreen
import es.uji.smallaris.ui.screens.vehiculos.VehiculosScreen
import es.uji.smallaris.ui.state.LugaresViewModel
import es.uji.smallaris.ui.state.RutasViewModel
import es.uji.smallaris.ui.state.UsuarioViewModel
import es.uji.smallaris.ui.state.VehiculosViewModel

@Composable
fun SmallarisNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: SmallarisDestination,
    navigationEnabled: MutableState<Boolean>

) {
    val lugaresViewModel = rememberSaveable(saver = LugaresViewModel.Saver) { LugaresViewModel() }
    val vehiculosViewModel =
        rememberSaveable(saver = VehiculosViewModel.Saver) { VehiculosViewModel() }
    val rutasViewModel = rememberSaveable(saver = RutasViewModel.Saver) { RutasViewModel() }
    val usuarioViewModel = rememberSaveable(saver = UsuarioViewModel.Saver) { UsuarioViewModel() }

    var loadingServiciosObjetos by remember { mutableStateOf(true) }
    if (!usuarioViewModel.sesionIniciada) {
        LoginScreen(
            viewModel = usuarioViewModel,
            modifier = modifier // Pasar el modificador aquí
        ) { loadingServiciosObjetos = true }
        
    } else if (loadingServiciosObjetos) {
        LoadingScreen(
            loadingProcess = {
                //Vehiculos
                vehiculosViewModel.initializeList()

                //Lugares
                lugaresViewModel.initializeList()

                //Rutas
                rutasViewModel.initializeList()


            },
            onTimeout = {
                loadingServiciosObjetos = false
                navigationEnabled.value = true
            }
        )
    } else {
        NavHost(
            navController = navController,
            modifier = modifier,
            startDestination = startDestination.route
        )
        {


            composable(route = LugaresDestination.route) {
                LugaresScreen(
                    viewModel = lugaresViewModel
                )
            }
            composable(route = VehiculosDestination.route) {
                VehiculosScreen(
                    viewModel = vehiculosViewModel
                )
            }
            composable(route = RutasDestination.route) {
                RutasScreen(
                    viewModel = rutasViewModel
                )
            }
            composable(route = UsuarioDestination.route) {
                UsuarioScreen(
                    viewModel = usuarioViewModel
                )
            }

        }
    }

}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
