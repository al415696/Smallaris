package es.uji.smallaris.model

class ServicioVehiculos(private val repositorio: RepositorioVehiculos) {

    private val vehiculos = mutableListOf<Vehiculo>()

    init {
        this.vehiculos.addAll(repositorio.getVehiculos())
    }


    fun addVehiculo (nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo): Vehiculo? {
        var vehiculo: Vehiculo
        //        Checks de validez de datos tienen que estar aquí no en las clases que use
        if (checkValidezVehiculo(nombre, consumo, matricula, tipo)){
            vehiculo = Vehiculo(nombre = nombre, consumo = consumo, matricula = matricula, tipo = tipo)
            if (!checkUnicidadVehiculo(nombre, matricula))
                throw VehicleAlredyExistsException("vehiculo ya existe")
            //        Se ejecuta el método add del repositorio

            if (repositorio.addVehiculos(vehiculo)){
                vehiculos.add(vehiculo)
                return vehiculo
            }
        }
        return null
    }
    private fun checkValidezVehiculo(nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo): Boolean{
        return nombre.isNotEmpty() && matricula.isNotEmpty() && consumo >=0
    }
    private fun checkUnicidadVehiculo(nombre: String,  matricula: String): Boolean{
        for (otro in vehiculos){
            if (nombre == otro.nombre){
                return false
            }
            if (matricula == otro.matricula){
                return false
            }
        }
        return true
    }

    fun getVehiculos(): List<Vehiculo>{
        return vehiculos
    }


}