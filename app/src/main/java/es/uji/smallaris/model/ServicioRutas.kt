package es.uji.smallaris.model

import es.uji.smallaris.model.lugares.LugarInteres
import kotlinx.coroutines.runBlocking
import kotlin.jvm.Throws

class ServicioRutas(
    private val calculadorRutas: CalculadorRutas,
    private val repositorioRutas: RepositorioRutas = RepositorioFirebase(),
    private val servicioRutasYCoste: ServicioAPIs = ServicioAPIs
) {
    private var rutas = mutableListOf<Ruta>()

    init {
        runBlocking {
            updateRutas()
        }
    }

    suspend fun updateRutas() {
        this.rutas = repositorioRutas.getRutas().toMutableList()
    }

    @Throws(ConnectionErrorException::class, RouteException::class)
    suspend fun addRuta(ruta: Ruta): Ruta {
        if (ruta.isFavorito())
            throw RouteException("No se puede eliminar una ruta favoria")
        if (!repositorioRutas.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")
        if (rutas.contains(ruta))
            throw RouteException("La ruta ya existe")
        if  (repositorioRutas.addRuta(ruta))
            rutas.add(ruta)
        else
            throw RouteException("No se pudo añadir la ruta por un problema remoto")
        return ruta
    }

    @Throws(ConnectionErrorException::class)
    suspend fun getRutas(ordenRuta: OrdenRuta = OrdenRuta.FAVORITO_THEN_NOMBRE): List<Ruta> {
        if (!repositorioRutas.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")
        return rutas.sortedWith(
            ordenRuta.comparator()
        )
    }

    fun builder(): RutaBuilderWrapper {
        return RutaBuilderWrapper(calculadorRutas, servicioRutasYCoste, this)
    }

    @Throws(ConnectionErrorException::class)
    suspend fun setFavorito(ruta: Ruta, favorito: Boolean = true): Boolean {
        if (!repositorioRutas.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")
        if (ruta.isFavorito() == favorito)
            return false
        ruta.setFavorito(favorito)
        if (rutas.contains(ruta)) {
            repositorioRutas.setRutaFavorita(ruta, favorito)
            return true
        }
        return false
    }

    @Throws(ConnectionErrorException::class, RouteException::class)
    suspend fun deleteRuta(ruta: Ruta): Boolean {
        if (!repositorioRutas.enFuncionamiento())
            throw ConnectionErrorException("Firebase no está disponible")

        if (ruta.isFavorito()) {
            throw RouteException("Ruta favorita no se puede borrar")
        }

        rutas.remove(ruta)
        return repositorioRutas.deleteRuta(ruta)
    }

    fun contains(ruta: Ruta): Boolean {
        return rutas.contains(ruta)
    }
    fun contains(lugar: LugarInteres): List<Ruta> {
        val lista = mutableListOf<Ruta>()
        for (ruta in rutas){
            if (ruta.getInicio() == lugar || ruta.getFin() == lugar)
                lista.add(ruta)
        }
        return lista
    }

    companion object{
        private lateinit var servicio: ServicioRutas
        fun getInstance(): ServicioRutas{
            if (!this::servicio.isInitialized){
                servicio = ServicioRutas(CalculadorRutasORS(ServicioAPIs), RepositorioFirebase.getInstance(), ServicioAPIs)
            }
            return servicio
        }
    }

}