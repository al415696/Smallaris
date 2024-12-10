package es.uji.smallaris.model

abstract class Favoritable {
    private var favorito : Boolean = false
    fun isFavorito(): Boolean{
        return favorito
    }
    fun setFavorito(boolean: Boolean): Boolean{
        var result = boolean != favorito
        favorito = boolean
        return result
    }
}