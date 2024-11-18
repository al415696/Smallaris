package es.uji.smallaris.model

import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test


class ServicioLugaresTest {

    @Test
    fun addLugar_R2HU01_darDeAltaLugarOK() {
        // Given
        val servicioAPIs = ServicioAPIs()
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = repositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, mutableListOf(), servicioAPIs)

        // When
        val resultado = servicioLugares.addLugar(-0.0376709F, 39.986F)

        //Then
        assertEquals(LugarInteres(-0.0376709F, 39.986F, "Castellón de la Plana"), resultado)
        assertEquals(1, servicioLugares.getLugares().size)
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarYaExistente() {

        var resultado: UbicationErrorException? = null

        // Given
        val servicioAPIs = ServicioAPIs()
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = repositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, mutableListOf(), servicioAPIs)
        servicioLugares.addLugar(-0.0376709F, 39.986F, "Castellón de la Plana")

        // When
        try {
            servicioLugares.addLugar(-0.0376709F, 39.986F)
        } catch (e: UbicationErrorException) {
            resultado = e
        }

        //Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationErrorException)
        assertEquals(1, servicioLugares.getLugares().size)
        assertEquals(servicioLugares.getLugares()[0].nombre, "Castellón de la Plana")
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarLatitudInvalida() {

        var resultado: UbicationErrorException? = null

        // Given
        val servicioAPIs = ServicioAPIs()
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = repositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, mutableListOf(), servicioAPIs)

        // When
        try {
            servicioLugares.addLugar(-0.0376709F, 95.0F)
        } catch (e: UbicationErrorException) {
            resultado = e
        }

        //Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationErrorException)
        assertEquals(0, servicioLugares.getLugares().size)
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarLongitudInvalida() {

        var resultado: UbicationErrorException? = null

        // Given
        val servicioAPIs = ServicioAPIs()
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = repositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, mutableListOf(), servicioAPIs)

        // When
        try {
            servicioLugares.addLugar(-200.0F, 39.986F)
        } catch (e: UbicationErrorException) {
            resultado = e
        }

        //Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationErrorException)
        assertEquals(0, servicioLugares.getLugares().size)
    }

}