# 🕸️ Web Scraper de Palabras — listasdepalabras.es

Este script permite scrapear palabras de longitud específica desde la web [listasdepalabras.es](https://www.listasdepalabras.es/), y guardarlas en un archivo CSV con marca de tiempo.

---

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

>NOTA: algunas dependencias como fastapi, cloudscraper o py_eureka_client no son necesarias para este script…  
pero si serán necesarias para otros scripts de scraping más avanzados en este repositorio.

---

## 🧠 Uso del script

```bash
python palabras_scraper.py <longitud_palabra> <numero_paginas>
```
### Ejemplo
```bash
python palabras_scraper.py 8 22
```

Esto creará un archivo CSV en la carpeta output/ con un nombre como: `result-words-length8-20250430-143921.csv`

---

## 📁 Estructura de salida
El CSV generado contiene una sola columna:

```csv
words
abrigado
abundaba
acuerdos
...
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