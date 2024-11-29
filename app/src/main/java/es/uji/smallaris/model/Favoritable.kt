package es.uji.smallaris.model

abstract class Favoritable {
    var favorito : Boolean = false
    fun isFavorito(): Boolean{
        return false
    }
    fun setFavorito(boolean: Boolean): Boolean{
        return false
    }
}