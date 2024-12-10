package es.uji.smallaris.model

import com.mapbox.geojson.LineString

class RutaBuilder: IBuilderRutas {
    private var inicio: LugarInteres = LugarInteres(0.0, 0.0, "", "")
    private var fin: LugarInteres = LugarInteres(0.0, 0.0, "", "")
    private var vehiculo: Vehiculo = Vehiculo("Desconocido", 0.0, "", TipoVehiculo.Desconocido)
    private var tipo: TipoRuta = TipoRuta.Desconocida
    private var trayecto: LineString = LineString.fromLngLats(listOf())
    private var distancia: Float = 0.0f
    private var duracion: Float = 0.0f
    private var coste: Double = 0.0
    private var nombre: String = ""

    override fun setInicio(inicio: LugarInteres): IBuilderRutas = apply { this.inicio = inicio }

    override fun setFin(fin: LugarInteres): IBuilderRutas = apply { this.fin = fin }

    override fun setVehiculo(vehiculo: Vehiculo): IBuilderRutas = apply { this.vehiculo = vehiculo }

    override fun setTipo(tipo: TipoRuta): IBuilderRutas = apply { this.tipo = tipo }

    override fun setTrayecto(trayecto: LineString): IBuilderRutas = apply { this.trayecto = trayecto }

    override fun setDistancia(distancia: Float): IBuilderRutas = apply { this.distancia = distancia }

    override fun setDuracion(duracion: Float): IBuilderRutas = apply { this.duracion = duracion }

    override fun setCoste(coste: Double): IBuilderRutas = apply { this.coste = coste }

    override fun setNombre(nombre: String): IBuilderRutas = apply { this.nombre = nombre }

    fun getInicio(): LugarInteres {
        return inicio
    }

    fun getFin(): LugarInteres {
        return fin
    }

    fun getVehiculo(): Vehiculo {
        return vehiculo
    }

    fun getTipo(): TipoRuta {
        return tipo
    }

    override fun reset() {
        // Restablecer todos los atributos a sus valores iniciales
        inicio = LugarInteres(0.0, 0.0, "", "")
        fin = LugarInteres(0.0, 0.0, "", "")
        vehiculo = Vehiculo("Desconocido", 0.0, "", TipoVehiculo.Desconocido)
        tipo = TipoRuta.Desconocida
        trayecto = LineString.fromLngLats(listOf())
        distancia = 0.0f
        duracion = 0.0f
        coste = 0.0
        nombre = ""
    }

    @Throws(IllegalArgumentException::class)
    override fun getRuta(): Ruta {
        // Validaciones
        if (nombre == "") {
            throw IllegalArgumentException("El nombre no puede estar vacío")
        }
        if (inicio.nombre == "" || fin.nombre == "") {
            throw IllegalArgumentException("El origen y el destino no pueden estar vacíos")
        }
        if (vehiculo.tipo == TipoVehiculo.Desconocido) {
            throw VehicleException("Debes configurar un vehiculo")
        }
        if (distancia <= 0 || duracion <= 0 || coste < 0) {
            throw IllegalArgumentException("Distancia($distancia), duración($duracion) y coste($coste) deben ser mayores que 0")
        }

        val ruta = Ruta(inicio, fin, vehiculo, tipo, trayecto, distancia, duracion, coste, nombre)
        reset()
        return ruta
    }
}
