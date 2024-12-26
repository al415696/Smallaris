package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.ServicioLugares
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TestPersistencia {

    private lateinit var repositorioFirebase: RepositorioFirebase
    private lateinit var servicioUsuarios: ServicioUsuarios
    private lateinit var servicioVehiculos: ServicioVehiculos
    private lateinit var servicioLugares: ServicioLugares
    private lateinit var servicioRutas: ServicioRutas

    @Before
    fun setUp() = runBlocking {
        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.registrarUsuario("al415647@uji.es", "12345678")
        repositorioFirebase.iniciarSesion("al415647@uji.es", "12345678")

        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        val servicioAPIs = ServicioAPIs
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        servicioRutas = ServicioRutas(CalculadorRutasORS(servicioAPIs), repositorioFirebase, servicioAPIs)
    }

    @After
    fun tearDown() {
        runBlocking {
            val auth = repositorioFirebase.obtenerAuth()
            val firestore = repositorioFirebase.obtenerFirestore()

            auth.currentUser?.let { user ->
                try {
                    val usuarioDocRef = firestore.collection("usuarios").document(user.uid)

                    val subcolecciones = listOf("vehículos", "lugares")
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
                    // Manejo de excepciones si es necesario
                    println("Error al eliminar el usuario o sus subcolecciones: ${ex.message}")
                } finally {
                    auth.signOut()
                }
            }
        }
    }

    @Test
    fun testPersistenciaVehiculo() = runBlocking {
        // Dado
        val nombreVehiculo = "VehiculoTestNuevo"
        val consumo = 12.0
        val matricula = "TEST9876"
        val tipo = TipoVehiculo.Gasolina95

        // Cuando
        val vehiculoCreado = servicioVehiculos.addVehiculo(nombreVehiculo, consumo, matricula, tipo)
        servicioUsuarios.cerrarSesion()

        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.iniciarSesion("al415647@uji.es", "12345678")
        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        val servicioAPIs = ServicioAPIs
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        servicioRutas = ServicioRutas(CalculadorRutasORS(servicioAPIs), repositorioFirebase, servicioAPIs)

        // Entonces
        val vehiculosRecuperados = servicioVehiculos.getVehiculos()
        assertTrue(vehiculosRecuperados.contains(vehiculoCreado))
    }

    @Test
    fun testPersistenciaLugar() = runBlocking {
        // Dado
        val longitud = -0.12345
        val latitud = 39.98765
        val nombreLugar = "LugarTestNuevo"

        // Cuando
        val lugarCreado = servicioLugares.addLugar(longitud, latitud, nombreLugar)
        servicioUsuarios.cerrarSesion()

        repositorioFirebase = RepositorioFirebase()
        repositorioFirebase.iniciarSesion("al415647@uji.es", "12345678")
        servicioUsuarios = ServicioUsuarios(repositorioFirebase)
        servicioVehiculos = ServicioVehiculos(repositorioFirebase)
        val servicioAPIs = ServicioAPIs
        servicioLugares = ServicioLugares(repositorioFirebase, servicioAPIs)
        servicioRutas = ServicioRutas(CalculadorRutasORS(servicioAPIs), repositorioFirebase, servicioAPIs)

        // Entonces
        val lugaresRecuperados = servicioLugares.getLugares()
        assertTrue(lugaresRecuperados.contains(lugarCreado))
    }
}
