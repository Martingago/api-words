import requests
from bs4 import BeautifulSoup
import csv
import time
import random
from fake_useragent import UserAgent
import urllib3
import tempfile
import os
import shutil

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
    base_url = "https://dle.rae.es/{}"
    url = base_url.format(palabra)
    
    ua = UserAgent()
    headers = {
        'User-Agent': ua.random,
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Language': 'es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3',
        'Accept-Encoding': 'gzip, deflate, br',
        'Connection': 'keep-alive',
    }

    try:
        time.sleep(random.uniform(0.2, 0.5))
        response = session.get(url, headers=headers, timeout=3)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, 'html.parser')
        
        ol = soup.find('ol', class_='c-definitions')
        if not ol:
            print(f"No se encontró definición para la palabra '{palabra}'.")
            return "NOT_FOUND", None

        li = ol.find('li')
        if not li:
            return None, None

        abbr = li.find('abbr')
        calification = abbr['title'] if abbr else None

        definition_div = li.find('div', class_='c-definitions__item', role='definition')
        if not definition_div:
            return calification, None

        spans = definition_div.find_all('span', attrs={'data-id': True})
        
        if not spans:
            enlace = definition_div.find('a', class_='a')
            definition = enlace.get_text(strip=True) if enlace else None
        else:
            definition = ' '.join(span.get_text(strip=True) for span in spans)
            
        # Añadir comillas triples solo cuando se genera la definición inicialmente
        if definition:
            definition = f'"{definition}"'

        return calification, definition

    except requests.exceptions.RequestException as e:
        print(f"Error al procesar '{palabra}': {str(e)}")
        return None, None

def escribir_a_archivo(rows, fieldnames, output_path):
    """Función separada para escribir los datos en un nuevo archivo"""
    with open(output_path, 'w', encoding='utf-8', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()
        for row in rows:
            writer.writerow(row)

def procesar_archivo(input_path, output_path, batch_size=10):
    session = RetrySession()
    all_rows = []
    
    with open(input_path, mode='r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        fieldnames = reader.fieldnames
        
        for row in reader:
            status = row.get('status', '').strip().lower()
            
            if status != 'true':
                palabra = row.get('word', '').strip().lower()
                if not palabra:
                    continue
                    
                print(f"Procesando palabra: {palabra}")
                
                calification, definition = obtener_datos(palabra, session)
                
                if calification == "NOT_FOUND":
                    row['status'] = 'null'
                else:
                    if not calification and not definition:
                        # Reintentos en caso de fallo
                        for _ in range(2):
                            time.sleep(random.uniform(1, 2))
                            calification, definition = obtener_datos(palabra, session)
                            if calification or definition:
                                break

                    if calification and calification != "NOT_FOUND":
                        row['calification'] = calification
                    if definition:
                        row['definition'] = definition
                        row['status'] = 'true'
                
            all_rows.append(row)
            
            # Escribir en lotes al archivo de salida
            if len(all_rows) % batch_size == 0:
                escribir_a_archivo(all_rows, fieldnames, output_path)
                print(f"Procesadas {len(all_rows)} palabras")
        
        # Escribir las filas restantes
        if all_rows:
            escribir_a_archivo(all_rows, fieldnames, output_path)
            print(f"Procesamiento completado. Total de palabras procesadas: {len(all_rows)}")

if __name__ == "__main__":
    input_path = "palabras_definiciones.csv"
    output_path = "palabras_definiciones_output.csv"
    procesar_archivo(input_path, output_path, batch_size=10)