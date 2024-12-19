package es.uji.smallaris.model

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TestServicioVehiculos {
    @Test
    fun addVehiculo_R3HU1V1_anyadirVehiculoListaVaciaOk() = runBlocking{
        //      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)

        //      WHEN
        var vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)

        //      THEN
        assertEquals(vehiculo, Vehiculo(nombre="Coche", consumo = 7.1, matricula ="1234BBB", tipo = TipoVehiculo.Gasolina95 ))
        assertEquals(true, servicioVehiculos.getVehiculos().contains(vehiculo))
        assertEquals(1, servicioVehiculos.getVehiculos().count())

    }
    @Test
    fun addVehiculo_R3HU1I1_anyadirVehiculoconMismoYaEnLista() = runBlocking{
        //      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)
        var resultado : Exception? = null


        //      WHEN
        try {
            servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)

        }
        //THEN
        catch (excepcion: Exception){
            resultado = excepcion
        }
        assertTrue(resultado is VehicleException)
    }

    @Test
    fun getVehiculos_R3HU2V1_getListaCon1Vehiculo() = runBlocking{
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)
//        WHEN
        var lista = servicioVehiculos.getVehiculos()
//        THEN
        assertEquals(1, lista.count())
        assertEquals(Vehiculo(nombre="Coche", consumo = 7.1, matricula ="1234BBB", tipo = TipoVehiculo.Gasolina95 ), lista[0])
    }

    @Test
    fun getVehiculos_R3HU2I1_getListaErrorConexion() = runBlocking{
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)
        var resultado : Exception? = null
//        WHEN
        try {
            servicioVehiculos.getVehiculos()
        }
        catch (exception: Exception){
            resultado = exception
        }
//        THEN
        assertNotNull(resultado)
        assertTrue(resultado is ConnectionErrorException)

    }

    @Test
    fun deleteVehiculo_R3HU3V1_eliminarVehiculoOk() = runBlocking{
        //      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        var vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)
        //      WHEN
        val exito = vehiculo?.let {  servicioVehiculos.deleteVehiculo(it) }
        //      THEN
        assertNotNull(exito)
        if (exito != null)
            assertTrue(exito)
        assertTrue(servicioVehiculos.getVehiculos().isEmpty())

    }
    @Test
    fun deleteVehiculo_R3HU3I1_eliminarVehiculoInexistente() = runBlocking{
        //      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        var vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)
        var vehiculoInexistente = Vehiculo("Unicornio", 77.7, "7777LLL", TipoVehiculo.Bici)
        var resultado: Exception? = null
        //      WHEN
        try {
        servicioVehiculos.deleteVehiculo(vehiculoInexistente)
    } catch (e: Exception) {
        resultado = e
    }
        //      THEN
        assertTrue(resultado is VehicleException)
    }

    @Test
    fun updateVehiculos_R3HU4V1_updateUnVehiculoOk() = runBlocking{
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        var vehiculoInicial: Vehiculo =
            servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)!!
//        WHEN
        var resultado = servicioVehiculos.updateVehiculo(vehiculoInicial, nuevoNombre = "Moto", nuevoTipoVehiculo = TipoVehiculo.Electrico)
//        THEN
        val vehiculoFinal = servicioVehiculos.getVehiculos()[0]
        println(resultado)
        assertTrue(resultado)
        assertEquals("Moto", vehiculoFinal.nombre)
        assertTrue(7.1 == vehiculoFinal.consumo)
        assertEquals("1234BBB", vehiculoFinal.matricula)
        assertEquals(TipoVehiculo.Electrico, vehiculoFinal.tipo)
    }

    @Test
    fun updateVehiculos_R3HU4I1_updateVehiculoInexistente() = runBlocking{
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        var vehiculoInicial: Vehiculo =Vehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)
//        WHEN
        var resultado = servicioVehiculos.updateVehiculo(vehiculoInicial, nuevoNombre = "Moto", nuevoTipoVehiculo = TipoVehiculo.Electrico)
//        THEN
        assertFalse(resultado)
    }
    @Test
    fun updateVehiculos_R3HU4V2_updateVehiculoConMasEnLista() = runBlocking{
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        var vehiculoInicial: Vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)!!
        servicioVehiculos.addVehiculo("Otro",7.1,"8888BBB" ,TipoVehiculo.Gasolina95)
