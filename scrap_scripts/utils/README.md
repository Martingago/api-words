# 🧹 Limpieza de Palabras - Script CSV

Este script está diseñado para limpiar y normalizar palabras almacenadas en un archivo `.csv`, eliminando números, caracteres especiales y duplicados, y exportando el resultado a un nuevo archivo limpio.

---

## 🚀 Funcionalidades

- Elimina números, guiones y caracteres especiales no deseados.
- Convierte las palabras a mayúsculas (manteniendo tildes y ñ).
- Elimina duplicados automáticamente.
- Soporte para ejecución con parámetros desde línea de comandos.
- Alternativa por defecto para funcionar sin parámetros, ideal para entornos de desarrollo.

---

## 🛠️ Requisitos

- Python 3.x
- No requiere librerías externas (usa solo librerías estándar).

---

## 📦 Instalación y uso

### ✅ Modo 1: Sin parámetros (modo local)
El script usará rutas preconfiguradas dentro del mismo archivo:

```bash
python limpiar_csv.py
```

```python
default_input = "../scrap_list_words/output/archivo-original.csv"
default_output = "../scraper/archivo-limpio.csv"
```

## ✅ Modo 2: Con parámetros

```bash
python limpiar_csv.py --input ruta/entrada.csv --output ruta/salida.csv
```
Ejemplo:

```bash
python limpiar_csv.py --input ./data/original.csv --output ./data/limpio.csv
```

---

## 💡 Estructura esperada del archivo de entrada
El archivo debe tener la siguiente estructura:

```csv
word	
piedra	
árbol
```

El script limpiará la columna `word` y creará la columna `status` a con los valores en false.

---

## 📁 Salida generada
Archivo `.csv` con dos columnas: `word` y `status`. Todas las palabras estarán en mayúsculas, únicas y limpias.

## Ejemplo de uso

```csv
Input:
word
- piedra
árbol+
piedra

Output:
word, status
PIEDRA,false
ÁRBOL,false

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