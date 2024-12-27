package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres
import es.uji.smallaris.model.lugares.ServicioLugares
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TestPersistencia {

    private lateinit var repositorioFirebase: RepositorioFirebase
    private lateinit var servicioUsuarios: ServicioUsuarios
    private lateinit var servicioVehiculos: ServicioVehiculos
    private lateinit var servicioLugares: ServicioLugares
    private lateinit var servicioRutas: ServicioRutas
    private lateinit var calculadorRutas: CalculadorRutasORS
    private lateinit var servicioAPIs: ServicioAPIs

    @Before
    fun setUp() = runBlocking {
        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.registrarUsuario("al415647@uji.es", "12345678")
        repositorioFirebase.iniciarSesion("al415647@uji.es", "12345678")

        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        servicioAPIs = ServicioAPIs
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
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

                    val subcolecciones = listOf("vehículos", "lugares", "rutas")
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
    fun testPersistenciaVehiculo() = runBlocking {
        val nombreVehiculo = "VehiculoTestNuevo"
        val consumo = 12.0
        val matricula = "TEST9876"
        val tipo = TipoVehiculo.Gasolina95

        val vehiculoCreado = servicioVehiculos.addVehiculo(nombreVehiculo, consumo, matricula, tipo)

        servicioUsuarios.cerrarSesion()

        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.iniciarSesion("al415647@uji.es", "12345678")
        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        val servicioAPIs = ServicioAPIs
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        servicioRutas = ServicioRutas(CalculadorRutasORS(servicioAPIs), repositorioFirebase, servicioAPIs)

        val vehiculosRecuperados = servicioVehiculos.getVehiculos()
        assertTrue(vehiculosRecuperados.contains(vehiculoCreado))

        val vehiculoFavorito = vehiculoCreado?.let { servicioVehiculos.setVehiculoFavorito(it, true) }
        if (vehiculoFavorito != null) {
            assertTrue(vehiculoFavorito)
        }
        if (vehiculoCreado != null) {
            assertTrue(vehiculoCreado.isFavorito())
        }

        if (vehiculoCreado != null) {
            servicioVehiculos.setVehiculoFavorito(vehiculoCreado, false)
        }

        val vehiculoEliminado = vehiculoCreado?.let { servicioVehiculos.deleteVehiculo(it) }
        if (vehiculoEliminado != null) {
            assertTrue(vehiculoEliminado)
        }

        val vehiculosRecuperadosPostEliminacion = servicioVehiculos.getVehiculos()
        assertFalse(vehiculosRecuperadosPostEliminacion.contains(vehiculoCreado))
    }

    @Test
    fun testPersistenciaLugar() = runBlocking {
        val longitud = -0.12345
        val latitud = 39.98765
        val nombreLugar = "LugarTestNuevo"

        val lugarCreado = servicioLugares.addLugar(longitud, latitud, nombreLugar)

        servicioUsuarios.cerrarSesion()

        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.iniciarSesion("al415647@uji.es", "12345678")
        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        val servicioAPIs = ServicioAPIs
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        servicioRutas = ServicioRutas(CalculadorRutasORS(servicioAPIs), repositorioFirebase, servicioAPIs)

        val lugaresRecuperados = servicioLugares.getLugares()
        assertTrue(lugaresRecuperados.contains(lugarCreado))

        val lugarHechoFavorito = servicioLugares.setLugarInteresFavorito(lugarCreado, true)
        assertTrue(lugarHechoFavorito)
        assertTrue(lugarCreado.isFavorito())

        servicioLugares.setLugarInteresFavorito(lugarCreado, false)
        
        val lugarEliminado = servicioLugares.deleteLugar(lugarCreado)
        assertTrue(lugarEliminado)

        val lugaresRecuperadosPostEliminacion = servicioLugares.getLugares()
        assertFalse(lugaresRecuperadosPostEliminacion.contains(lugarCreado))
    }

    @Test
    fun testPersistenciaRuta() = runBlocking {
        val origen = LugarInteres(-0.12345, 39.98765, "OrigenTest", "MunicipioOrigen")
        val destino = LugarInteres(-0.54321, 39.56789, "DestinoTest", "MunicipioDestino")
        val vehiculo = Vehiculo("Coche", 5.0, "ABC123", TipoVehiculo.Gasolina95)
        val tipoRuta = TipoRuta.Corta

        val builderWrapper = RutaBuilderWrapper(calculadorRutas, servicioAPIs, servicioRutas)
        builderWrapper
            .setInicio(origen)
            .setFin(destino)
            .setVehiculo(vehiculo)
            .setTipo(tipoRuta)
            .setNombre("RutaTest")

        val rutaCreada = builderWrapper.build()

        servicioRutas.addRuta(rutaCreada)
        assertTrue(servicioRutas.contains(rutaCreada))

        servicioUsuarios.cerrarSesion()

        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.iniciarSesion("al415647@uji.es", "12345678")
        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        calculadorRutas = CalculadorRutasORS(servicioAPIs)
        servicioRutas = ServicioRutas(calculadorRutas, repositorioFirebase, servicioAPIs)

        val rutasRecuperadas = servicioRutas.getRutas()
        assertTrue(rutasRecuperadas.contains(rutaCreada))

        val rutaFavorita = servicioRutas.setFavorito(rutaCreada, true)
        assertTrue(rutaFavorita)
        assertTrue(rutaCreada.isFavorito())

        servicioRutas.setFavorito(rutaCreada, false)

        val rutaEliminada = servicioRutas.deleteRuta(rutaCreada)
        assertTrue(rutaEliminada)

        assertFalse(servicioRutas.contains(rutaCreada))
    }
}
