# Smallaris - Aplicación de Movilidad

Smallaris es una aplicación de movilidad diseñada para ofrecer una experiencia integral en la planificación de rutas. Proporciona funcionalidades avanzadas como cálculo de rutas personalizadas, estimación de costes de viaje (combustible, electricidad, pie o bicicleta), y una interfaz basada en mapas para una experiencia de usuario fluida e intuitiva.

## Descripción del Proyecto

Este proyecto es parte de las asignaturas **EI1039 - Diseño de Software** y **EI1048 - Paradigmas de Software** del grado de ingenieria informática en la UJI, y tiene como objetivo implementar una aplicación Android empleando patrones de diseño, la metodologías ágile ATDD y tecnologías modernas como **Jetpack Compose**.

## Características Principales

- **Gestión de Usuarios**: Registro, inicio de sesión, cierre de sesión, borrado de cuenta y cambio de contraseña.
- **Mapas Interactivos**: Uso de **Mapbox** para seleccionar puntos y visualizar rutas.
- **Cálculo de Rutas**: Integración con **OpenRouteService API** para obtener rutas con trayectos cortos, rápidos y económicos con detalles como duración y distancia.
- **Estimación de Costes**: Consultas a APIs públicas para obtener precios de combustibles y electricidad mediante APIs públicas.
- **Persistencia de Datos**: Almacenamiento de usuarios, rutas, vehículos y lugares de interés mediante **Firebase Firestore**.
- **Favoritos**: Posibilidad de marcar lugares, rutas y vehículos como favoritos para acceso rápido desde a interfaz de usuario.

---

## Tecnologías Empleadas

1. **Android Studio**: IDE principal para el desarrollo.
2. **Jetpack Compose**: Marco moderno para construir interfaces de usuario nativas en Android.
3. **OpenRouteService API**: Servicio de cálculo de rutas y geocodificación.
4. **Mapbox**: Plataforma de mapas y navegación interactiva.
5. **Firebase Firestore**: Base de datos en la nube para la persistencia de datos.
6. **APIs de Precio de Combustible y Electricidad**: Para la estimación de costes asociados a rutas.
7. **MockK**: Herramienta de mocking para pruebas unitarias e integración.

---

## Requisitos de Instalación

### Clonar el Repositorio
```bash
git clone https://github.com/al415696/Smallaris.git
cd Smallaris
