package es.uji.smallaris.model

class UserAlreadyExistsException(message: String = "El usuario ya existe.") : Exception(message)