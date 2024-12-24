package es.uji.smallaris

import android.util.Log
import es.uji.smallaris.model.API
import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.RepositorioFirebase
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.RepositorioLugares
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.lugares.ServicioLugares
import es.uji.smallaris.model.ServicioORS
import es.uji.smallaris.model.lugares.UbicationException
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class TestServicioLugares {
    companion object {

        private var mockServicioORS = mockk<ServicioORS>(relaxed = true)
        private var mockRepositorioLugares = mockk<RepositorioLugares>(relaxed = true)
        private val servicioAPIs = ServicioAPIs

        @JvmStatic
        @BeforeClass
        fun setupGlobal(): Unit {
            mockServicioORS = mockk<ServicioORS>(relaxed = true)
            mockRepositorioLugares = mockk<RepositorioLugares>(relaxed = true)
            servicioAPIs.setServicioMapa(mockServicioORS)

            coEvery { mockRepositorioLugares.enFuncionamiento() } returns true
            coEvery { mockRepositorioLugares.addLugar(any()) } returns true
            coEvery { mockServicioORS.getToponimoCercano(-0.0376709, 39.986) } returns
                    "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
            coEvery { mockServicioORS.getToponimoCercano(39.8856508, -0.08128) } returns
                    "Pizzeria Borriana, Burriana, Comunidad Valenciana, España"
            coEvery { mockServicioORS.getToponimoCercano(39.8614095, -0.18500) } returns
                    "Camp de Futbol, Villavieja, Comunidad Valenciana, España"
            coEvery { mockRepositorioLugares.setLugarInteresFavorito(any(), any()) } returns true
            coEvery { mockServicioORS.getCoordenadas("Topónimo_inexistente") } throws UbicationException("No se encontraron coordenadas para el topónimo Topónimo_inexistente")
            coEvery { mockServicioORS.getCoordenadas("Castellón de la Plana") } returns Pair(-0.037787, 39.987142)
            coEvery { mockServicioORS.getToponimoCercano(-0.037787, 39.987142) } returns
                    "Buzón de Correos, Castellón de la Plana, Comunidad Valenciana, España"
        }
    }

    @Before
    fun setup() {
        clearMocks(mockRepositorioLugares, mockServicioORS, recordedCalls = true, answers = false)
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarOK() = runBlocking {
        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)

        // When
        val resultado = servicioLugares.addLugar(-0.0376709, 39.986)

        //Then
        assertEquals(
            LugarInteres(
                -0.0376709,
                39.986,
                "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            ), resultado
        )
        assertEquals(1, servicioLugares.getLugares().size)
        coVerify { mockServicioORS.getToponimoCercano(any(), any()) }
        coVerify { mockRepositorioLugares.enFuncionamiento() }
        coVerify { mockRepositorioLugares.addLugar(any()) }
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarYaExistente() = runBlocking {

        var resultado: UbicationException? = null

        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)
        servicioLugares.addLugar(
            -0.0376709,
            39.986,
            "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        )

        // When
        try {
            servicioLugares.addLugar(-0.0376709, 39.986)
        } catch (e: UbicationException) {
            resultado = e
        }

        //Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationException)
        assertEquals(1, servicioLugares.getLugares().size)
        assertEquals(
            servicioLugares.getLugares()[0].nombre,
            "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        )
        coVerify { mockRepositorioLugares.enFuncionamiento() }
        coVerify(exactly = 1) { mockServicioORS.getToponimoCercano(any(), any()) }
        coVerify(exactly = 1) { mockRepositorioLugares.addLugar(any()) }
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarLatitudInvalida() = runBlocking {

        var resultado: UbicationException? = null

        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)

        // When
        try {
            servicioLugares.addLugar(-0.0376709, 95.0)
        } catch (e: UbicationException) {
            resultado = e
        }

        //Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationException)
        assertEquals(0, servicioLugares.getLugares().size)
        // Al crear el servicio, se comprueba si FireBase funciona
        // Al obtene los lugares, se comprueba si FireBase funciona
        coVerify { mockRepositorioLugares.enFuncionamiento() }
        coVerify(exactly = 0) { mockServicioORS.getToponimoCercano(any(), any()) }
        coVerify(exactly = 0) { mockRepositorioLugares.addLugar(any()) }
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarLongitudInvalida() = runBlocking {

        var resultado: UbicationException? = null

        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)

        // When
        try {
            servicioLugares.addLugar(-200.0, 39.986)
        } catch (e: UbicationException) {
            resultado = e
        }

        //Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationException)
        assertEquals(0, servicioLugares.getLugares().size)
        // Al crear el servicio, se comprueba si FireBase funciona
        // Al obtene los lugares, se comprueba si FireBase funciona
        coVerify { mockRepositorioLugares.enFuncionamiento() }
        coVerify(exactly = 0) { mockServicioORS.getToponimoCercano(any(), any()) }
        coVerify(exactly = 0) { mockRepositorioLugares.addLugar(any()) }
    }

    @Test//(timeout = 2000)
    fun getLugares_R2HU03_obtenerListaLugares1Elemento() = runBlocking {

        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)
        servicioLugares.addLugar(
            -0.0376709,
            39.986,
            "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        )

        // When
        val resultado = servicioLugares.getLugares()

        //Then
        assertEquals(1, resultado.size)
        assertEquals(
            LugarInteres(
                -0.0376709,
                39.986,
                "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            ), servicioLugares.getLugares()[0]
        )
        coVerify { mockRepositorioLugares.addLugar(any()) }
        coVerify { mockServicioORS.getToponimoCercano(any(), any()) }
    }

    @Test
    fun getLugares_R2HU03_faltaConexionBBDD() = runBlocking {

        var resultado: ConnectionErrorException? = null

        coEvery { mockRepositorioLugares.enFuncionamiento() } returns false

        // Given
        val servicioAPIs = ServicioAPIs
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)

        // When
        try {
            servicioLugares.getLugares()
        } catch (e: ConnectionErrorException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is ConnectionErrorException)
        coVerify { mockRepositorioLugares.enFuncionamiento() }
    }

    @Test
    fun setFavorito_R5HU03V1_AsignarLugarNoFavoritoComoFavorito() = runBlocking {
        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)
        servicioLugares.addLugar(
            -0.0376709,
            39.986,
            "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        )

        // When
        val lista = servicioLugares.getLugares()
        val cambiado = servicioLugares.setFavorito(lista[0], true)

        // Then
        assertTrue(cambiado)
        assertTrue(servicioLugares.getLugares()[0].isFavorito())
        coVerify { mockRepositorioLugares.setLugarInteresFavorito(any(), any()) }
    }

    @Test
    fun setFavorito_R5HU03I1_AsignarLugarYaFavoritoComoFavorito() = runBlocking {

        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)
        servicioLugares.addLugar(
            -0.0376709,
            39.986,
            "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        ).let { servicioLugares.setFavorito(it) }
        // When
        val lista = servicioLugares.getLugares()
        val cambiado = servicioLugares.setFavorito(lista[0], true)

        // Then
        assertTrue(!cambiado)
        assertTrue(servicioLugares.getLugares()[0].isFavorito())
        coVerify(exactly = 1) { mockRepositorioLugares.setLugarInteresFavorito(any(), any()) }
    }

    @Test
    fun getLugares_R5HU03_LugaresFavoritosPrimero() = runBlocking {

        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)
        servicioLugares.addLugar(
            -0.0376709,
            39.986,
            "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        )
        servicioLugares.addLugar(
            39.8856508, -0.08128, "Pizzeria Borriana, Burriana, Comunidad Valenciana, España"
        ).let { servicioLugares.setFavorito(it) }
        servicioLugares.addLugar(
            39.8614095, -0.18500, "Camp de Futbol, Villavieja, Comunidad Valenciana, España"
        )

        // When
        val lista = servicioLugares.getLugares()

        // Then
        assertEquals(
            LugarInteres(
                39.8856508,
                -0.08128,
                "Pizzeria Borriana, Burriana, Comunidad Valenciana, España",
                "Burriana"
            ), lista[0]
        )

    }

    @Test
    fun addLugar_R2HU02_darDeAltaLugarPorToponimoOK() = runBlocking {
        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)

        // When
        val (longitud, latitud) = servicioAPIs.getCoordenadas("Castellón de la Plana")
        val resultado = servicioLugares.addLugar(longitud, latitud)

        //Then
        assertEquals(longitud, -0.037787)
        assertEquals(latitud, 39.987142)
        assertEquals("Castellón de la Plana", resultado.municipio)
        assertEquals(1, servicioLugares.getLugares().size)
    }

    @Test
    fun addLugar_R2HU02_darDeAltaLugarPorToponimoInexistente() = runBlocking {

        var excepcion: UbicationException? = null

        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)

        // When
        try {
            val (longitud, latitud) = servicioAPIs.getCoordenadas("Topónimo_inexistente")
            servicioLugares.addLugar(longitud, latitud)
        } catch (e: UbicationException) {
            excepcion = e
        }

        //Then
        assertNotNull(excepcion)
        assertTrue(excepcion is UbicationException)
        assertEquals(0, servicioLugares.getLugares().size)
        coVerify { mockRepositorioLugares.enFuncionamiento() }
        coVerify(exactly = 1) { mockServicioORS.getCoordenadas(any()) }
        coVerify(exactly = 0) { mockRepositorioLugares.addLugar(any()) }
    }

    @Test
    fun deleteLugar_R2HU04_eliminarLugarOK() = runBlocking {

        coEvery { mockRepositorioLugares.deleteLugar(any()) } returns true

        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)
        val lugar = servicioLugares.addLugar(-0.0376709, 39.986)

        // When
        val resultado = servicioLugares.deleteLugar(lugar)

        //Then
        assertEquals(true, resultado)
        assertEquals(0, servicioLugares.getLugares().size)
        coVerify { mockRepositorioLugares.deleteLugar(any()) }
    }

    @Test
    fun deleteLugar_R2HU04_eliminarLugarFavorito() = runBlocking {

        var excepcion: UbicationException? = null
        coEvery { mockRepositorioLugares.deleteLugar(any()) } returns false

        // Given
        val servicioLugares = ServicioLugares(mockRepositorioLugares, servicioAPIs)
        val lugar = servicioLugares.addLugar(-0.0376709, 39.986)
        servicioLugares.setFavorito(lugar, true)

        // When
        try {
            val resultado = servicioLugares.deleteLugar(lugar)

        } catch (e: UbicationException) {
            excepcion = e
        }

        //Then
        assertNotNull(excepcion)
        assertTrue(excepcion is UbicationException)
        assertTrue(excepcion!!.message.equals("Ubicación favorita"))
        assertEquals(1, servicioLugares.getLugares().size)
        coVerify(exactly = 0) { mockRepositorioLugares.deleteLugar(any()) }
    }
}