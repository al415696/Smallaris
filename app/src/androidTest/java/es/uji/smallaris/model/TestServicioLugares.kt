package es.uji.smallaris.model

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test


class TestServicioLugares {

    @Test
    fun addLugar_R2HU01_darDeAltaLugarOK() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

        // When
        val resultado = servicioLugares.addLugar(-0.0376709, 39.986)

        //Then
        assertEquals(LugarInteres(-0.0376709, 39.986, "Mercado Central, Castellón de la Plana, VC, España"), resultado)
        assertEquals(1, servicioLugares.getLugares().size)
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarYaExistente()  = runBlocking {

        var resultado: UbicationErrorException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
        servicioLugares.addLugar(-0.0376709, 39.986, "Mercado Central, Castellón de la Plana, VC, España")

        // When
        try {
            servicioLugares.addLugar(-0.0376709, 39.986)
        } catch (e: UbicationErrorException) {
            resultado = e
        }

        //Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationErrorException)
        assertEquals(1, servicioLugares.getLugares().size)
        assertEquals(servicioLugares.getLugares()[0].nombre, "Mercado Central, Castellón de la Plana, VC, España")
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarLatitudInvalida()  = runBlocking {

        var resultado: UbicationErrorException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

        // When
        try {
            servicioLugares.addLugar(-0.0376709, 95.0)
        } catch (e: UbicationErrorException) {
            resultado = e
        }

        //Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationErrorException)
        assertEquals(0, servicioLugares.getLugares().size)
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarLongitudInvalida() = runBlocking {

        var resultado: UbicationErrorException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

        // When
        try {
            servicioLugares.addLugar(-200.0, 39.986)
        } catch (e: UbicationErrorException) {
            resultado = e
        }

        //Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationErrorException)
        assertEquals(0, servicioLugares.getLugares().size)
    }

    @Test
    fun getLugares_R2HU03_obtenerListaLugares1Elemento()  = runBlocking {

        // Given
        val servicioAPIs = ServicioAPIs
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
        servicioLugares.addLugar(-0.0376709, 39.986, "Mercado Central, Castellón de la Plana, VC, España")

        // When
        val resultado = servicioLugares.getLugares()

        //Then
        assertEquals(1, resultado.size)
        assertEquals(LugarInteres(-0.0376709, 39.986, "Mercado Central, Castellón de la Plana, VC, España"), servicioLugares.getLugares()[0])
    }

    @Test
    fun getLugares_R2HU03_faltaConexionBBDD()  = runBlocking {

        var resultado: ConnectionErrorException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

        // When
        try {
            servicioLugares.getLugares()
        } catch (e: ConnectionErrorException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is ConnectionErrorException)
    }

    @Test
    fun setFavorito_R5HU03V1_AsignarLugarNoFavoritoComoFavorito() = runBlocking{
        // Given
        val servicioAPIs = ServicioAPIs
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
        servicioLugares.addLugar(-0.0376709, 39.986, "Mercado Central, Castellón de la Plana, VC, España")

        // When
        val lista = servicioLugares.getLugares()
        val cambiado = servicioLugares.setFavorito(lista[0],true)

        // Then
        assertTrue(cambiado)
        assertTrue(servicioLugares.getLugares()[0].isFavorito())
    }
    @Test
    fun setFavorito_R5HU03I1_AsignarLugarYaFavoritoComoFavorito() = runBlocking{

        // Given
        val servicioAPIs = ServicioAPIs
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
        servicioLugares.addLugar(-0.0376709, 39.986, "Mercado Central, Castellón de la Plana, VC, España")
            .let { servicioLugares.setFavorito(it) }
        // When
        val lista = servicioLugares.getLugares()
        val cambiado = servicioLugares.setFavorito(lista[0],true)

        // Then
        assertTrue(!cambiado)
        assertTrue(servicioLugares.getLugares()[0].isFavorito())
    }
    @Test
    fun getLugares_R5HU03_LugaresFavoritosPrimero()  = runBlocking {

        // Given
        val servicioAPIs = ServicioAPIs
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
        servicioLugares.addLugar(-0.0376709, 39.986, "Mercado Central, Castellón de la Plana, VC, España")
        servicioLugares.addLugar(39.8856508, -0.08128, "Pizzeria Borriana, Burriana, VC, España")
            .let { servicioLugares.setFavorito(it) }
        servicioLugares.addLugar(39.8614095, -0.18500, "Camp de Futbol, Villavieja, VC, España")



        // When
        var lista = servicioLugares.getLugares()


        // Then
        assertEquals(lista[0],LugarInteres(39.8856508, -0.08128, "Pizzeria Borriana, Burriana, VC, España") )
    }

}