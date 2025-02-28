# SERVICIO API DICCIONARIO RAE

Servicio principal de la aplicación. Se trata de una API REST que contiene la información de las palabras del diccionario de la Real Academia Española.

> **NOTA**: Se requiere de la ejecución del servicio de **Eureka Server** para el correcto funcionamiento de esta aplicación. Además algunos endpoints requieren de la ejecución del microservicio de Scraping para poder devolver correctamente las salidas.

## Crear contenedor docker

Comandos para ejecutar y crear un contenedor docker:
```bash
docker build -t rae-api-words .
docker run -d -P --name rae-api-words rae-api-words
```