package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TestServicioVehiculos {
    private lateinit var repositorioFirebase: RepositorioFirebase
    private lateinit var servicioUsuarios: ServicioUsuarios
    private lateinit var servicioVehiculos: ServicioVehiculos

    @Before
    fun setUp() = runBlocking {
        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.registrarUsuario("testVehiculo@uji.es", "12345678")
        repositorioFirebase.iniciarSesion("testVehiculo@uji.es", "12345678")

        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
    }

    @After
    fun tearDown() {
        runBlocking {
            try {
                servicioUsuarios.borrarUsuario()
            } catch (e: Exception) {
                println("Error al borrar el usuario: ${e.message}")
            }
        }
    }

    @Test
    fun addVehiculo_R3HU1V1_anyadirVehiculoListaVaciaOk() = runBlocking {
        //      GIVEN
        // Solo están los vehiculos invariantes
        //      WHEN
        val vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)

        //      THEN
        assertEquals(
            Vehiculo(
                nombre = "Coche",
                consumo = 7.1,
                matricula = "1234BBB",
                tipo = TipoVehiculo.Gasolina95
            ), vehiculo
        )
        assertEquals(true, servicioVehiculos.getVehiculos().contains(vehiculo))
        assertEquals(3, servicioVehiculos.getVehiculos().count())

    }

    @Test
    fun addVehiculo_R3HU1I1_anyadirVehiculoconMismoYaEnLista() = runBlocking {
        //      GIVEN
        servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        var resultado: Exception? = null
        //      WHEN
        try {
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)

        }
        //THEN
        catch (excepcion: Exception) {
            resultado = excepcion
        }
        assertTrue(resultado is VehicleException)
    }

    @Test
    fun getVehiculos_R3HU2V1_getListaCon1VehiculoAnyadido() = runBlocking {
//        GIVEN
        // Solo están los vehiculos invariantes
        val vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
//        WHEN
        val lista = servicioVehiculos.getVehiculos()
//        THEN
        assertEquals(3, lista.count())
        assertTrue(lista.contains(vehiculo))
    }

    @Test
    fun deleteVehiculo_R3HU3V1_eliminarVehiculoOk() = runBlocking {
        //      GIVEN
        val vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        //      WHEN
        val exito = servicioVehiculos.deleteVehiculo(vehiculo)
        //      THEN
        assertNotNull(exito)
        assertTrue(exito)
        assertTrue(servicioVehiculos.getVehiculos().count() == 2)

    }

    @Test
    fun deleteVehiculo_R3HU3I1_eliminarVehiculoInexistente() = runBlocking {
        //      GIVEN
        servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        val vehiculoInexistente = Vehiculo("Unicornio", 77.7, "7777LLL", TipoVehiculo.Bici)
        var resultado: Exception? = null
        //      WHEN
        try {
            servicioVehiculos.deleteVehiculo(vehiculoInexistente)
        } catch (e: Exception) {
            resultado = e
        }
        //      THEN
        assertTrue(resultado is VehicleException)
    }

    @Test
    fun updateVehiculos_R3HU4V1_updateUnVehiculoOk() = runBlocking {
        //        GIVEN
        val vehiculoInicial: Vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        val vehiculoEsperadoFinal =
            Vehiculo("Moto", 7.1, "1234BBB", TipoVehiculo.Electrico)

//        WHEN
        val resultado = servicioVehiculos.updateVehiculo(
            vehiculoInicial,
            nuevoNombre = "Moto",
            nuevoTipoVehiculo = TipoVehiculo.Electrico
        )
//        THEN
        assertTrue(resultado)
        assertTrue(servicioVehiculos.getVehiculos().contains(vehiculoEsperadoFinal))
    }

    @Test
    fun updateVehiculos_R3HU4I1_updateVehiculoInexistente() = runBlocking {
        //        GIVEN
        val vehiculoInicial = Vehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        //        WHEN
        val resultado = servicioVehiculos.updateVehiculo(
            vehiculoInicial,
            nuevoNombre = "Moto",
            nuevoTipoVehiculo = TipoVehiculo.Electrico
        )
        //        THEN
        assertFalse(resultado)
    }

    @Test
    fun updateVehiculos_R3HU4V2_updateVehiculoConMasEnLista() = runBlocking {
        //        GIVEN
        val vehiculoInicial: Vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        val otroVehiculo =
            servicioVehiculos.addVehiculo("Otro", 7.1, "8888BBB", TipoVehiculo.Gasolina95)
        val vehiculoEsperadoFinal =
            Vehiculo("Moto", 7.1, "1234BBB", TipoVehiculo.Electrico)

        //        WHEN
        val resultado = servicioVehiculos.updateVehiculo(
            vehiculoInicial,
            nuevoNombre = "Moto",
            nuevoTipoVehiculo = TipoVehiculo.Electrico
        )
        //        THEN
//        val vehiculoFinal = servicioVehiculos.getVehiculos()[0]
        assertTrue(resultado)
        assertTrue(servicioVehiculos.getVehiculos().contains(vehiculoEsperadoFinal))
        assertTrue(servicioVehiculos.getVehiculos().contains(otroVehiculo))
    }

    @Test
    fun updateVehiculos_R3HU4I2_updateVehiculoSolapamientoIdentificadoresNuevos() = runBlocking {
//        GIVEN
        val vehiculoInicial: Vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        servicioVehiculos.addVehiculo("Otro", 7.1, "8888BBB", TipoVehiculo.Gasolina95)
        var resultado: Exception? = null
//        WHEN
        try {
            servicioVehiculos.updateVehiculo(
                vehiculoInicial,
                nuevoNombre = "Otro",
                nuevoTipoVehiculo = TipoVehiculo.Electrico
            )
        } catch (e: Exception) {
            resultado = e
        }
//        THEN
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
    }

    @Test
    fun updateVehiculos_R3HU4I3_updateVehiculoSinCambiarNada() = runBlocking {
//        GIVEN


        val vehiculoInicial: Vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        var resultado: Exception? = null
//        WHEN
        try {
            servicioVehiculos.updateVehiculo(vehiculoInicial)
        } catch (e: Exception) {
            resultado = e
        }

//        THEN
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
    }

    @Test
    fun getVehiculos_R5HU4V2_getVehiculosOrdenadosFavoritosPrimero() = runBlocking {
        //      GIVEN
        servicioVehiculos.addVehiculo(
            nombre = "Zulom",
            consumo = 5.13,
            matricula = "3333WWW",
            tipo = TipoVehiculo.Diesel
        )
        servicioVehiculos.addVehiculo(
            nombre = "Abobamasnow",
            consumo = 1.36,
            matricula = "1234DPP",
            tipo = TipoVehiculo.Gasolina95
        )
        servicioVehiculos.addVehiculo(
            nombre = "Zyxcrieg",
            consumo = 6.66,
            matricula = "4444XXX",
            tipo = TipoVehiculo.Electrico
        )
            .let { servicioVehiculos.setVehiculoFavorito(it) }
        servicioVehiculos.addVehiculo(
            nombre = "Carrozaso",
            consumo = 15.82,
            matricula = "5675BFC",
            tipo = TipoVehiculo.Gasolina95
        )


        //      WHEN
        val lista = servicioVehiculos.getVehiculos()

        //      THEN
        assertEquals("Zyxcrieg", lista[0].nombre)

    }

    @Test
    fun getVehiculo_setFavorito_R5HU4V1_asignarVehiculoNoFavoritoComoVehiculoFavorito() =
        runBlocking {
            //      GIVEN
            val vehiculo = servicioVehiculos.addVehiculo(
                nombre = "Zyxcrieg",
                consumo = 6.66,
                matricula = "4444XXX",
                tipo = TipoVehiculo.Electrico
            )


            //      WHEN
            servicioVehiculos.getVehiculos()
            val cambiado = servicioVehiculos.setVehiculoFavorito(vehiculo)

            //      THEN
            assertTrue(cambiado)
            assertTrue(servicioVehiculos.getVehiculos()[0].isFavorito())
        }

    @Test
    fun getVehiculo_setFavorito_R5HU4I1_asignarVehiculoYaFavoritoComoVehiculoFavorito() =
        runBlocking {
            //      GIVEN
            val vehiculo: Vehiculo
            servicioVehiculos.addVehiculo(
                nombre = "Zyxcrieg",
                consumo = 6.66,
                matricula = "4444XXX",
                tipo = TipoVehiculo.Electrico
            )
                .let {
                    vehiculo = it
                    servicioVehiculos.setVehiculoFavorito(it)
                }
            servicioVehiculos.getVehiculo(nombre = "Zyxcrieg", matricula = "4444XXX")
                ?.setFavorito(true)


            //      WHEN
            servicioVehiculos.getVehiculos()
            val cambiado = servicioVehiculos.setVehiculoFavorito(vehiculo)

            //      THEN
            assertFalse(cambiado)
            assertTrue(servicioVehiculos.getVehiculos()[0].isFavorito())
        }
}