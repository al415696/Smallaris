package es.uji.smallaris.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.uji.smallaris.ui.components.SmallarisNavBar
import es.uji.smallaris.ui.navigation.SmallarisDestination
import es.uji.smallaris.ui.navigation.SmallarisNavHost
import es.uji.smallaris.ui.navigation.UsuarioDestination
import es.uji.smallaris.ui.navigation.navigateSingleTopTo
import es.uji.smallaris.ui.theme.SmallarisTheme

@Composable
fun SmallarisApp(){
    SmallarisTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination
//        val currentScreen = rallyTabRowScreens.find { it.route == currentDestination?.route } ?: Overview
        val startDestination = UsuarioDestination
        Scaffold(
            bottomBar = {
                SmallarisNavBar(
                    startDestination =  startDestination,
                    onTabSelected ={
                        smallarisDestination : SmallarisDestination -> navController.navigateSingleTopTo(smallarisDestination.route)
                    }
                )
            }
        ) { innerPadding ->
            SmallarisNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                startDestination =  startDestination
            )
        }
    }
}