/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.uji.smallaris.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import es.uji.smallaris.ui.navigation.LugaresDestination
import es.uji.smallaris.ui.navigation.MapaDestination
import es.uji.smallaris.ui.navigation.RutasDestination
import es.uji.smallaris.ui.navigation.SmallarisDestination
import es.uji.smallaris.ui.navigation.UsuarioDestination
import es.uji.smallaris.ui.navigation.VehiculosDestination


@Composable
fun SmallarisNavBar(
    startDestination: SmallarisDestination,
    onTabSelected: (SmallarisDestination) -> Unit
) {
    var selectedDestination by rememberSaveable() { mutableStateOf(startDestination.route)}


    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        TOP_LEVEL_DESTINATIONS.forEach { appDestination ->
            NavigationBarItem(
                selected = selectedDestination == appDestination.route,
                onClick = {
                    selectedDestination = appDestination.route
                    onTabSelected(appDestination)
                          },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(appDestination.icon),
                        contentDescription =  appDestination.route
                    )
                }
            )
        }
    }
}
val TOP_LEVEL_DESTINATIONS = listOf(
    MapaDestination,
    LugaresDestination,
    VehiculosDestination,
    RutasDestination,
    UsuarioDestination
)