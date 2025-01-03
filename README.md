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
3. **OpenRouteService API**: Servicio de cálculo de rutas, geocodificación, geocodificación inversa.
4. **Mapbox**: Plataforma de mapas y navegación interactiva.
5. **Firebase Firestore**: Base de datos noSQL en la nube para la persistencia de datos.
6. **APIs de Precio de Combustible y Electricidad**: Para la estimación de costes asociados a rutas, proporcionada por organismos públicos.
7. **MockK**: Herramienta de mocking para pruebas de integración.
8. **JUnit4**: Herramienta de test para pruebas de aceptación.

---

## Requisitos de Instalación

### 1. Clonar el Repositorio
```bash
git clone https://github.com/al415696/Smallaris.git
cd Smallaris
```

### 2. Configurar las claves API
  Configura el archivo local.properties en la raíz del proyecto y añade las claves API necesarias para los siguientes servicios:
  
  - OpenRouteService
  - Firebase
  - Mapbox

### 3. Configurar el entorno Android
  Instala Android Studio y configura un dispositivo físico o virtual con al menos Android 8.0 (API 26)..

### 4. Sincronizar y compilar
  -  Abre el proyecto en Android Studio.
  - Sincroniza el proyecto con Gradle para asegurarte de que todas las dependencias estén descargadas y configuradas correctamente.
  - Compila y ejecuta el proyecto en tu dispositivo o emulador Android.

---

## Estructura del Proyecto

El proyecto sigue una estructura modular organizada de la siguiente manera:

- **`app/src/`**
  - **`androidTest/`**: Contiene las pruebas de integración *end-to-end* sobre los diferentes servicios implementados.
  - **`main/`**
    - **`model/`**: Define las clases de dominio, como usuarios, rutas y vehículos. También contiene los servicios relacionados con APIs y la persistencia de datos.
    - **`ui/`**: Interfaz de usuario.
      - **`components/`**: funciones componibles generalizadas(componentes de UI) para ser usadas en varios ficheros sin redundancia.
      - **`navigation/`**: Elementos directamente relacionados con la navegación como el controller de navegación y las rutas e iconos de cada ventana.
      - **`screens/`**: Los grandes fragmentos de UI, las ventanas y subventanas que forman la GUI usando los elementos de los directiorios adyacentes.
      - **`state/`**: Los View Models que comunican y adaptan la funcionalidad de los servicios del modelo para ser usados en la interfaz.
      - **`theme/`**: La configuración general de elementos gráficos en compose: paletas de colores, tipografía y formas de contenedores.
    - **`MainActivity/`**:  El archivo que se debe ejecutar para iniciar la aplicación. 
  - **`test/`**: Contiene las pruebas de aceptación  sobre los diferentes servicios implementados con todas las dependencias sustituidas con mocks de a librerria *Mockk*.

---

## Autores

Este proyecto ha sido desarrollado por:

- **[Alejandro Díaz Rivero](https://github.com/MCY-1911)**: Gesttión de lugares de iterés, rutas y mapas.
- **[Hugo Martí Fernández](https://github.com/HugoMartiFernandez)**: Gestión de usuarios y Firerbase.
- **[Oscar Renau Pallarés](https://github.com/al415696)**: Gestión de vhiculos, servicios de prcios e interfaces gráficas.

Si deseas colaborar o tienes alguna pregunta, no dudes en ponerte en contacto con los autores a través de sus perfiles de GitHub.

--- 

## Licencia

Este proyecto está licenciado bajo la licencia [Apache 2.0](LICENSE).

### Permisos otorgados:
- Uso comercial
- Modificación
- Distribución
- Uso privado

### Limitaciones:
- Responsabilidad limitada
- Sin garantías

Consulta el archivo [`LICENSE`](LICENSE.txt) en la raíz del repositorio para más detalles.



