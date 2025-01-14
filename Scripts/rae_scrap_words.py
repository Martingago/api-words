import requests
from bs4 import BeautifulSoup
import csv
import time
import random
from fake_useragent import UserAgent
import http.client
import urllib3
import tempfile
import os
import shutil
from itertools import islice

class RetrySession(requests.Session):
    def __init__(self, retries=3, backoff_factor=0.3, status_forcelist=(500, 502, 504)):
        super().__init__()
        retry = urllib3.util.Retry(
            total=retries,
            read=retries,
            connect=retries,
            backoff_factor=backoff_factor,
            status_forcelist=status_forcelist,
        )
        adapter = requests.adapters.HTTPAdapter(pool_connections=100, pool_maxsize=100, max_retries=retry)
        self.mount('http://', adapter)
        self.mount('https://', adapter)

def obtener_datos(palabra, session):
    # [El resto de la función obtener_datos permanece igual]
    base_url = "https://dle.rae.es/{}"
    url = base_url.format(palabra)
    
    ua = UserAgent()
    headers = {
        'User-Agent': ua.random,
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Language': 'es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3',
        'Accept-Encoding': 'gzip, deflate, br',
        'DNT': '1',
        'Connection': 'keep-alive',
        'Upgrade-Insecure-Requests': '1',
        'Sec-Fetch-Dest': 'document',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-Site': 'none',
        'Sec-Fetch-User': '?1',
        'Cache-Control': 'max-age=0',
    }

    try:
        time.sleep(random.uniform(0.2, 0.5))
        response = session.get(url, headers=headers, timeout=3)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, 'html.parser')
        ol = soup.find('ol', class_='c-definitions')
        if not ol:
            print(f"No se encontró <ol class='c-definitions'> para la palabra '{palabra}'.")
            return "NOT_FOUND", None

        li = ol.find('li')
        if not li:
            print(f"No se encontró <li> dentro de <ol> para la palabra '{palabra}'.")
            return None, None

        abbr = li.find('abbr')
        calification = abbr['title'] if abbr else None

        definition_div = li.find('div', class_='c-definitions__item', role='definition')
        if not definition_div:
            print(f"No se encontró el div de definición para la palabra '{palabra}'.")
            return calification, None

        spans = definition_div.find_all('span')
        definition = ' '.join(span.get_text(strip=True) for span in spans)

        return calification, definition

    except requests.exceptions.RequestException as e:
        print(f"Error al procesar '{palabra}': {str(e)}")
        return None, None

def guardar_lote(rows, fieldnames, file_path):
    temp_fd, temp_path = tempfile.mkstemp(text=True)
    os.close(temp_fd)

    try:
        # Primero leemos todas las filas existentes
        existing_rows = []
        if os.path.exists(file_path):
            with open(file_path, 'r', encoding='utf-8') as file:
                reader = csv.DictReader(file)
                existing_rows = list(reader)

        # Actualizamos las filas existentes con las nuevas
        rows_dict = {row['word']: {k: v for k, v in row.items() if k in fieldnames} 
                    for row in rows if 'word' in row}

        for row in existing_rows:
            if row.get('word') in rows_dict:
                # Solo actualizar con campos válidos
                row.update(rows_dict[row['word']])

        # Asegurarnos de que todas las filas solo contengan campos válidos
        cleaned_rows = []
        for row in existing_rows:
            cleaned_row = {k: v for k, v in row.items() if k in fieldnames}
            cleaned_rows.append(cleaned_row)

        # Escribimos todas las filas al archivo temporal
        with open(temp_path, 'w', encoding='utf-8', newline='') as file:
            writer = csv.DictWriter(file, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(cleaned_rows)

        # Reemplazamos el archivo original
        shutil.move(temp_path, file_path)
        print(f"Guardado lote de datos. Progreso guardado en: {file_path}")

    except Exception as e:
        print(f"Error al guardar el lote: {str(e)}")
        if os.path.exists(temp_path):
            os.remove(temp_path)
        raise

def procesar_archivo(file_path, batch_size=10):
    session = RetrySession()
    processed_batch = []
    
    with open(file_path, mode='r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        fieldnames = reader.fieldnames
        
        max_intentos = 3
        palabras_procesadas = 0
        
        for row in reader:
            # Asegurarnos de que la fila tiene todos los campos necesarios
            cleaned_row = {k: v for k, v in row.items() if k in fieldnames}
            
            # Validar el status con un valor por defecto seguro
            status = cleaned_row.get('status')
            if status is None:
                status = 'false'  # valor por defecto si status es None
            
            if str(status).strip().lower() == 'false':
                palabra = cleaned_row.get('word', '').strip().lower()
                if not palabra:
                    continue
                    
                print(f"Procesando palabra: {palabra}")
                
                calification, definition = obtener_datos(palabra, session)
                
                if calification == "NOT_FOUND":
                    cleaned_row['status'] = 'null'
                else:
                    if not calification and not definition:
                        for intento in range(max_intentos - 1):
                            time.sleep(random.uniform(1, 2))
                            calification, definition = obtener_datos(palabra, session)
                            if calification or definition:
                                break

                    if calification and calification != "NOT_FOUND":
                        cleaned_row['calification'] = calification
                    if definition:
                        cleaned_row['definition'] = definition
                        cleaned_row['status'] = 'true'
                
                processed_batch.append(cleaned_row)
                palabras_procesadas += 1

                if palabras_procesadas % batch_size == 0:
                    guardar_lote(processed_batch, fieldnames, file_path)
                    processed_batch = []
                    print(f"Procesadas {palabras_procesadas} palabras")
            else:
                processed_batch.append(cleaned_row)

        if processed_batch:
            guardar_lote(processed_batch, fieldnames, file_path)
            print(f"Procesamiento completado. Total de palabras procesadas: {palabras_procesadas}")



if __name__ == "__main__":
    file_path = "palabras_definiciones_test.csv"
    procesar_archivo(file_path, batch_size=10)  # Guarda cada 10 palabras