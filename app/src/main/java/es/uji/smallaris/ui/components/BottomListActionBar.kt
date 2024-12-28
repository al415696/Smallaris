package es.uji.smallaris.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uji.smallaris.R

@Composable
fun BottomListActionBar(
    modifier: Modifier,
    showBar: Boolean = true,
    showTextOnSort: Boolean = false,
    addFunction: () -> Unit = {},
    sortFunction: () -> String = {""}
) {
    var showTextNow by remember{ mutableStateOf(false)}
    var shownText by remember{ mutableStateOf("")}
    if (showBar) {
        val containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        val contentColor = MaterialTheme.colorScheme.onSurface
        var currentAlpha by remember { mutableStateOf(1F) }
        Row(
            modifier = modifier.padding(bottom = 5.dp, start = 5.dp, end = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,

            ) {
            Row(Modifier.weight(1f)) {
                FloatingActionButton(
                    onClick = {
                        shownText ="Ordenado por " + sortFunction()
                        showTextNow = true
                        currentAlpha=1F
                              },
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(55.dp),
                    containerColor = containerColor,
                    contentColor = contentColor
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Sort,
                        stringResource(R.string.default_description_text),
                        modifier = Modifier.fillMaxSize(),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                }
                if (showTextOnSort && showTextNow){

                    val opacity = animateFloatAsState(
                        targetValue = currentAlpha,
                        animationSpec =  tween(
                            if(currentAlpha>0F) 0 else 1500
                            , easing = FastOutSlowInEasing
                        )
                    )
                    if (opacity.value == 0F){
                        showTextNow = false
                    }
                    else{
                        currentAlpha = 0F
                    }
                    Surface (modifier = Modifier
                        .alpha(
                            opacity.value
                        )
                        .align(Alignment.Bottom)
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            text = shownText,
                            maxLines = 2
                        )
                    }


                }
            }

            FloatingActionButton(
                onClick = addFunction, modifier = Modifier
                    .fillMaxHeight()
                    .width(55.dp),
                containerColor = containerColor,
                contentColor = contentColor
            ) {
                Icon(
                    Icons.Filled.AddCircle,
                    //                    ImageVector.vectorResource(R.drawable.directions_car_24px),
                    stringResource(R.string.default_description_text),
                    modifier = Modifier.fillMaxSize(),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
@Preview
@Composable
fun PreviewBar(){
    BottomListActionBar(modifier = Modifier
        .height(60.dp)
        .fillMaxWidth())
}