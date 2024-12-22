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
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith

@RunWith(Enclosed::class)
class TestServicioUsuarios {

    class PruebasSinAfterPropioTest {
        private lateinit var repositorioUsuarios: RepositorioUsuarios
        private lateinit var servicioUsuarios: ServicioUsuarios

        @After
        fun tearDown(): Unit {
            runBlocking {
                val auth = FirebaseAuth.getInstance()

                // Cierra sesión si hay un usuario activo
                auth.currentUser?.let {
                    auth.signOut()
                }
                Thread.sleep(2000)
            }
        }

        @Test
        fun registrarUsuario_R1HU01_registrarUsuarioYaExistente() {
            runBlocking {
                var resultado: UserAlreadyExistsException? = null
                // Dado
                repositorioUsuarios = RepositorioFirebase()
                servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
                // El usuario que tiene "al415647@uji.es" por correo está siempre registrado en la base de datos de Firebase

                // Cuando
                try {
                    servicioUsuarios.registrarUsuario("al415647@uji.es", "12345678")
                } catch (excepcion: UserAlreadyExistsException) {
                    resultado = excepcion
                }

                // Entonces
                assertNotNull(resultado)
                assertTrue(resultado is UserAlreadyExistsException)
            }
        }

        @Test
        fun iniciarSesion_R1HU02_iniciarSesionExito() {
            runBlocking {
                // Dado
                repositorioUsuarios = RepositorioFirebase()
                servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
                // El usuario que tiene "al415647@uji.es" por correo está siempre registrado en la base de datos de Firebase

                // Cuando
                val usuario = servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")

                // Entonces
                assertEquals(Usuario(correo = "al415647@uji.es"), usuario)
                assertNotNull(servicioUsuarios.obtenerUsuarioActual())
            }
        }

        @Test
        fun iniciarSesion_R1HU02_iniciarSesionSinRegistrarse() = runBlocking {
            var resultado: UnregisteredUserException? = null

            // Dado
            repositorioUsuarios = RepositorioFirebase()
            servicioUsuarios = ServicioUsuarios(repositorioUsuarios)

            // Cuando
            try {
                servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
            } catch (excepcion: UnregisteredUserException) {
                resultado = excepcion
            }

            // Entonces
            assertNotNull(resultado)
            assertTrue(resultado is UnregisteredUserException)
            assertNull(servicioUsuarios.obtenerUsuarioActual())
        }

        @Test
        fun cerrarSesion_R1HU03_cerrarSesionExito() {
            runBlocking {
                // Dado
                repositorioUsuarios = RepositorioFirebase()
                servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
                // El usuario que tiene "al415647@uji.es" por correo está siempre registrado en la base de datos de Firebase
                servicioUsuarios.iniciarSesion("al415647@uji.es", "12345678")

                // Cuando
                val resultado = servicioUsuarios.cerrarSesion()

                // Entonces
                assertTrue("Devuelve true solo en caso de cerrar sesión con éxito.", resultado)
                assertNull(servicioUsuarios.obtenerUsuarioActual())
            }
        }

        @Test
        fun cerrarSesion_R1HU03_cerrarSesionSinIniciarSesion() {
            runBlocking {
                var resultado: UnloggedUserException? = null
                // Dado
                val repositorioUsuarios = RepositorioFirebase()
                val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
                // Cuando
                try {
                    servicioUsuarios.cerrarSesion() // Intentar cerrar sesión sin usuario
                } catch (excepcion: UnloggedUserException) {
                    resultado = excepcion
                }
                // Entonces
                assertNotNull(resultado)
                assertTrue(resultado is UnloggedUserException)
                assertNull(servicioUsuarios.obtenerUsuarioActual())
            }
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
                assertNotNull(servicioUsuarios.obtenerUsuarioActual())
            }
        }

        @After
        fun tearDown() {
            runBlocking {
                val auth = FirebaseAuth.getInstance()
                val firestore = FirebaseFirestore.getInstance()

                // Elimina el usuario creado y su documento en la colección usuarios-test
                auth.currentUser?.let { user ->
                    try {
                        firestore.collection("usuarios-test").document(user.uid).delete()
                            .await()
                        user.delete().await()
                    } catch (ex: Exception) {
                        // Manejo de excepciones si es necesario
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
        fun borrarUsuario_R1HU04_borrarUsuarioExito() = runBlocking{
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