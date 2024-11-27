package es.uji.smallaris.model

class UnregisteredUserException(message: String = "El usuario no está registrado.") : Exception(message)