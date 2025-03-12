# Video Streaming App - Spring Boot + MongoDB + Flutter

Este repositorio contiene una aplicación completa de streaming de video con un backend en Spring Boot utilizando MongoDB para almacenamiento y una aplicación móvil desarrollada en Flutter para visualización. La arquitectura está inspirada en aplicaciones como TikTok, permitiendo navegar de forma vertical entre videos.

## Estructura del Repositorio

```
/
├── video-streaming/                 # Aplicación Spring Boot
│   ├── src/                 # Código fuente Java
│   ├── pom.xml              # Dependencias Maven
│   └── application.properties # Configuración
└── video_streaming_app/               # Aplicación Flutter
    ├── lib/                # Código fuente Dart
    ├── android/            # Configuración específica de Android
    ├── ios/                # Configuración específica de iOS
    └── pubspec.yaml        # Dependencias Flutter
```

## Requisitos Previos

### Backend
- Java JDK 11 o superior
- Maven 3.6 o superior
- MongoDB (local o MongoDB Atlas)

### Frontend
- Flutter SDK 3.0 o superior
- Dart 2.17 o superior
- Un IDE compatible (VS Code, Android Studio)
- Emulador o dispositivo físico para pruebas

## Configuración del Backend

### 1. Configurar MongoDB

Si estás usando MongoDB Atlas:

1. Crea una cuenta en [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Configura un nuevo cluster y obtén la cadena de conexión
3. Reemplaza la cadena de conexión en `application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/videostreaming?retryWrites=true&w=majority
```

Si usas MongoDB local:

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=videostreaming
```

### 2. Ejecutar el Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

El servidor se iniciará en `http://localhost:8080`.

## Configuración del Frontend

### 1. Instalar dependencias

```bash
cd frontend
flutter pub get
```

### 2. Configurar dirección del servidor

Edita el archivo `lib/services/api_service.dart` y actualiza la URL base:

```dart
final String baseUrl = 'http://192.168.1.X:8080/api';  // Reemplaza con la IP de tu servidor
```

- Para emulador Android: `10.0.2.2:8080`
- Para iOS Simulator: `localhost:8080` 
- Para dispositivos físicos: usa la IP de tu ordenador en la red

### 3. Configuración para Android

Para permitir el tráfico HTTP en texto plano, edita `android/app/src/main/AndroidManifest.xml`:

```xml
<application
    ...
    android:usesCleartextTraffic="true">
    ...
</application>
```

### 4. Ejecutar la aplicación Flutter

```bash
flutter run
```

## Endpoints API

El backend proporciona los siguientes endpoints RESTful:

### Gestión de Videos

| Método | Endpoint | Descripción | Parámetros |
|--------|----------|-------------|------------|
| GET | `/api/videos` | Obtener lista de todos los videos | Ninguno |
| GET | `/api/videos/{id}` | Obtener información de un video específico | `id`: ID del video |
| GET | `/api/videos/stream/{id}` | Transmitir un video específico | `id`: ID del video |
| POST | `/api/videos` | Subir un nuevo video | `file`: archivo de video (multipart), `title`: título del video, `description`: descripción del video |
| DELETE | `/api/videos/{id}` | Eliminar un video | `id`: ID del video |

### Ejemplo de uso con cURL

#### Listar todos los videos
```bash
curl -X GET http://localhost:8080/api/videos
```

#### Obtener información de un video
```bash
curl -X GET http://localhost:8080/api/videos/123456
```

#### Subir un nuevo video
```bash
curl -X POST http://localhost:8080/api/videos \
  -F "file=@/ruta/al/video.mp4" \
  -F "title=Mi Video" \
  -F "description=Esta es una descripción"
```

#### Eliminar un video
```bash
curl -X DELETE http://localhost:8080/api/videos/123456
```

## Funcionalidades Principales

### Backend (Spring Boot)

1. **Almacenamiento en MongoDB GridFS**: Los videos se almacenan como archivos binarios en MongoDB usando GridFS.
2. **Streaming con soporte para Byte Range**: Implementación de solicitudes HTTP Range para permitir la reproducción desde cualquier punto del video.
3. **Patrón MVC**: Implementación siguiendo el patrón Model-Repository-Service-Controller.
4. **CORS habilitado**: Configurado para permitir solicitudes desde diferentes orígenes.

### Frontend (Flutter)

1. **Interfaz estilo TikTok**: Navegación vertical entre videos con reproducción automática.
2. **Barra de navegación inferior**: Acceso rápido a feed de videos, subida y perfil.
3. **Reproductor de video optimizado**: Reproducción automática y pausado inteligente.
4. **Diseño responsivo**: Funciona en diferentes tamaños de pantalla.

## Prueba de la Aplicación

1. Inicia el backend Spring Boot.
2. Inicia la aplicación Flutter en tu dispositivo o emulador.
3. En la primera ejecución, el feed estará vacío.
4. Usa la función de subida para agregar videos desde tu dispositivo.
5. Los videos subidos aparecerán automáticamente en el feed.
6. Desliza verticalmente para navegar entre los videos.

## Solución de Problemas Comunes

### Backend

- **Error de conexión a MongoDB**: Verifica que la cadena de conexión sea correcta y que MongoDB esté ejecutándose.
- **Errores al subir archivos grandes**: Ajusta `spring.servlet.multipart.max-file-size` y `spring.servlet.multipart.max-request-size` en `application.properties`.

### Frontend

- **Error "Cleartext HTTP traffic not permitted"**: Asegúrate de haber configurado correctamente `android:usesCleartextTraffic="true"` en el `AndroidManifest.xml`.
- **No se pueden cargar videos**: Verifica que la URL del servidor sea correcta y que el servidor esté ejecutándose.
- **Problemas de reproducción**: Asegúrate de tener la última versión de las dependencias `video_player` y `chewie`.

## Consideraciones de Seguridad

- Esta implementación está diseñada para desarrollo y pruebas. Para un entorno de producción, considera:
  - Implementar HTTPS en el backend
  - Agregar autenticación de usuarios
  - Configurar límites de tamaño y tipos de archivos permitidos
  - Configurar políticas CORS más restrictivas

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta el archivo LICENSE para más detalles.
