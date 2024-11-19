package es.uji.smallaris.model

class ServicioVehiculos(private val repositorio: RepositorioVehiculos) {

    private val vehiculos = mutableListOf<Vehiculo>()

    init {
        this.vehiculos.addAll(repositorio.getVehiculos())
    }


    fun addVehiculo (nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo): Vehiculo? {


//        Checks de validez de datos tienen que estar aquí no en las clases que use
        var vehiculo = Vehiculo(nombre = nombre, consumo = consumo, matricula = matricula, tipo = tipo)
//        se ejecuta el método add del repositorio
        repositorio.addVehiculos(vehiculo)
        return null
    }

    fun getVehiculos(): List<Vehiculo>{
        return listOf()
    }


}