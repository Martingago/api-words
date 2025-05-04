# 🧠 Microservicio de Scraping RAE – WordRadar

Este microservicio, desarrollado en **Python 3.12**, permite obtener definiciones y metadatos de palabras directamente desde el diccionario en línea de la Real Academia Española (RAE). Expuesto como API REST mediante **FastAPI**, forma parte de la arquitectura distribuida de WordRadar y depende del servicio de descubrimiento **Eureka Server**.

---

## ⚙️ Endpoint principal

```
POST /procesar-palabra
```

**Request:**

```json
{
  "word": "ejemplo"
}
```

> ⚠️ Una palabra mal escrita o derivada devolverá un objeto `related_word`. Las palabras no existentes devuelven un `404`.

---

## 📦 Ejecución en local

### 1. Crear entorno conda (recomendado)

```bash
conda create --name scrap_web python=3.12 -y
conda activate scrap_web
```

### 2. Instalar dependencias

```bash
conda install anaconda::requests anaconda::beautifulsoup4 conda-forge::fake-useragent conda-forge::fastapi
pip install cloudscraper py_eureka_client python-dotenv
```

### 3. Crear un fichero .env 
El fichero .env debe contener las siguientes claves:

```env
EUREKA_CLIENT_SERVICE=http://localhost:8761/eureka
SERVICE_PORT=8090
```

### 4. Lanzar servidor

```bash
python run.py
```

> 📅 El servicio necesita que **Eureka Server** esté corriendo en `http://localhost:8761/eureka` para registrarse correctamente.

---

## 🚀 Ejecución con Docker

### 1. Construir imagen Docker

```bash
docker build -t rae-scraper-service .
```

### 2. Ejecutar contenedor Docker

```bash
docker run -d -P \
  --name rae-scraper-service \
  -e EUREKA_CLIENT_SERVICE=http://localhost:8761/eureka \
  -e SERVICE_PORT=8090 \
  rae-scraper-service
```

> ❗ **IMPORTANTE**: El servicio no se registrará correctamente si el servidor de Eureka no está disponible o no se pasan correctamente las variables de entorno.

---

## 🧱 Requisitos previos

* Python 3.12
* FastAPI
* Uvicorn
* BeautifulSoup4
* cloudscraper
* py\_eureka\_client
* Docker (opcional)
* Eureka Server operativo

---

## 📊 Arquitectura

```
rae-scraper-service/
├── app/
│   ├── __init__.py
│   ├── config.py
│   ├── main.py
│   ├── scrapper.py
│   ├── session_manager.py
│   └── utils.py
├── .dockerignore
├── .env
├── .gitignore
├── Dockerfile
└── readme.md
├── requirements.txt
└── run.py
```

---

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
