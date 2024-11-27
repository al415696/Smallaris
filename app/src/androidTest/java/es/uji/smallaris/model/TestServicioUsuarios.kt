package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TestServicioUsuarios {

    private lateinit var repositorioUsuarios: RepositorioUsuarios
    private lateinit var servicioUsuarios: ServicioUsuarios

    @After
    fun tearDown() {
        runBlocking {
            val auth = repositorioUsuarios.obtenerAuth()
            val firestore = repositorioUsuarios.obtenerFirestore()

            // Guardar UID antes de eliminar al usuario
            val usuarioId = auth.currentUser?.uid

            // Eliminar usuario de Firebase Authentication
            try {
                auth.currentUser?.delete()?.await()
                println("Usuario autenticado eliminado correctamente.")
            } catch (e: Exception) {
                println("Error al eliminar usuario autenticado: ${e.localizedMessage}")
            }

            // Eliminar documentos relacionados en Firestore usando el UID
            if (usuarioId != null) {
                try {
                    firestore.collection("usuarios").document(usuarioId).delete().await()
                    println("Documento del usuario eliminado de Firestore.")
                } catch (e: Exception) {
                    println("Error al eliminar documento de Firestore: ${e.localizedMessage}")
                }
            } else {
                println("No se pudo obtener el UID del usuario para borrar datos en Firestore.")
            }
        }
    }



    @Test
    fun registrarUsuario_R1HU01_registrarUsuarioExito() {
        runBlocking {
            // Dado
            repositorioUsuarios = RepositorioFirebase()
            servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
            // Cuando
            val usuario = servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            // Entonces
            assertEquals(Usuario(uid = "", correo = "al415617@uji.es"), usuario)
        }
    }

    @Test
    fun registrarUsuario_R1HU01_registrarUsuarioYaExistente() {
        runBlocking {
            var resultado: UserAlreadyExistsException? = null
            // Dado
            repositorioUsuarios = RepositorioFirebase()
            servicioUsuarios = ServicioUsuarios(repositorioUsuarios)
            servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            println("Primer usuario registrado")
            // Cuando
            try {
                servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
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
            servicioUsuarios.registrarUsuario("al415617@uji.es", "alHugo415617")
            // Cuando
            val usuario = servicioUsuarios.iniciarSesion("al415617@uji.es", "alHugo415617")
            // Entonces
            assertEquals(Usuario(uid = "", correo = "al415617@uji.es"), usuario)
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
    }
}