package es.uji.smallaris.ui.screens.lugares

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import es.uji.smallaris.model.lugares.LugarInteres

val saverLugarInteres: Saver<LugarInteres, *> = listSaver(
    save = { listOf(it.longitud, it.latitud, it.nombre, it.municipio) },
    restore = {
        LugarInteres(
            longitud = it[0] as Double,
            latitud = it[1] as Double,
            nombre = it[2] as String,
            municipio = it[3] as String
        )
    }
)
