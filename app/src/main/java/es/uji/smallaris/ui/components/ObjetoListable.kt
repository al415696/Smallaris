package es.uji.smallaris.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R

@Composable
fun ObjetoListable(
    modifier: Modifier = Modifier,
    nombre: String,
    secondaryInfo: String,
    terciaryInfo: String = "",
    onGeneralClick: () ->Unit = {},
    selected: Boolean = false,
    favorito: Boolean = false,
    deleteFuncition: () -> Unit = {},
    favoriteFuncion: () -> Unit = {},
    updateFunction:() -> Unit = {}
) {
    Surface(
        modifier = modifier,
        color = if (selected) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primaryContainer,
        onClick = {
            onGeneralClick()
            println("prueba")
        }
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(45.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = favoriteFuncion
            ) {
                Icon(
                    imageVector = if (favorito) Icons.Filled.Favorite else
                        Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(R.string.default_description_text),
                    modifier = Modifier.size(35.dp),
                )
            }
            DisplayDataObjetoSimplificada(
                modifier = Modifier,
                nombre = nombre,
                secondaryInfo = secondaryInfo,
                terciaryInfo = terciaryInfo
            )

            IconButton(
                onClick = updateFunction
            ) {
                Icon(
                    Icons.Filled.Edit, stringResource(R.string.default_description_text),
                    modifier = Modifier.size(30.dp)
                )

            }
            IconButton(
                onClick = deleteFuncition
            ) {
                Icon(
                    Icons.Filled.Delete, stringResource(R.string.default_description_text),
                    modifier = Modifier.size(30.dp),
                )
            }

        }
    }
}

@Composable
fun DisplayDataObjetoSimplificada(
    modifier: Modifier = Modifier,
    nombre: String,
    secondaryInfo: String,
    terciaryInfo: String = ""
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(45.dp)
            .width(250.dp),
        horizontalArrangement = Arrangement.Absolute.Left,
    ) {
        if (terciaryInfo.isEmpty()) {
            Text(
                nombre, style = MaterialTheme.typography.bodyLarge,
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
                    .height(45.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.height(30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        nombre, style = MaterialTheme.typography.bodyLarge,
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
                    modifier = modifier.padding(horizontal = 10.dp)
                )
            }
        }
    }

}