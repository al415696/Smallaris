package es.uji.smallaris.model.integration

import es.uji.smallaris.model.ConnectionErrorException
import es.uji.smallaris.model.LugarInteres
import es.uji.smallaris.model.RepositorioFirebase
import es.uji.smallaris.model.RepositorioLugares
import es.uji.smallaris.model.ServicioAPIs
import es.uji.smallaris.model.ServicioLugares
import es.uji.smallaris.model.ServicioORS
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class TestServicioLugares {

    private var mockServicioORS = mockk<ServicioORS>(relaxed = true)
    private var mockRepositorioLugares = mockk<RepositorioLugares>(relaxed = true)
    private val servicioAPIs = ServicioAPIs

    @Before
    fun setup() {
        mockServicioORS = mockk<ServicioORS>(relaxed = true)
        mockRepositorioLugares = mockk<RepositorioLugares>(relaxed = true)
        servicioAPIs.setServicioMapa(mockServicioORS)
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarOK_mockObtenerToponimo(): Unit = runBlocking {

        every { mockServicioORS.getToponimoCercano(-0.0376709, 39.986) } returns
                "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        // Given
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

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
        verify { mockServicioORS.getToponimoCercano(-0.0376709, 39.986) }
    }

    @Test
    fun getLugares_R2HU03_faltaConexionBBDD_mockRepositorioLugares() = runBlocking {

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
    fun addLugar_R2HU02_darDeAltaLugarPorToponimoOK_mockObtenerCoordenadas() = runBlocking {

        every { mockServicioORS.getCoordenadas("Castellón de la Plana") } returns Pair(-0.0376709, 39.986)
        every { mockServicioORS.getToponimoCercano(-0.0376709, 39.986) } returns
                "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"

        // Given
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

        // When
        val (longitud, latitud) = servicioAPIs.getCoordenadas("Castellón de la Plana")
        val resultado = servicioLugares.addLugar(longitud, latitud)

        //Then
        assertEquals(-0.0376709, resultado.longitud)
        assertEquals(39.986, resultado.latitud)
        assertEquals("Castellón de la Plana", resultado.municipio)
        assertEquals(1, servicioLugares.getLugares().size)
        verify { mockServicioORS.getCoordenadas("Castellón de la Plana") }
        verify { mockServicioORS.getToponimoCercano(-0.0376709, 39.986) }
    }
}