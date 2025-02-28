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