//        WHEN
        var resultado = servicioVehiculos.updateVehiculo(vehiculoInicial, nuevoNombre = "Moto", nuevoTipoVehiculo = TipoVehiculo.Electrico)
//        THEN
        val vehiculoFinal = servicioVehiculos.getVehiculos()[0]
        println(resultado)
        assertTrue(resultado)
        assertEquals("Moto", vehiculoFinal.nombre)
        assertTrue(7.1 == vehiculoFinal.consumo)
        assertEquals("1234BBB", vehiculoFinal.matricula)
        assertEquals(TipoVehiculo.Electrico, vehiculoFinal.tipo)
    }
    @Test
    fun updateVehiculos_R3HU4I2_updateVehiculoSolapamientoIdentificadoresNuevos() = runBlocking{
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        var vehiculoInicial: Vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)!!
        servicioVehiculos.addVehiculo("Otro",7.1,"8888BBB" ,TipoVehiculo.Gasolina95)
        var resultado: Exception? = null
//        WHEN
        try {
            servicioVehiculos.updateVehiculo(vehiculoInicial, nuevoNombre = "Otro", nuevoTipoVehiculo = TipoVehiculo.Electrico)
        } catch (e: Exception) {
            resultado = e
        }
//        THEN
        assertNotNull(resultado)
        assertTrue(resultado is VehicleException)
    }
    @Test
    fun updateVehiculos_R3HU4I3_updateVehiculoSinCambiarNada() = runBlocking{
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        var vehiculoInicial: Vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina95)!!
        var resultado: Boolean = false
//        WHEN
            resultado = servicioVehiculos.updateVehiculo(vehiculoInicial)

//        THEN
        assertFalse(resultado)
    }

    @Test
    fun getVehiculos_R5HU4V2_getVehiculosOrdenadosFavoritosPrimero() = runBlocking{
        //      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo(nombre= "Zulom",consumo=5.13, matricula = "3333WWW" ,tipo=TipoVehiculo.Diesel)
        servicioVehiculos.addVehiculo(nombre= "Abobamasnow",consumo=1.36, matricula = "1234DPP" ,tipo=TipoVehiculo.Gasolina95)
        servicioVehiculos.addVehiculo(nombre= "Zyxcrieg",consumo=6.66, matricula = "4444XXX" ,tipo=TipoVehiculo.Electrico)
            ?.let { servicioVehiculos.setFavorito(it) }
        servicioVehiculos.addVehiculo(nombre= "Carrozaso",consumo=15.82, matricula = "5675BFC" ,tipo=TipoVehiculo.Gasolina95)


        //      WHEN
        var lista = servicioVehiculos.getVehiculos()

        //      THEN
        assertEquals("Zyxcrieg", lista[0].nombre)
        assertEquals("Abobamasnow", lista[1].nombre)
        assertEquals("Zulom", lista[lista.size-1].nombre)

    }
    @Test
    fun getVehiculo_setFavorito_R5HU4V1_asignarVehiculoNoFavoritoComoFavorito() = runBlocking{
        //      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo(nombre= "Zyxcrieg",consumo=6.66, matricula = "4444XXX" ,tipo=TipoVehiculo.Electrico)


        //      WHEN
        val lista = servicioVehiculos.getVehiculos()
        val cambiado = servicioVehiculos.setFavorito(lista[0])

        //      THEN
        assertTrue(cambiado)
        assertTrue(servicioVehiculos.getVehiculos()[0].isFavorito())
    }
    @Test
    fun getVehiculo_setFavorito_R5HU4I1_asignarVehiculoYaFavoritoComoFavorito() = runBlocking{
        //      GIVEN
        val repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        val servicioVehiculos : ServicioVehiculos = ServicioVehiculos(repositorioVehiculos)
        servicioVehiculos.addVehiculo(nombre= "Zyxcrieg",consumo=6.66, matricula = "4444XXX" ,tipo=TipoVehiculo.Electrico)
            ?.let { servicioVehiculos.setFavorito(it) }
        servicioVehiculos.getVehiculo(nombre = "Zyxcrieg", matricula = "4444XXX" )?.setFavorito(true)


        //      WHEN
        val lista = servicioVehiculos.getVehiculos()
        val cambiado = servicioVehiculos.setFavorito(lista[0])

        //      THEN
        assertFalse(cambiado)
        assertTrue(servicioVehiculos.getVehiculos()[0].isFavorito())
    }
}