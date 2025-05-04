# PROYECTO API RAE

Este es un proyecto de API REST del diccionario de la Real Academia Española (RAE).
Este proyecto cuenta con los siguientes servicios disponibles para el usuario:

 > Servicio de API REST con funcionalidades de:
- Búsqueda de palabras.
- Generador de palabras aleatorias.
- Generador de palabra diaria.
- Búsqueda de sinónimos y antónimos.
- Validación de palabras
- Listas de palabras (filtros de tamaño de palabra, carácteres de inicio/fin, semánticamente...)

> Microservicio de scraping web:
- Término a scrapear.

> Documentación:
- Documentación detallada creada con **Redoc** a la que se puede acceder a través de `/documentation`.

> Landing page:
- Landing page de muestra del servicio creada con **Angular**


## Set-up del contenedor docker

Comando para lanzar el contenedor docker junto sus microservicios:
```bash
docker-compose up
```

### Reiniciar servicio de docker

```bash
docker-compose down
docker-compose up --build
```

## 🏠 Variables de entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
DB_USER=root                      # Nombre de usuario de PostgreSQL
DB_PASSWORD=root                  # Contraseña del usuario
DB_URL=jdbc:postgresql://host.docker.internal:5432/api_words
EUREKA_CLIENT_SERVICE=http://eureka-server:8761/eureka/
SERVICE_PORT=8090                 # Puerto para el microservicio de scrapping
JWT_KEY=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
JWT_USER=USER_JWT
```

## 🧐 Créditos

Desarrollado por [Martín Gago](https://martingago.dev/) como parte del proyecto WordRadar  
Inspirado en la necesidad de un diccionario español accesible, potente y documentado.

---

## 📄 Licencia

### MIT License

Copyright © 2025 [martingago.dev](https://martingago.dev/)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and 
associated documentation files (the “Software”), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial 
portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
