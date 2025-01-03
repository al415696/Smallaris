package es.uji.smallaris.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import es.uji.smallaris.ui.navigation.LugaresDestination
import es.uji.smallaris.ui.navigation.RutasDestination
import es.uji.smallaris.ui.navigation.SmallarisDestination
import es.uji.smallaris.ui.navigation.UsuarioDestination
import es.uji.smallaris.ui.navigation.VehiculosDestination


@Composable
fun SmallarisNavBar(
    currentDestination: SmallarisDestination,
    onTabSelected: (SmallarisDestination) -> Unit,
    navigationEnabled: MutableState<Boolean>
) {


    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        TOP_LEVEL_DESTINATIONS.forEach { appDestination ->
            NavigationBarItem(
                selected = currentDestination.route == appDestination.route,
                onClick = {
                    if (navigationEnabled.value)
                        onTabSelected(appDestination)
                },
                icon = {
                    Icon(
                        imageVector = appDestination.icon,
                        contentDescription = appDestination.route
                    )
                }
            )
        }
    }
}

val TOP_LEVEL_DESTINATIONS = listOf(
    LugaresDestination,
    VehiculosDestination,
    RutasDestination,
    UsuarioDestination
)