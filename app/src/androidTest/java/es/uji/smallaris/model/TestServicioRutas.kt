package es.uji.smallaris.model

import android.util.Log
import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.lugares.ServicioLugares
import es.uji.smallaris.model.lugares.UbicationException
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TestServicioRutas {

//    private var repositorioFirebase: RepositorioRutas = RepositorioFirebase()
//    private val servicioAPIs = ServicioAPIs

//    @Before
//    fun setUp() {
//        repositorioFirebase = RepositorioFirebase()
//        assert(servicioAPIs.apiEnFuncionamiento(API.RUTA))
//    }

    private lateinit var repositorioFirebase: RepositorioFirebase
    private lateinit var servicioUsuarios: ServicioUsuarios
    private lateinit var servicioRutas: ServicioRutas
    private lateinit var calculadorRutas: CalculadorRutasORS
    private lateinit var servicioAPIs: ServicioAPIs

    @Before
    fun setUp() = runBlocking {
        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.registrarUsuario("testRuta@uji.es", "12345678")
        repositorioFirebase.iniciarSesion("testRuta@uji.es", "12345678")

        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioAPIs = ServicioAPIs
        calculadorRutas = CalculadorRutasORS(servicioAPIs)
        servicioRutas = ServicioRutas(calculadorRutas, repositorioFirebase, servicioAPIs)
    }

    @After
    fun tearDown() {
        runBlocking {
            val auth = repositorioFirebase.obtenerAuth()
            val firestore = repositorioFirebase.obtenerFirestore()

            auth.currentUser?.let { user ->
                try {
                    val usuarioDocRef = firestore.collection("usuarios").document(user.uid)

                    val subcolecciones = listOf("rutas")
                    for (subcoleccion in subcolecciones) {
                        val subcoleccionRef = usuarioDocRef.collection(subcoleccion)
                        val documentos = subcoleccionRef.get().await()

                        for (documento in documentos) {
                            subcoleccionRef.document(documento.id).delete().await()
                        }
                    }

                    usuarioDocRef.delete().await()

                    user.delete().await()

                } catch (ex: Exception) {
                    println("Error al eliminar el usuario o sus subcolecciones: ${ex.message}")
                } finally {
                    auth.signOut()
                }
            }
        }
    }

    @Test
    fun addRuta_R4HU01_R4HU04_R4HU05_calcularYGuardarRutaCortaOK() = runBlocking {
        // Given
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).build()
        servicioRutas.addRuta(ruta)

        // Then
        assert(ruta.getDistancia() > 0)
        assert(ruta.getDuracion() > 0)
        assert(servicioRutas.getRutas().size == 1)
    }

    @Test
    fun addRuta_R4HU05_GuardarRutaCortaYaGuardada() = runBlocking {

        var resultado: RouteException? = null

        // Given
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).build()
        servicioRutas.addRuta(ruta)

        // Then
        assert(ruta.getDistancia() > 0)
        assert(ruta.getDuracion() > 0)
        assert(servicioRutas.getRutas().size == 1)

        // When
        try {
            servicioRutas.addRuta(ruta)
        } catch (e: RouteException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is RouteException)
    }

    @Test
    fun builder_R4HU04_calcularRutaEconomicaOK() = runBlocking {
        // Given
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Economica).build()

        // Then
        assert(ruta.getDistancia() > 0)
        assert(ruta.getDuracion() > 0)
    }

    @Test
    fun builder_R4HU04_calcularRutaRapidaOK() = runBlocking {
        // Given
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Rapida).build()

        // Then
        assert(ruta.getDistancia() > 0)
        assert(ruta.getDuracion() > 0)
    }

    @Test
    fun builder_R4HU01_trayectoFaltaVehiculo() = runBlocking {

        var resultado: VehicleException? = null

        // Given
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        

        // When
        try {
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino)
                .setTipo(TipoRuta.Corta).build()
        } catch (e: VehicleException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
    }

    @Test
    fun builder_R4HU04_calcularRutaCortaFaltaDestino() = runBlocking {

        var resultado: UbicationException? = null

        // Given
        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        

        // When
        try {
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setVehiculo(coche)
                .setTipo(TipoRuta.Corta).build()
        } catch (e: UbicationException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is UbicationException)
    }

    @Test
    fun builder_R4HU02_costeCocheCorrecto() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val coche = Vehiculo("Coche", 7.0, "234", TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(coche)
            .setTipo(TipoRuta.Corta).build()

        // Then
        assert(ruta.getCoste() > 0)
    }

    @Test
    fun builder_R4HU02_costeFaltaVehiculo() = runBlocking {

        var resultado: VehicleException? = null

        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        

        // When
        try {
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino)
                .setTipo(TipoRuta.Corta).build()
        } catch (e: VehicleException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
    }

    @Test
    fun builder_R4HU3_costePieCorrecto() = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val pie = Vehiculo("Pie", matricula = "Pie", tipo = TipoVehiculo.Pie)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(pie)
            .setTipo(TipoRuta.Corta).build()

        // Then
        assert(ruta.getCoste() > 0)
    }

    @Test
    fun builder_R4HU3_costeBiciCorrecto(): Unit = runBlocking {
        // Given
        val servicioAPIs = ServicioAPIs
        assert(servicioAPIs.apiEnFuncionamiento(API.COSTE))

        val pie = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        

        // When
        val ruta = servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
            .setFin(destino).setVehiculo(pie)
            .setTipo(TipoRuta.Corta).build()

        // Then
        assert(ruta.getCoste() > 0)
        Log.d("Coste", "Resultado del test: ${ruta.getCoste()}")
    }

    @Test
    fun builder_R4HU3_rutaPieExcepcion() = runBlocking {

        var resultado: RouteException? = null

        // Given
        val pie = Vehiculo("Pie", matricula = "Pie", tipo = TipoVehiculo.Pie)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino =
            LugarInteres(39.34651, -0.35293, "Albufera de Valencia, Valencia, España", "Valencia")
        

        // When
        try {
            servicioRutas.builder().setNombre("Ruta hacia Albufera").setInicio(origen)
                .setFin(destino).setVehiculo(pie)
                .setTipo(TipoRuta.Corta).build()
        } catch (e: RouteException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is RouteException)
    }

    @Test
    fun builder_R4HU3_rutaBiciExcepcion() = runBlocking {

        var resultado: RouteException? = null

        // Given
        val bici = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino =
            LugarInteres(39.34651, -0.35293, "Albufera de Valencia, Valencia, España", "Valencia")
        

        // When
        try {
            servicioRutas.builder().setNombre("Ruta hacia Albufera").setInicio(origen)
                .setFin(destino).setVehiculo(bici)
                .setTipo(TipoRuta.Corta).build()
        } catch (e: RouteException) {
            resultado = e
        }

        // Then
        assertNotNull(resultado)
        assertTrue(resultado is RouteException)
    }

    @Test
    fun getLugares_R4HU6_listaRutasCorrecto(): Unit = runBlocking {
        // Given
        val pie = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        
        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(pie)
                .setTipo(TipoRuta.Corta).build()
        )

        // When
        val listaRutas = servicioRutas.getRutas()

        // Then
        assert(listaRutas.size == 1)
    }

    @Test
    fun getLugares_R5HU5_listaRutasFavoritoPrimero(): Unit = runBlocking {
        // Given
        val pie = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino1 = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )

        val destino2 = LugarInteres(
            0.024997,
            39.994958,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        
        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón1").setInicio(origen)
                .setFin(destino1).setVehiculo(pie)
                .setTipo(TipoRuta.Corta).build()
        )
        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón2").setInicio(origen)
                .setFin(destino2).setVehiculo(pie)
                .setTipo(TipoRuta.Corta).build()
        ).let {
            servicioRutas.setFavorito(it, true)
        }

        // When
        val listaRutas = servicioRutas.getRutas()

        // Then
        assert(listaRutas[0].getNombre() == "Ruta por Castellón2")
    }

    @Test
    fun getLugares_setFavoritos_R5HU5V1_asignarRutaNoFavoritaComoFavorita(): Unit = runBlocking {
        // Given
        val pie = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        
        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón2").setInicio(origen)
                .setFin(destino).setVehiculo(pie)
                .setTipo(TipoRuta.Corta).build()
        )


        // When
        val listaRutas = servicioRutas.getRutas()
        val cambiado = servicioRutas.setFavorito(listaRutas[0], true)

        // Then
        assertTrue(listaRutas[0].isFavorito())
        assertTrue(cambiado)
    }

    @Test
    fun getLugares_setFavoritos_R5HU5I1_asignarRutaFavoritaComoFavorita(): Unit = runBlocking {
        // Given
        val pie = Vehiculo("Bici", matricula = "Bici", tipo = TipoVehiculo.Bici)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        
        servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón2").setInicio(origen)
                .setFin(destino).setVehiculo(pie)
                .setTipo(TipoRuta.Corta).build()
        ).let {
            servicioRutas.setFavorito(it, true)
        }

        // When
        val listaRutas = servicioRutas.getRutas()
        val cambiado = servicioRutas.setFavorito(listaRutas[0], true)

        // Then
        assertTrue(listaRutas[0].isFavorito())
        assertFalse(cambiado)
    }

    @Test
    fun deleteRuta_R4HU07_eliminarRutaOK() = runBlocking {
        // Given
        val coche = Vehiculo("Coche", 7.0, matricula = "6319BKN", tipo = TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        
        val ruta = servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(coche)
                .setTipo(TipoRuta.Corta).build()
        )

        // When
        val resultado = servicioRutas.deleteRuta(ruta)

        //Then
        assertEquals(true, resultado)
        assertEquals(0, servicioRutas.getRutas().size)
    }

    @Test
    fun deleteRuta_R4HU07_eliminarRutaFavorita() = runBlocking {

        var excepcion: RouteException? = null

        // Given
        val coche = Vehiculo("Coche", 7.0, matricula = "6319BKN", tipo = TipoVehiculo.Gasolina95)
        val origen =
            LugarInteres(
                -0.067893,
                39.991907,
                "Talleres, Castellón de la Plana, Comunidad Valenciana, España",
                "Castellón de la Plana"
            )
        val destino = LugarInteres(
            0.013474,
            39.971408,
            "Cámara de tráfico 10, Grao, Comunidad Valenciana, España",
            "Castellón de la Plana"
        )
        
        val ruta = servicioRutas.addRuta(
            servicioRutas.builder().setNombre("Ruta por Castellón").setInicio(origen)
                .setFin(destino).setVehiculo(coche)
                .setTipo(TipoRuta.Corta).build()
        )
        servicioRutas.setFavorito(ruta, true)

        // When
        try {
            servicioRutas.deleteRuta(ruta)
        } catch (e: RouteException) {
            excepcion = e
        }

        //Then
        assertNotNull(excepcion)
        assertTrue(excepcion is RouteException)
        assertTrue(excepcion!!.message.equals("Ruta favorita no se puede borrar"))
        assertEquals(1, servicioRutas.getRutas().size)
    }
}