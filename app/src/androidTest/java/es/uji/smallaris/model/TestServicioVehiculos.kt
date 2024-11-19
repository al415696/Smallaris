package es.uji.smallaris.model

import org.junit.Assert.assertEquals
import org.junit.Test

class TestServicioVehiculos {
    @Test
    fun addVehiculo_R3HU1V1(){
//      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos()

        servicioVehiculos.setRepositorio(repositorioVehiculos)
//      WHEN
        var vehiculo = servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)

//      THEN
        assertEquals(vehiculo, Vehiculo(nombre="Coche", consumo = 7.1, matricula ="1234BBB", tipo = TipoVehiculo.Gasolina ))
        assertEquals(true, repositorioVehiculos.getVehiculos().contains(vehiculo))
        assertEquals(1, repositorioVehiculos.getVehiculos().count())

    }
    @Test
    fun addVehiculo_R3HU1I1(){
//      GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos()
        servicioVehiculos.setRepositorio(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)


//      WHEN
        try {
            servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)

        }
//                  THEN
        catch (excepcion: Exception){

            assertEquals(VehicleAlredyExistsException::class.java, excepcion ::class.java)
        }
    }

    @Test
    fun addVehiculo_R3HU2V1(){
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos()
        servicioVehiculos.setRepositorio(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)
//        WHEN
        var lista = servicioVehiculos.getVehiculos()
//        THEN
        assertEquals(1, lista.count())
        assertEquals(Vehiculo(nombre="Coche", consumo = 7.1, matricula ="1234BBB", tipo = TipoVehiculo.Gasolina ), lista[0])
    }

    @Test
    fun addVehiculo_R3HU2I1(){
//        GIVEN
        var repositorioVehiculos : RepositorioVehiculos = RepositorioFirebase()
        var servicioVehiculos : ServicioVehiculos = ServicioVehiculos()
        servicioVehiculos.setRepositorio(repositorioVehiculos)
        servicioVehiculos.addVehiculo("Coche",7.1,"1234BBB" ,TipoVehiculo.Gasolina)
//        WHEN
        try {
            servicioVehiculos.getVehiculos()
        }
        //        THEN
        catch (exception: Exception){
            assertEquals(ConnectionErrorException::class.java, exception ::class.java)

        }
    }

}