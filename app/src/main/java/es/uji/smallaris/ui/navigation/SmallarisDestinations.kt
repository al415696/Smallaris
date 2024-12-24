package es.uji.smallaris.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.ui.graphics.vector.ImageVector


interface SmallarisDestination {
    val icon: ImageVector
    val route: String
}

object MapaDestination : SmallarisDestination {
    override val icon = Icons.Filled.Map

    override val route = "screens/MapaScreen"
}

object LugaresDestination : SmallarisDestination {
    override val icon = Icons.Filled.LocationOn
    override val route = "screens/LugaresScreen"
}

object VehiculosDestination : SmallarisDestination {
    override val icon = Icons.Filled.DirectionsCar
    override val route = "screens/VehiculosScreen"
}

object RutasDestination : SmallarisDestination {
    override val icon = Icons.Filled.Route
    override val route = "screens/rutasScreen"
}

object UsuarioDestination : SmallarisDestination {
    override val icon = Icons.Filled.Person
    override val route = "screens/UsuarioScreen"
}