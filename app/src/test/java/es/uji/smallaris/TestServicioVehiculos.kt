package es.uji.smallaris

import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.RepositorioVehiculos
import es.uji.smallaris.model.ServicioRutas
import es.uji.smallaris.model.ServicioVehiculos
import es.uji.smallaris.model.TipoVehiculo
import es.uji.smallaris.model.VehicleException
import es.uji.smallaris.model.Vehiculo
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test

class TestServicioVehiculos {

    companion object {

        private var mockRepositorioVehiculos = mockk<RepositorioVehiculos>(relaxed = true)
        private var mockServicioRutas = mockk<ServicioRutas>(relaxed = true)

        @JvmStatic
        @BeforeClass
        fun setupGlobal() {
            mockRepositorioVehiculos = mockk<RepositorioVehiculos>(relaxed = true)
            coEvery { mockRepositorioVehiculos.enFuncionamiento() } returns true
            coEvery { mockRepositorioVehiculos.addVehiculos(any()) } returns true
            coEvery { mockRepositorioVehiculos.removeVehiculo(any()) } returns true
            coEvery { mockRepositorioVehiculos.updateVehiculos(any(), any()) } returns true
            coEvery { mockRepositorioVehiculos.setVehiculoFavorito(any(), any()) } returns true
            coEvery { mockServicioRutas.contains(ofType(Vehiculo::class)) } returns emptyList()
        }
    }

    @After
    fun setup() {
        clearMocks(mockRepositorioVehiculos, recordedCalls = true, answers = false)
        coEvery { mockRepositorioVehiculos.enFuncionamiento() } returns true
    }

    @Test
    fun addVehiculo_R3HU1V1_anyadirVehiculoListaVaciaOk(): Unit = runBlocking {
        //      GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)

        //      WHEN
        val vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)

