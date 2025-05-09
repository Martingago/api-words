# 🕷️ Scraper CLI de palabras RAE

Este script en Python permite automatizar el proceso de scraping de definiciones, ejemplos, sinónimos y antónimos de palabras desde la web oficial del **Diccionario de la Real Academia Española (RAE)**.

Permite procesar lotes de palabras desde un archivo `.csv`, generar resultados en `.jsonl`, y actualizar el estado de las palabras procesadas en el CSV original.

## 📦 Requisitos

- Python `>=3.10` (recomendado 3.12)
- Se recomienda el uso de un entorno virtual (por ejemplo, `conda`)

Instalación de dependencias con `conda`:

```bash
conda create --name scrap_web python=3.12 -y
conda activate scrap_web
conda install anaconda::requests anaconda::beautifulsoup4 conda-forge::fake-useragent conda-forge::fastapi
pip install cloudscraper py_eureka_client python-dotenv
```

---

## ⚙️ Ejecución

```bash
python scraper.py --input palabras.csv --output resultado.jsonl --related relacionadas.csv --batch_size 20
```

Si no se especifican argumentos, el script usará valores por defecto:

```bash
📥 Entrada CSV: ./words_realated_5.csv
📤 Salida JSONL: ./rae_output_YYYYMMDD-HHMMSS.jsonl
🔁 Archivo de palabras relacionadas: ./words_realated_5.csv
📦 Tamaño de lote: 25
```

---

## 🧠 Comportamiento del script
1. Carga el archivo CSV con las palabras a buscar.
2. Consulta la RAE mediante scraping, extrayendo:
    - Calificación gramatical (sustantivo masculino, etc.)
    - Definición textual
    - Ejemplos de uso
    - Sinónimos y antónimos
3. Si la palabra no se encuentra, se buscará una sugerencia alternativa, que se almacenará como related_word.
4. Se escriben los resultados en formato .jsonl.
5. Se actualiza el CSV original con el estado (true, false, null) de cada palabra procesada.

---

## 📄 Formatos esperados

Entrada CSV

```txt
word,status
perro,false
gato,false
```

---

## Salida JSONL

```json
{"language":"esp","word":"perro","base_word":"perro","length":5,"definitions":[...]}
```

---

## 💡 Consejos
- Puedes automatizar el scraping en batches sin saturar la RAE.
- Añade sleeps si necesitas reducir carga (aunque el script ya incluye retry + headers falsificados).
- Recomendado: hacer backup del .csv original antes de ejecutar el script.

---

## ⚠️ Limitaciones
- El scraping está sujeto a cambios en el HTML de la RAE.
- No se garantiza estabilidad a largo plazo si modifican su estructura web.
- El servicio no debe ser abusado ni usado con fines masivos sin consentimiento.

### 👨‍💻 Desarrollado como parte del ecosistema WordRadar. Compatible con la arquitectura de microservicios y exportable a API REST si se desea.

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
