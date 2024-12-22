package es.uji.smallaris.ui.screens.lugares

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import es.uji.smallaris.model.OrdenLugarInteres
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.ui.screens.lugares.LugarScreenContent
import es.uji.smallaris.ui.screens.lugares.LugaresAddContent
import es.uji.smallaris.ui.screens.lugares.LugaresListContent
import es.uji.smallaris.ui.state.LugaresViewModel

@Composable
fun LugaresScreen(
    viewModel: LugaresViewModel,
    onGoToLugar: (Double, Double) -> Unit = {_,_ ->}
){
    val modifier: Modifier = Modifier
    val items = viewModel.items
    val currentContent = remember { mutableStateOf(LugarScreenContent.Lista) }
    val currentUpdatedLugar: MutableState<LugarInteres> = remember { mutableStateOf(
        if (items.isEmpty())    LugarInteres(0.0,0.0,"Nada", "Abismo")

        else items[0]

    )
    }
    var currentOrderIndex: Int = 0


    Surface(color = MaterialTheme.colorScheme.primary) {
        println("items "+ items)

        when(currentContent.value){
            LugarScreenContent.Lista ->
                LugaresListContent(
                    modifier = modifier,
                    items = items,
                    favoriteFuncion = { lugarInteres: LugarInteres, favorito: Boolean ->  viewModel.setLugarFavorito(lugarInteres, favorito) },
                    addFunction = {currentContent.value = LugarScreenContent.Add},
                    sortFunction =
                    {
                        currentOrderIndex = (currentOrderIndex+1) % OrdenLugarInteres.entries.size
                        println(currentOrderIndex)
                        println(OrdenLugarInteres.entries[currentOrderIndex].toString())
                        viewModel.sortItems(OrdenLugarInteres.entries[currentOrderIndex])
                        OrdenLugarInteres.entries[currentOrderIndex].getNombre()
                    }
                    ,
                    deleteFuncition = {lugarInteres: LugarInteres ->  viewModel.deleteLugar(lugarInteres) }
                )
            LugarScreenContent.Add ->
                LugaresAddContent(
                    funAddLugar = {longitud: Double, latitud: Double, nombre: String ->viewModel.addLugar(longitud, latitud, nombre)},
                    onBack = {currentContent.value = LugarScreenContent.Lista },
                    funConseguirToponimos = viewModel::getToponimo,//{_,_-> ""}
                    funConseguirCoordenadas = viewModel::getCoordenadas
                )
        }
    }
}

private enum class LugarScreenContent(){
    Lista,
    Add
}