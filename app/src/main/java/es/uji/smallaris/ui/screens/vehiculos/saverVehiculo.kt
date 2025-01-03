package es.uji.smallaris.ui.screens.vehiculos

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.Vehiculo

// Define cómo guardar y restaurar Ruta
val saverVehiculo: Saver<Vehiculo?, *> = listSaver(
    save = {
        if (it == null) emptyList() else listOf(
            it.nombre,
            it.matricula,
            it.tipo,
            it.consumo
        )
    },
    restore = {
        if (it.isEmpty())
            null
        else
            Vehiculo(
                nombre = it[0] as String,
                matricula = it[1] as String,
                tipo = it[2] as TipoVehiculo,
                consumo = it[3] as Double
            )
    }
)