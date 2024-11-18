package es.uji.smallaris.model

class Vehiculo {
    lateinit var nombre : String
    var consumo : Double = 0.0
    lateinit var matricula : String
    lateinit var tipo : TipoVehiculo

    constructor(nombre: String, consumo: Double, matricula: String, tipo: TipoVehiculo) {
        this.nombre = nombre
        this.consumo = consumo
        this.matricula = matricula
        this.tipo = tipo
    }

}