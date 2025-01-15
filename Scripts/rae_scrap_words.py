import os
import requests
from bs4 import BeautifulSoup
import csv
import time
import random
from fake_useragent import UserAgent
import urllib3
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
        #time.sleep(random.uniform(0.1, 0.3))
        response = session.get(url, headers=headers, timeout=1)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, 'html.parser')

        ol = soup.find('ol', class_='c-definitions')
        if not ol:
            print("{palabra} no fue encontrada en RAE")
            return "NOT_FOUND", None

        li = ol.find('li')
        if not li:
            return None, None

        abbr = li.find('abbr')
        calification = abbr['title'] if abbr else None

        calification = f'"{calification}"'

        definition_div = li.find('div', class_='c-definitions__item', role='definition')
        if not definition_div:
            return calification, None

        spans = definition_div.find_all('span', attrs={'data-id': True})

        if not spans:
            enlace = definition_div.find('a', class_='a')
            definition = enlace.get_text(strip=True) if enlace else None
        else:
            definition = ' '.join(span.get_text(strip=True) for span in spans)

        if definition:
            definition = f'"{definition}"'

        return calification, definition

    except requests.exceptions.RequestException as e:
        print(f"Error al procesar '{palabra}': {e}")
        return None, None

def escribir_a_archivo(rows, fieldnames, output_path):
    modo = 'a' if os.path.exists(output_path) else 'w'
    with open(output_path, modo, encoding='utf-8', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames, extrasaction='ignore', restval='null')
        if modo == 'w':
            writer.writeheader()
        writer.writerows(rows)

def actualizar_csv_original(input_path, rows):
    temp_file_path = input_path + ".tmp"
    with open(input_path, 'r', encoding='utf-8') as infile, open(temp_file_path, 'w', encoding='utf-8', newline='') as outfile:
        reader = csv.DictReader(infile)
        fieldnames = reader.fieldnames
        writer = csv.DictWriter(outfile, fieldnames=fieldnames, extrasaction='ignore', restval='null')

        writer.writeheader()
        for row in reader:
            palabra = row.get('word', '').strip().lower()
            # Busca la palabra en las filas procesadas
            status_row = next((r for r in rows if r['word'].strip().lower() == palabra), None)
            if status_row:
                # Actualiza el status y cualquier otro campo necesario
                row['status'] = status_row.get('status', 'false')
            writer.writerow(row)  # Escribe la fila actualizada o tal como estaba

    # Reemplaza el archivo original por el actualizado
    shutil.move(temp_file_path, input_path)


def procesar_archivo(input_path, output_path, batch_size=10):
    session = RetrySession()
    all_rows = []

    with open(input_path, 'r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        fieldnames = reader.fieldnames + ['calification', 'definition', 'word_length', 'language']

        for row in reader:
            try:
                status = row.get('status', '')
                # Asegúrate de que `status` no sea None antes de llamar a strip y lower
                status = status.strip().lower() if status is not None else ''

                if status == 'null':
                    print(f"Saltando palabra: {row.get('word', '')}")
                    continue
                if status == 'false':
                    palabra = row.get('word', '').strip().lower()
                    if not palabra:
                        continue

                    print(f"Procesando palabra: {palabra}")
                    calification, definition = obtener_datos(palabra, session)

                    if calification == "NOT_FOUND":
                        row.update({
                            'status': 'null',
                            'calification': 'null',
                            'definition': 'null',
                            'word_length': 'null',
                            'language': 'null',
                        })
                    else:
                        row.update({
                            'calification': calification if calification else 'null',
                            'definition': definition if definition else 'null',
                            'status': 'true' if definition else 'false',
                            'word_length': len(palabra) if palabra else 'null',
                            'language': 'esp' if palabra else 'null',
                        })

                    all_rows.append(row)

                    if len(all_rows) % batch_size == 0:
                        escribir_a_archivo(all_rows, fieldnames, output_path)
                        actualizar_csv_original(input_path, all_rows)
                        print(f"Procesadas {len(all_rows)} palabras")
                        all_rows.clear()

            except Exception as e:
                # Manejo general de errores para evitar que el programa falle
                print(f"Error procesando fila: {row}. Detalles: {e}")
                continue  # Continúa con la siguiente fila incluso si hay un error

        if all_rows:
            escribir_a_archivo(all_rows, fieldnames, output_path)
            actualizar_csv_original(input_path, all_rows)
            print(f"Procesamiento completado. Total de palabras procesadas: {len(all_rows)}")


if __name__ == "__main__":
    input_path = "palabras_test.csv"
    output_path = "palabras_definiciones_output.csv"
    procesar_archivo(input_path, output_path, batch_size=10)
