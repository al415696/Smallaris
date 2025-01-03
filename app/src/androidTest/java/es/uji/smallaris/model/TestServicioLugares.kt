package es.uji.smallaris.model

import android.util.Log
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.lugares.ServicioLugares
import es.uji.smallaris.model.lugares.UbicationException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class TestServicioLugares {
    private lateinit var repositorioFirebase: RepositorioFirebase
    private lateinit var servicioUsuarios: ServicioUsuarios
    private lateinit var servicioLugares: ServicioLugares
    private lateinit var calculadorRutas: CalculadorRutasORS
    private lateinit var servicioAPIs: ServicioAPIs

    @Before
    fun setUp() = runBlocking {
        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.registrarUsuario("testLugar@uji.es", "12345678")
        repositorioFirebase.iniciarSesion("testLugar@uji.es", "12345678")

        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioAPIs = ServicioAPIs
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        calculadorRutas = CalculadorRutasORS(servicioAPIs)
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
    fun addLugar_R2HU01_darDeAltaLugarOK() = runBlocking {
        // Given
        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))

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

        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))


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

        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))


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

        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))


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
    fun setFavorito_R5HU03V1_AsignarLugarNoFavoritoComoLugarInteresFavorito() = runBlocking {
        // Given


        servicioLugares.addLugar(
            -0.0376709,
            39.986,
            "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        )

        // When
        val lista = servicioLugares.getLugares()
        val cambiado = servicioLugares.setLugarInteresFavorito(lista[0], true)

        // Then
        assertTrue(cambiado)
        assertTrue(servicioLugares.getLugares()[0].isFavorito())
    }

    @Test
    fun setFavorito_R5HU03I1_AsignarLugarYaFavoritoComoLugarInteresFavorito() = runBlocking {

        // Given


        servicioLugares.addLugar(
            -0.0376709,
            39.986,
            "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        ).let { servicioLugares.setLugarInteresFavorito(it) }
        // When
        val lista = servicioLugares.getLugares()
        val cambiado = servicioLugares.setLugarInteresFavorito(lista[0], true)

        // Then
        assertTrue(!cambiado)
        assertTrue(servicioLugares.getLugares()[0].isFavorito())
    }

    @Test
    fun getLugares_R5HU03_LugaresFavoritosPrimero() = runBlocking {

        // Given
        servicioLugares.addLugar(
            -0.0376709,
            39.986,
            "Mercado Central, Castellón de la Plana, Comunidad Valenciana, España"
        )
        servicioLugares.addLugar(
            39.8856508, -0.08128, "Pizzeria Borriana, Burriana, Comunidad Valenciana, España"
        ).let { servicioLugares.setLugarInteresFavorito(it) }
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

        assert(servicioAPIs.apiEnFuncionamiento(API.COORDS))


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

        assert(servicioAPIs.apiEnFuncionamiento(API.COORDS))


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

        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))


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

        assert(servicioAPIs.apiEnFuncionamiento(API.TOPONIMO))


        val lugar = servicioLugares.addLugar(-0.0376709, 39.986)
        servicioLugares.setLugarInteresFavorito(lugar, true)

        // When
        try {
            servicioLugares.deleteLugar(lugar)

        } catch (e: UbicationException) {
            excepcion = e
        }

        //Then
        assertNotNull(excepcion)
        assertTrue(excepcion is UbicationException)
        assertTrue(excepcion!!.message.equals("Ubicación favorita no se puede borrar"))
        assertEquals(1, servicioLugares.getLugares().size)
    }


}