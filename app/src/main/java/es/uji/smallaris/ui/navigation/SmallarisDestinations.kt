package es.uji.smallaris.ui.navigation

import es.uji.smallaris.R


interface SmallarisDestination {
    val icon: Int
    val route: String
}

object MapaDestination : SmallarisDestination {
    override val icon = R.drawable.map_24px

    override val route = "screens/MapaScreen"
}

object LugaresDestination : SmallarisDestination {
    override val icon = R.drawable.location_on_24px
    override val route = "screens/LugaresScreen"
}

object VehiculosDestination : SmallarisDestination {
    override val icon = R.drawable.directions_car_24px
    override val route = "screens/VehiculosScreen"
}

object RutasDestination : SmallarisDestination {
    override val icon = R.drawable.explore_24px
    override val route = "screens/rutasScreen"
}

object UsuarioDestination : SmallarisDestination {
    override val icon = R.drawable.person_24px
    override val route = "screens/UsuarioScreen"
}