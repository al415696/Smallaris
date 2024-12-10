package es.uji.smallaris.model

import com.mapbox.geojson.LineString

interface IBuilderRutas {
    fun setInicio(inicio: LugarInteres): IBuilderRutas
    fun setFin(fin: LugarInteres): IBuilderRutas
    fun setVehiculo(vehiculo: Vehiculo): IBuilderRutas
    fun setTipo(tipo: TipoRuta): IBuilderRutas
    fun setTrayecto(trayecto: LineString): IBuilderRutas
    fun setDistancia(distancia: Float): IBuilderRutas
    fun setDuracion(duracion: Float): IBuilderRutas
    fun setCoste(coste: Double): IBuilderRutas
    fun reset()
    fun getRuta(): Ruta
}