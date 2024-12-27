package es.uji.smallaris.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
class TestServicioUsuarios {

    class PruebasSinAfterPropioTest {
        private lateinit var repositorioUsuarios: RepositorioUsuarios
        private lateinit var servicioUsuarios: ServicioUsuarios

        @Before
        fun setUp() {
            repositorioUsuarios = RepositorioFirebase()
            servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
            runBlocking {
                try {
                    servicioUsuarios.registrarUsuario("al415647@uji.es", "12345678")
                    servicioUsuarios.cerrarSesion()
                } catch (e: UserAlreadyExistsException) {
                    // Si el usuario ya existe, no hacemos nada, solo continuamos
                    println("El usuario ya existe: ${e.message}")
                }
            }
        }

        @After
        fun tearDown() {
            runBlocking {
                val auth = FirebaseAuth.getInstance()
                val firestore = FirebaseFirestore.getInstance()
                repositorioUsuarios = RepositorioFirebase()
                servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
                servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")
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
        fun registrarUsuario_R1HU01_registrarUsuarioYaExistente() {
            runBlocking {
                var resultado: UserAlreadyExistsException? = null

                // Dado LO QUE SE REALIZA EN setUp()

                // Cuando intentamos registrar un usuario con un correo ya existente
                try {
                    servicioUsuarios.registrarUsuario("al415647@uji.es", "12345678")
                } catch (excepcion: UserAlreadyExistsException) {
                    resultado = excepcion
                }

                // Entonces verificamos que se lance la excepción correcta
                assertNotNull(resultado)
                assertTrue(resultado is UserAlreadyExistsException)
            }
        }

        @Test
        fun iniciarSesion_R1HU02_iniciarSesionExito() {
            runBlocking {
                // Dado LO QUE SE REALIZA EN setUp()

                // Cuando
                val usuario = servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")

                // Entonces
                assertEquals(Usuario(correo = "al415647@uji.es"), usuario)
            }
        }

        @Test
        fun iniciarSesion_R1HU02_iniciarSesionSinRegistrarse() = runBlocking {
            var resultado: UnregisteredUserException? = null

            // Dado LO QUE SE REALIZA EN setUp()

            // Cuando
            try {
                servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
            } catch (excepcion: UnregisteredUserException) {
                resultado = excepcion
            }

            // Entonces
            assertNotNull(resultado)
            assertTrue(resultado is UnregisteredUserException)
        }

        @Test
        fun cerrarSesion_R1HU03_cerrarSesionExito() = runBlocking {
            // Dado LO QUE SE REALIZA EN setUp() y:
            servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")

            // Cuando
            val usuario = servicioUsuarios.cerrarSesion()

            // Entonces
            assertEquals("al415647@uji.es", usuario.correo)
            assertNull(servicioUsuarios.obtenerUsuarioActual())
        }

        @Test
        fun cerrarSesion_R1HU03_cerrarSesionSinIniciarSesion() = runBlocking {
            var resultado: UnloggedUserException? = null
            // Dado
            val repositorioUsuarios = RepositorioFirebase()
            val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)

            // Cuando
            try {
                servicioUsuarios.cerrarSesion()
            } catch (excepcion: UnloggedUserException) {
                resultado = excepcion
            }

            // Entonces
            assertNotNull(resultado)
            assertTrue(resultado is UnloggedUserException)
        }
    }

    class RegistrarUsuarioExitoTest {
        private lateinit var repositorioUsuarios: RepositorioUsuarios
        private lateinit var servicioUsuarios: ServicioUsuarios

        @Test
        fun registrarUsuario_R1HU01_registrarUsuarioExito() {
            runBlocking {
                // Dado
                repositorioUsuarios = RepositorioFirebase()
                servicioUsuarios = ServicioUsuarios(repositorioUsuarios)

                // Cuando
                val usuario =
                    servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")

                // Entonces
                assertEquals(Usuario(correo = "al415617@uji.es"), usuario)
            }
        }

        @After
        fun tearDown() {
            runBlocking {
                val auth = FirebaseAuth.getInstance()
                val firestore = FirebaseFirestore.getInstance()

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
    }

    class BorrarUsuarioExitoTest {
        private lateinit var repositorioUsuarios: RepositorioUsuarios
        private lateinit var servicioUsuarios: ServicioUsuarios

        @Test
        fun borrarUsuario_R1HU04_borrarUsuarioExito() = runBlocking {
            // Dado
            repositorioUsuarios = RepositorioFirebase()
            servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
            servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")

            // Cuando
            val usuario = servicioUsuarios.borrarUsuario()

            // Entonces
            assertEquals(Usuario(correo = "al415617@uji.es"), usuario)
            assertNull(servicioUsuarios.obtenerUsuarioActual())
        }

    }
}