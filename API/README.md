# 💡 RAE API Words — Servicio REST para el Diccionario RAE

**RAE API Words** es el servicio principal del ecosistema WordRadar. Expone una API REST para acceder, validar, gestionar y buscar palabras del diccionario de la Real Academia Española. El sistema está diseñado con arquitectura modular y escalable, incluyendo integración con servicios de descubrimiento, scraping de datos y seguridad JWT.

---

## ⚙️ Requisitos del sistema

- Java 17+
- Maven 3.8+
- Docker (para despliegue opcional)
- PostgreSQL (base de datos principal)
- **Eureka Server** ejecutándose localmente en `http://localhost:8761/eureka/`
- **Microservicio Scraper** activo si se quiere acceder a scraping automático

---

## 🏠 Variables de entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
DB_USER=root                      # Nombre de usuario de PostgreSQL
DB_PASSWORD=root                  # Contraseña del usuario
DB_URL=jdbc:postgresql://localhost:5432/api_words
EUREKA_CLIENT_SERVICE=http://localhost:8761/eureka/
JWT_KEY=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
JWT_USER=USER_JWT
```

---

## 🪪 Ejecución en entorno local

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/rae-api-words.git
cd rae-api-words
```

### 2. Iniciar Eureka Server
Debes tener activo el servicio Eureka en `localhost:8761`.
(Consulta la documentación de tu Eureka Server para iniciarlo)

### 3. Lanzar la aplicación

```bash
./mvnw spring-boot:run
```

---

## 🐳 Despliegue con Docker

Puedes construir y ejecutar el servicio con Docker:

```bash
docker build -t rae-api-words .
docker run -d -P --name rae-api-words rae-api-words
```

> Asegúrate de que las variables de entorno estén bien definidas o montadas vía `--env-file`.

---

## 🧬 Endpoints destacados

| Método | Endpoint                        | Descripción                                                          |
|--------|---------------------------------|----------------------------------------------------------------------|
| GET    | `/api/v1/qualifications`        | Obtiene listado de clasificaciones de palabras disponibles           |
| GET    | `/api/v1/stats`                 | Obtiene conjunto de estadísticas existentes del API                  |
| GET    | `/api/v1/languages`             | Carga información de los idiomas existentes en la BBDD               |
| GET    | `/api/v1/words/{word}`          | Busca una palabra en la base de datos                                |
| GET    | `/api/v1/words/{word}/deep`     | Busca y scrapea palabra si no existe                                 |
| GET    | `/api/v1/words/random`          | Obtiene una palabra aleatoria de la base de datos                    |
| GET    | `/api/v1/words/daily`           | Obtiene la palabra diaria generada por el servidor                   |
| GET    | `/api/v1/words/names`           | Paginación de búsqueda de palabras con filtros                       |
| GET    | `/api/v1/words/details`         | Lo anterior, pero devuelve un objeto detallado de las palabras       |
| GET    | `/api/v1/words/{word}/synonyms` | Muestra listado palabras sinónimas de una búsqueda concreta          |
| GET    | `/api/v1/words/{word}/antonyms` | Muestra listado palabras antónimas de una búsqueda concreta          |
| POST   | `/api/v1/scrap-word`            | Scrapea manualmente una palabra y la añade a la BBDD                 |
| POST   | `/api/v1/private/add-word`      | Carga un objeto de palabra en la BBDD                                |
| POST   | `/api/v1/private/upload-jsonl`  | Subida de archivo masivo `.jsonl` que carga palabras en la BBDD      |
| DELETE | `/api/v1/private/delete`        | Eliminar palabra (requiere autenticación)                            |
| POST   | `/api/v1/validate/file/json`    | Validación masiva desde archivo CSV y devuelve respuesta en el body  |
| POST   | `/api/v1/validate/file/csv`     | Validación masiva desde archivo CSV y devuelve un fichero `.csv`     |
| POST   | `/api/v1/validate/body/json`    | Validación desde el body de la petición y su respuesta es en el body |
| POST   | `/api/v1/validate/body/csv`     | Validación desde el body de la petición y devuelve un fichero `.csv` |

> Consulta `/documentation` para ver la documentación detallada de todos los endpoints disponibles.

---

## 🔐 Seguridad

Este servicio está protegido con JWT. Se requiere autenticarse para endpoints privados mediante un token generado en el endpoint de login:

```http
POST /auth/login
```

Usa el usuario `JWT_USER` y obtén el token firmado con `JWT_KEY`.

---

## 📙 Arquitectura

```
├── batch/
├── client/
├── config/
├── context/
├── controller/
├── domain/
│   ├── model/
│   ├── repository/
│   ├── service/
├── dto/
├── exceptions/
├── mapper/
├── utils/
├── resources/
│   ├── application.yml
│   ├── .env
```

- Modularidad limpia, siguiendo principios de Clean Architecture adaptada a Spring
- Servicios desacoplados, fácil mantenimiento y pruebas
- Repositorios extendidos con filtros dinámicos y paginación

---


## 🧐 Créditos

Desarrollado por [Martín Gago](https://martingago.dev/) como parte del proyecto WordRadar  
Inspirado en la necesidad de un diccionario español accesible, potente y documentado.

---

## 📄 Licencia

MIT License o la que decidas usar.

