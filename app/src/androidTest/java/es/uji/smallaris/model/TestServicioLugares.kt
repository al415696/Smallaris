package es.uji.smallaris.model

import android.util.Log
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
        assertEquals(
            LugarInteres(
                -0.0376709,
                39.986,
                "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            ), resultado
        )
        assertEquals(1, servicioLugares.getLugares().size)
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarYaExistente() = runBlocking {

        var resultado: UbicationException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
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
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarLatitudInvalida() = runBlocking {

        var resultado: UbicationException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

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
    }

    @Test
    fun addLugar_R2HU01_darDeAltaLugarLongitudInvalida() = runBlocking {

        var resultado: UbicationException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

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
    }

    @Test
    fun getLugares_R2HU03_obtenerListaLugares1Elemento() = runBlocking {

        // Given
        val servicioAPIs = ServicioAPIs
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
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
    }

    @Test
    fun setFavorito_R5HU03V1_AsignarLugarNoFavoritoComoFavorito() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
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
    }

    @Test
    fun setFavorito_R5HU03I1_AsignarLugarYaFavoritoComoFavorito() = runBlocking {

        // Given
        val servicioAPIs = ServicioAPIs
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
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
    }

    @Test
    fun getLugares_R5HU03_LugaresFavoritosPrimero() = runBlocking {

        // Given
        val servicioAPIs = ServicioAPIs
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
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
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COORDS))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

        // When
        val (longitud, latitud) = servicioAPIs.getCoordenadas("Castellón de la Plana")
        val resultado = servicioLugares.addLugar(longitud, latitud)
        Log.i("Información", "$longitud, $latitud")

        //Then
        assertEquals(longitud, resultado.longitud)
        assertEquals(latitud, resultado.latitud)
        assertEquals("Castellón de la Plana", resultado.municipio)
        assertEquals(1, servicioLugares.getLugares().size)
    }

    @Test
    fun addLugar_R2HU02_darDeAltaLugarPorToponimoInexistente() = runBlocking {

        var excepcion: UbicationException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COORDS))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)

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
    }

    @Test
    fun deleteLugar_R2HU04_eliminarLugarOK() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
        val lugar = servicioLugares.addLugar(-0.0376709, 39.986)

        // When
        val resultado = servicioLugares.deleteLugar(lugar)

        //Then
        assertEquals(true, resultado)
        assertEquals(0, servicioLugares.getLugares().size)
    }

    @Test
    fun deleteLugar_R2HU04_eliminarLugarFavorito() = runBlocking {

        var excepcion: UbicationException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))
        val repositorioLugares: RepositorioLugares = RepositorioFirebase()
        val servicioLugares = ServicioLugares(repositorioLugares, servicioAPIs)
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
    }


}