        //      THEN
        assertEquals(
            vehiculo,
            Vehiculo(
                nombre = "Coche",
                consumo = 7.1,
                matricula = "1234BBB",
                tipo = TipoVehiculo.Gasolina95
            )
        )
        assertEquals(true, servicioVehiculos.getVehiculos().contains(vehiculo))
        assertEquals(1, servicioVehiculos.getVehiculos().count())
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify { mockRepositorioVehiculos.addVehiculos(any()) }
    }

    @Test
    fun addVehiculo_R3HU1I1_anyadirVehiculoconMismoYaEnLista() = runBlocking {
        //      GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        var resultado: Exception? = null

        println(servicioVehiculos.getVehiculos())

        //      WHEN
        try {
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        }

        //      THEN
        catch (excepcion: Exception) {
            resultado = excepcion
        }
        assertTrue(resultado is VehicleException)
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify(exactly = 1) { mockRepositorioVehiculos.addVehiculos(any()) }
    }

    @Test
    fun getVehiculos_R3HU2V1_getListaCon1VehiculoAnyadido() = runBlocking {

        //        GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
        // Solo están los vehiculos invariantes
        val vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)

        //        WHEN
        val lista = servicioVehiculos.getVehiculos()

        //        THEN
        // Es solo 1 por qué no se ha utilizado ningún user en repositorioFirebase
        assertEquals(1, lista.size)
        assertTrue(lista.contains(vehiculo))
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
    }

    @Test
    fun getVehiculos_R3HU2I1_getListaErrorConexion() = runBlocking {

        var resultado: Exception? = null

        //        GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
        coEvery { mockRepositorioVehiculos.enFuncionamiento() } returns false

        //        WHEN
        try {
            servicioVehiculos.getVehiculos()
        } catch (exception: Exception) {
            resultado = exception
        }

        //        THEN
        assertNotNull(resultado)
        assertTrue(resultado is ConnectionErrorException)
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
    }

    @Test
    fun deleteVehiculo_R3HU3V1_eliminarVehiculoOk() = runBlocking {

        //      GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
        val vehiculo =
            servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)

        //      WHEN
        val exito = servicioVehiculos.deleteVehiculo(vehiculo, mockServicioRutas)

        //      THEN
        assertNotNull(exito)
        assertTrue(exito)
        assertTrue(servicioVehiculos.getVehiculos().isEmpty())
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify { mockRepositorioVehiculos.removeVehiculo(any()) }
    }

    @Test
    fun deleteVehiculo_R3HU3I1_eliminarVehiculoInexistente() = runBlocking {

        //      GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)
        val vehiculoInexistente = Vehiculo("Unicornio", 77.7, "7777LLL", TipoVehiculo.Bici)
        var resultado: Exception? = null

        //      WHEN
        try {
            servicioVehiculos.deleteVehiculo(vehiculoInexistente, mockServicioRutas)
        } catch (e: Exception) {
            resultado = e
        }

        //      THEN
        assertTrue(resultado is VehicleException)
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify(exactly = 0) { mockRepositorioVehiculos.removeVehiculo(any()) }
    }

    @Test
    fun updateVehiculos_R3HU4V1_updateUnVehiculoOk() = runBlocking {

        //        GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
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
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify { mockRepositorioVehiculos.updateVehiculos(any(), any()) }
    }

    @Test
    fun updateVehiculos_R3HU4I1_updateVehiculoInexistente() = runBlocking {

        //        GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
        val vehiculoInicial = Vehiculo("Coche", 7.1, "1234BBB", TipoVehiculo.Gasolina95)

        //        WHEN
        val resultado = servicioVehiculos.updateVehiculo(
            vehiculoInicial,
            nuevoNombre = "Moto",
            nuevoTipoVehiculo = TipoVehiculo.Electrico
        )

        //        THEN
        assertFalse(resultado)
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify(exactly = 0) { mockRepositorioVehiculos.updateVehiculos(any(), any()) }
    }

    @Test
    fun updateVehiculos_R3HU4V2_updateVehiculoConMasEnLista() = runBlocking {

        //        GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
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
        assertTrue(resultado)
        assertTrue(servicioVehiculos.getVehiculos().contains(vehiculoEsperadoFinal))
        assertTrue(servicioVehiculos.getVehiculos().contains(otroVehiculo))
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify { mockRepositorioVehiculos.updateVehiculos(any(), any()) }
    }

    @Test
    fun updateVehiculos_R3HU4I2_updateVehiculoSolapamientoIdentificadoresNuevos() = runBlocking {
        //        GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
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
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify(exactly = 0) { mockRepositorioVehiculos.updateVehiculos(any(), any()) }
    }

    @Test
    fun updateVehiculos_R3HU4I3_updateVehiculoSinCambiarNada() = runBlocking {
        //        GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
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
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify(exactly = 0) { mockRepositorioVehiculos.updateVehiculos(any(), any()) }
    }

    @Test
    fun getVehiculos_R5HU4V2_getVehiculosOrdenadosFavoritosPrimero() = runBlocking{
        //      GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
        servicioVehiculos.addVehiculo(nombre= "Zulom",consumo=5.13, matricula = "3333WWW" ,tipo=TipoVehiculo.Diesel)
        servicioVehiculos.addVehiculo(nombre= "Abobamasnow",consumo=1.36, matricula = "1234DPP" ,tipo=TipoVehiculo.Gasolina95)
        servicioVehiculos.addVehiculo(nombre= "Zyxcrieg",consumo=6.66, matricula = "4444XXX" ,tipo=TipoVehiculo.Electrico)
            .let { servicioVehiculos.setVehiculoFavorito(it) }
        servicioVehiculos.addVehiculo(nombre= "Carrozaso",consumo=15.82, matricula = "5675BFC" ,tipo=TipoVehiculo.Gasolina95)

        //      WHEN
        val lista = servicioVehiculos.getVehiculos()

        //      THEN
        assertEquals("Zyxcrieg", lista[0].nombre)
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify { mockRepositorioVehiculos.getVehiculos() }
    }

    @Test
    fun getVehiculo_setFavorito_R5HU4V1_asignarVehiculoNoFavoritoComoVehiculoFavorito() = runBlocking{
        //      GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
        val vehiculo= servicioVehiculos.addVehiculo(nombre= "Zyxcrieg",consumo=6.66, matricula = "4444XXX" ,tipo=TipoVehiculo.Electrico)

        //      WHEN
        val cambiado = servicioVehiculos.setVehiculoFavorito(vehiculo)

        //      THEN
        assertTrue(cambiado)
        assertTrue(servicioVehiculos.getVehiculos()[0].isFavorito())
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify { mockRepositorioVehiculos.setVehiculoFavorito(any(), any()) }
    }

    @Test
    fun getVehiculo_setFavorito_R5HU4I1_asignarVehiculoYaFavoritoComoVehiculoFavorito() = runBlocking{
        //      GIVEN
        val servicioVehiculos = ServicioVehiculos(mockRepositorioVehiculos)
        val vehiculo: Vehiculo
        servicioVehiculos.addVehiculo(nombre= "Zyxcrieg",consumo=6.66, matricula = "4444XXX" ,tipo=TipoVehiculo.Electrico)
            .let {vehiculo= it
                servicioVehiculos.setVehiculoFavorito(it) }
        servicioVehiculos.getVehiculo(nombre = "Zyxcrieg", matricula = "4444XXX" )?.setFavorito(true)

        //      WHEN
        val cambiado = servicioVehiculos.setVehiculoFavorito(vehiculo)

        //      THEN
        assertFalse(cambiado)
        assertTrue(servicioVehiculos.getVehiculos()[0].isFavorito())
        coVerify { mockRepositorioVehiculos.enFuncionamiento() }
        coVerify(exactly = 1) { mockRepositorioVehiculos.setVehiculoFavorito(any(), any()) }
    }
}