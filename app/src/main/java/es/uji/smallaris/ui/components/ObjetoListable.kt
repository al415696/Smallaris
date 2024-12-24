package es.uji.smallaris.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R

@Composable
fun ObjetoListable(
    modifier: Modifier = Modifier,
    primaryInfo: String,
    secondaryInfo: String,
    selected: Boolean = false,
    terciaryInfo: String = "",
    onGeneralClick: () -> Unit = {},
    favorito: Boolean = false,
    favoriteFuncion: () -> Unit = {},
    firstActionIcon: ImageVector = Icons.Rounded.Edit,
    firstActionFunction:() -> Unit = {},
    secondActionIcon: ImageVector = Icons.Rounded.Delete,
    secondActionFuncition: () -> Unit = {},
    closedHeight: Dp = 45.dp,
    ratioHiddenFields: Float = 0.5F

    ) {

    Surface(
        modifier = if (selected) modifier else  modifier.height(closedHeight),
        color =if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primaryContainer,
        onClick = {
            onGeneralClick()
            println("prueba")
        }
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                modifier = Modifier.align(Alignment.Top),
                onClick = favoriteFuncion
            ) {
                Icon(
                    imageVector = if (favorito) Icons.Filled.Favorite else
                        Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(R.string.default_description_text),
                    modifier = Modifier.fillMaxSize(),
                )
            }
            DisplayDataObjetoSimplificada(
                modifier = Modifier,
                primaryInfo = primaryInfo,
                secondaryInfo = secondaryInfo,
                terciaryInfo = terciaryInfo,
                expanded = selected,
                ratioHiddenFields = ratioHiddenFields
            )

            IconButton(
                onClick = firstActionFunction,
                modifier = Modifier.align(Alignment.Top),
            ) {
                Icon(
                    imageVector =  firstActionIcon,
                    stringResource(R.string.default_description_text),
                    modifier = Modifier.size(30.dp)
                )

            }
            IconButton(
                onClick = secondActionFuncition,
                modifier = Modifier.align(Alignment.Top),
            ) {
                Icon(
                    imageVector =  secondActionIcon,
                    stringResource(R.string.default_description_text),
                    modifier = Modifier.size(30.dp),
                )
            }

        }
    }
}

@Composable
fun DisplayDataObjetoSimplificada(
    modifier: Modifier = Modifier,
    primaryInfo: String,
    secondaryInfo: String,
    terciaryInfo: String = "",
    expanded: Boolean = true,
    ratioHiddenFields: Float = 0.5F
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxHeight()
            .width(250.dp).padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.Absolute.Left,
    ) {
        if (!expanded){
            Text(
                text =primaryInfo,
                style = MaterialTheme.typography.headlineSmall,
                modifier = modifier.fillMaxWidth(),
                maxLines = 1
            )
        }
        else{
            Column(
                modifier = modifier
                    .width(250.dp)
                    .fillMaxHeight()
                    .padding(vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp) //.Center
            ) {
                Text(
                    primaryInfo, style = MaterialTheme.typography.headlineSmall,
                    modifier = modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        secondaryInfo, style = MaterialTheme.typography.bodyMedium,
                        modifier = modifier.fillMaxWidth(ratioHiddenFields)
                    )
                    Text(
                        terciaryInfo, style = MaterialTheme.typography.bodyMedium,
                        modifier = modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

}
@Composable
fun DisplayDataObjetoSimplificada2(
    modifier: Modifier = Modifier,
    primaryInfo: String,
    secondaryInfo: String,
    terciaryInfo: String = "",
    expanded: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxHeight()
            .width(250.dp),
        horizontalArrangement = Arrangement.Absolute.Left,
    ) {
        if (terciaryInfo.isEmpty()) {
            Text(
                primaryInfo, style = MaterialTheme.typography.bodyLarge,
                modifier = modifier.fillMaxWidth(0.75F)
            )
            Text(
                secondaryInfo, style = MaterialTheme.typography.bodySmall,
                modifier = modifier.fillMaxWidth()
            )
        } else {
            Column(
                modifier = modifier
                    .width(250.dp)
                    .fillMaxHeight()
                    .padding(vertical = 5.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier
                        .fillMaxHeight(.25f)
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        primaryInfo, style = MaterialTheme.typography.bodyLarge,
                        modifier = modifier.fillMaxWidth(0.75F)
                    )
                    Text(
                        secondaryInfo, style = MaterialTheme.typography.bodySmall,
                        modifier = modifier.fillMaxWidth()
                    )
                }
//                HorizontalDivider()
                Text(
                    text = terciaryInfo,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxHeight(0.25f)
                )
            }
        }
    }

}

@Preview
@Composable
private fun previewVehiculoSinTerciary() {
    ObjetoListable(
        primaryInfo = "Prueba",
        secondaryInfo = "Prueba2"
    )
}

@Preview
@Composable
private fun previewVehiculoConTerciary() {
    ObjetoListable(
        primaryInfo = "Prueba",
        secondaryInfo = "Prueba2",
        terciaryInfo = "Prueba3"
    )
}

@Preview
@Composable
private fun previewVehiculoSeleccionadoConTerciary() {
    ObjetoListable(
        primaryInfo = "Prueba",
        secondaryInfo = "Prueba2",
        terciaryInfo = "Prueba3",
        selected = true
    )
}