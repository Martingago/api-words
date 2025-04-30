# 🕸️ Web Scraper de Palabras — listasdepalabras.es

Este script permite scrapear palabras de longitud específica desde la web [listasdepalabras.es](listasdepalabras.es), y guardarlas en un archivo CSV con marca de tiempo.

## 📦 Requisitos
Se recomienda el uso de un entorno virtual (conda) para mantener tus dependencias limpias.

1. Crear el entorno
    ```bash
    conda create --name scrap_web python=3.12 -y
    ```
2. Activar el entorno
    ```bash
    source activate base  # Solo en Linux/macOS si tu terminal no activa automáticamente
    conda activate scrap_web
    ```
3. Instalar dependencias
    ```bash
    conda install anaconda::requests anaconda::beautifulsoup4 conda-forge::fake-useragent conda-forge::fastapi
    pip install cloudscraper py_eureka_client python-dotenv
    ```

    >NOTA: algunas dependencias como fastapi, cloudscraper o py_eureka_client no son necesarias para este script… pero si serán necesarias para otros scripts de scraping más avanzados en este repositorio.

## 🧠 Uso del script

```bash
python palabras_scraper.py <longitud_palabra> <numero_paginas>
```
### Ejemplo
```bash
python palabras_scraper.py 8 22
```

Esto creará un archivo CSV en la carpeta output/ con un nombre como: `result-words-length8-20250430-143921.csv`

## 📁 Estructura de salida
El CSV generado contiene una sola columna:

```csv
Palabras
abrigado
abundaba
acuerdos
...
```