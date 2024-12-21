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
import es.uji.smallaris.ui.screens.MapaScreen
import es.uji.smallaris.ui.screens.RutasScreen
import es.uji.smallaris.ui.screens.UsuarioScreen
import es.uji.smallaris.ui.screens.Vehiculos.VehiculosScreen
import es.uji.smallaris.ui.state.LugaresViewModel
import es.uji.smallaris.ui.state.MapaViewModel
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
    // Nota: puede que sea necesario quitar el remember y asignar con: viewModel<ClaseNuestraDeViewModel>()
    // Al añadir cualquier cosa a los viewModels también hay que actualizar el Saver.
    //los cosaXXXX son para mostrar el proceso de asignar y definir variables; no son definitivos
    val mapaViewModel = rememberSaveable(saver = MapaViewModel.Saver) { MapaViewModel() }
    val lugaresViewModel = rememberSaveable(saver = LugaresViewModel.Saver) { LugaresViewModel()}
    val vehiculosViewModel = rememberSaveable(saver =  VehiculosViewModel.Saver){ VehiculosViewModel()}
    val rutasViewModel = rememberSaveable(saver = RutasViewModel.Saver) { RutasViewModel()}
    val usuarioViewModel = rememberSaveable(saver = UsuarioViewModel.Saver) { UsuarioViewModel()}



    var loading by remember { mutableStateOf(true) }
    if (loading){
        LoadingScreen(
            loadingProcess = {
                //Vehiculos
//                vehiculosViewModel.debugFillList()
                vehiculosViewModel.updateList()
                //Lugares
//                lugaresViewModel.debugFillList()
                lugaresViewModel.updateList()
                vehiculosViewModel.updateList()
                navigationEnabled.value = true
            },
            onTimeout = { loading = false }
        )
    }
        else {
        NavHost(
            navController = navController,
            modifier = modifier,
            startDestination = startDestination.route
        )
        {


            composable(route = MapaDestination.route) {

                MapaScreen(
                    viewModel = mapaViewModel
//                onClickSeeAllAccounts = {
//                    navController.navigateSingleTopTo(Accounts.route)
//                },
//                onClickSeeAllBills = {
//                    navController.navigateSingleTopTo(Bills.route)
//                },
//                onAccountClick = { accountType ->
//                    navController.navigateToSingleAccount(accountType)
//                }
                )
            }
            composable(route = LugaresDestination.route) {
                LugaresScreen(
                    viewModel = lugaresViewModel
//                onAccountClick = { accountType ->
//                    navController.navigateToSingleAccount(accountType)
//                }
                )
            }
            composable(route = VehiculosDestination.route) {
                VehiculosScreen(
                    viewModel = vehiculosViewModel,
                    testFunction = { vehiculosViewModel.hacerCosa() }
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
