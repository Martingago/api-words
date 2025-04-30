# Microervicio de Scraping de la RAE

Pequeño microservicio creado con Python que expone un único endpoint: `/procesar-palabra` que recibe una palabra la cual será buscada empleando la página de la RAE y devolverá un JSON que contendrá la información relevante de la palabra.

 > **NOTA**: Una palabra **mal escrita** o **derivada** de otra se devolverá como un objeto diferente `related_word`, y aquellas palabras no existentes se devolverán bajo un 404.

```bash
docker build -t rae-scraper-service .
docker run -d -P --name rae-scraper-service rae-scraper-service

```