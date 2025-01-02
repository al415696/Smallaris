package es.uji.smallaris.model

class WrongPasswordException(message: String = "Contraseña incorrecta para el usuario") : Exception(message)