package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TestServicioUsuarios {

    @Test
    fun registrarUsuario_R1HU01_registrarUsuarioExito() {
        runBlocking {
            // Dado
            val repositorioUsuarios: RepositorioUsuarios = RepositorioFirebase()
            val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
            // Cuando
            val usuario = servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            // Entonces
            assertEquals(Usuario(uid = "", correo = "al415617@uji.es"), usuario)

            // Eliminar el usuario para poder registrar el mismo usuario de nuevo
            val auth = repositorioUsuarios.obtenerAuth()
            auth.currentUser?.delete()?.await()
        }
    }

    @Test
    fun registrarUsuario_R1HU01_registrarUsuarioYaExistente() {
        runBlocking {
            var resultado: UserAlreadyExistsException? = null
            // Dado
            val repositorioUsuarios: RepositorioUsuarios = RepositorioFirebase()
            val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
            servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            // Cuando
            try {
                servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            }
            catch (excepcion: UserAlreadyExistsException){
                resultado = excepcion
            }
            // Entonces
            assertNotNull(resultado)
            assertTrue(resultado is UserAlreadyExistsException)

            // Eliminar el usuario para poder registrar el mismo usuario de nuevo
            val auth = repositorioUsuarios.obtenerAuth()
            auth.currentUser?.delete()?.await()
        }
    }

    @Test
    fun iniciarSesion_R1HU02_iniciarSesionExito() {
        runBlocking {
            // Dado
            val repositorioUsuarios: RepositorioUsuarios = RepositorioFirebase()
            val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
            servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            // Cuando
            val usuario = servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
            // Entonces
            assertEquals(Usuario(uid = "", correo = "al415617@uji.es"), usuario)

            // Eliminar el usuario para poder registrar el mismo usuario de nuevo
            val auth = repositorioUsuarios.obtenerAuth()
            auth.currentUser?.delete()?.await()
        }
    }

    @Test
    fun iniciarSesion_R1HU02_iniciarSesionSinRegistrarse() = runBlocking {
        var resultado: UnregisteredUserException? = null
        // Dado
        val repositorioUsuarios: RepositorioUsuarios = RepositorioFirebase()
        val servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
        // Cuando
        try {
            servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
        }
        catch (excepcion: UnregisteredUserException){
            resultado = excepcion
        }
        // Entonces
        assertNotNull(resultado)
        assertTrue(resultado is UnregisteredUserException)
    }
}