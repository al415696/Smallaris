package es.uji.smallaris.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LoadingCircle(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidthPercentage: Float = 0.15f
) {
    Box(modifier = modifier) {
        CircularProgressIndicator(
            modifier = Modifier.size(size).align(Alignment.Center),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = size * strokeWidthPercentage
        )
    }
//        Spacer(Modifier.size(totalWidth/2))
}
@Composable
@Preview
fun previewDefaultLoadingCircle(){
    LoadingCircle()
}
@Composable
@Preview
fun previewSmallLoadingCircle(){
    LoadingCircle(size = 32.dp)
}
@Composable
@Preview
fun previewBigLoadingCircle(){
    LoadingCircle(size = 128.dp)
}