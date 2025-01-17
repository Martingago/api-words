import os
import requests
from bs4 import BeautifulSoup
import csv
import json
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

# Extrae los datos de una palabra
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
        response = session.get(url, headers=headers, timeout=1)
        response.raise_for_status()

        soup = BeautifulSoup(response.text, 'html.parser')

        section_definitions = soup.find('ol', class_='c-definitions')
        if not section_definitions:
            print(f"{palabra} no fue encontrada en RAE")

            #Si la palabra no fue encontrada, entonces busca una palabra relacionada:
            palabra_relacionada = soup.find('a', attrs={'data-acc': 'LISTA APROX'})
            if palabra_relacionada:
                relacionada = palabra_relacionada.get_text(strip=True)
                print(f"Se ha encontrado palabra relacionada: {relacionada}")
                # Guardar la palabra relacionada en el archivo de palabras similares
                guardar_palabra_relacionada(relacionada)


            # Devolver un null y no procesar esta palabra en el json
            return None

        definitions = []
        for li in section_definitions.find_all('li', recursive=False):
            definition_div = li.find('div', class_='c-definitions__item', role='definition')
            if not definition_div:
                continue

            #Extrae la calificacion:
            first_div = definition_div.find('div')
            calification_word = first_div.find('abbr')['title'] if first_div and first_div.find('abbr') else None

            # Extraer solo la definición o, en su defecto, el enlace <a>
            definition_text_parts = first_div.find_all('span', attrs={'data-id': True}) if first_div else []
            if definition_text_parts:
                definition_word = ' '.join(span.get_text(strip=True) for span in definition_text_parts)
            else:
                # Si no hay definición, buscar un enlace <a> y extraer su texto
                link_element = first_div.find('a', class_='a') if first_div else None
                definition_word = link_element.get_text(strip=True) if link_element else None

            # Extraer sinónimos y antónimos del footer
            sinonimos = []
            antonimos = []

            footer_div = definition_div.find('div', class_='c-definitions__item-footer')
            if footer_div:
                word_lists = footer_div.find_all('div', class_='c-word-list')
                for word_list in word_lists:
                    abbr = word_list.find('abbr', class_='sin-header-inline d')
                    if not abbr:
                        continue

                    title = abbr.get('title', '').strip()
                    if title == "Sinónimos o afines":
                        sinonimos.extend(
                            formatear_palabra(span.get_text(strip=True))
                            for span in word_list.find_all('span', class_='sin')
                        )
                    elif title == "Antónimo u opuesto":
                        antonimos.extend(
                            formatear_palabra(span.get_text(strip=True))
                            for span in word_list.find_all('span', class_='sin')
                        )

            definitions.append({
                "calification": calification_word,
                "definition": definition_word,
                "sinonimos": sinonimos,
                "antonimos": antonimos,
            })

        return {
            "language": "esp",
            "word": palabra,
            "length": len(palabra),
            "definitions": definitions
        }

    except requests.exceptions.RequestException as e:
        print(f"Error al procesar '{palabra}': {e}")
        return False



def formatear_palabra(palabra):
    palabra_sin_numeros = ''.join(char for char in palabra if not char.isdigit())
    return palabra_sin_numeros

# Limpia datos que puedan contener palabras relacionadas: números, guiones, etc.
def limpiar_palabra(palabra):
    palabra_sin_numeros = ''.join(char for char in palabra if not char.isdigit())
    # Si la palabra contiene una coma, tomar solo la primera parte
    if ',' in palabra_sin_numeros:
        palabra_sin_numeros = palabra_sin_numeros.split(',')[0]
    
    # Eliminar espacios en blanco al inicio y final
    palabra_limpia = palabra_sin_numeros.strip()
    
    # Convertir a mayúsculas manteniendo acentos
    return palabra_limpia.upper()

# Guarda las palabras relacionadas en un nuevo fichero .csv
def guardar_palabra_relacionada(palabra):
    output_file = "palabras_relacionadas.csv"
    fieldnames = ['word', 'status']
    
    # Limpiar la palabra antes de procesarla
    palabra = limpiar_palabra(palabra)
    
    # Verificar si el archivo existe
    file_exists = os.path.exists(output_file)
    
    # Verificar si la palabra ya existe en el archivo
    palabra_existe = False
    if file_exists:
        with open(output_file, 'r', encoding='utf-8') as file:
            reader = csv.DictReader(file)
            palabra_existe = any(row['word'] == palabra for row in reader)
    
    # Si la palabra no existe y no está vacía, añadirla al archivo
    if not palabra_existe and palabra:
        modo = 'a' if file_exists else 'w'
        with open(output_file, modo, encoding='utf-8', newline='') as file:
            writer = csv.DictWriter(file, fieldnames=fieldnames)
            if modo == 'w':
                writer.writeheader()
            writer.writerow({
                'word': palabra,
                'status': 'false'
            })
            print(f"Palabra relacionada '{palabra}' guardada en {output_file}")



def escribir_a_archivo(rows, fieldnames, output_path):
    modo = 'a' if os.path.exists(output_path) else 'w'
    with open(output_path, modo, encoding='utf-8', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=fieldnames, extrasaction='ignore', restval='null')
        if modo == 'w':
            writer.writeheader()
        writer.writerows(rows)

def actualizar_csv_original(input_path, processed_words):
    temp_file_path = input_path + ".tmp"
    
    # Leer todo el archivo original primero
    with open(input_path, 'r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        rows = list(reader)
        fieldnames = reader.fieldnames

    # Actualizar los estados
    for row in rows:
        palabra = row.get('word', '').strip().lower()
        # Buscar la palabra en las processed_words
        status_row = next((r for r in processed_words if r['word'].strip().lower() == palabra), None)
        if status_row:
            row['status'] = status_row['status']

    # Escribir el archivo actualizado
    with open(temp_file_path, 'w', encoding='utf-8', newline='') as outfile:
        writer = csv.DictWriter(outfile, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)

    # Reemplazar el archivo original
    shutil.move(temp_file_path, input_path)


def escribir_a_json(data, output_path):
    modo = 'a' if os.path.exists(output_path) else 'w'
    with open(output_path, modo, encoding='utf-8') as file:
        if modo == 'w':
            json.dump(data, file, ensure_ascii=False, indent=4)
        else:
            existing_data = []
            try:
                with open(output_path, 'r', encoding='utf-8') as infile:
                    existing_data = json.load(infile)
            except json.JSONDecodeError:
                pass
            existing_data.extend(data)
            with open(output_path, 'w', encoding='utf-8') as outfile:
                json.dump(existing_data, outfile, ensure_ascii=False, indent=4)



def procesar_archivo(input_path, output_json, batch_size=10):
    session = RetrySession()
    all_words = []
    processed_words = []

    with open(input_path, 'r', encoding='utf-8') as infile:
        reader = csv.DictReader(infile)
        
        for row in reader:
            palabra = row.get('word', '').strip().lower()
            status = row.get('status', '').strip().lower()
            
            if not palabra or status in ['true', 'null']:
                continue

            try:
                print(f"Procesando palabra: {palabra}")
                word_data = obtener_datos(palabra, session)
                
                # Nueva lógica para manejar los diferentes casos
                if word_data is None:
                    # Palabra no encontrada
                    new_status = 'null'
                    processed_words.append({
                        'word': palabra,
                        'status': new_status
                    })
                    actualizar_csv_original(input_path, processed_words)
                    print(f"Estado actualizado para la palabra: {palabra} -> {new_status}")
                elif word_data is False:
                    # Error en el procesamiento, mantener status 'false'
                    print(f"Error en el procesamiento de la palabra: {palabra}, manteniendo status 'false'")
                    continue
                else:
                    # Procesamiento exitoso
                    new_status = 'true'
                    processed_words.append({
                        'word': palabra,
                        'status': new_status
                    })
                    all_words.append(word_data)
                    
                    if len(all_words) % batch_size == 0:
                        escribir_a_json(all_words, output_json)
                        print(f"Procesadas {len(all_words)} palabras")
                        all_words.clear()
                    
                    actualizar_csv_original(input_path, processed_words)
                    print(f"Estado actualizado para la palabra: {palabra} -> {new_status}")

            except Exception as e:
                print(f"Error procesando palabra '{palabra}': {e}")
                # No modificamos el status en caso de error
                continue

        # Escribir las palabras restantes al JSON
        if all_words:
            escribir_a_json(all_words, output_json)
            print(f"Procesamiento completado. Total de palabras procesadas: {len(all_words)}")

            
if __name__ == "__main__":
    input_path = "./palabras.csv"
    output_path = "./palabras_definiciones.json"
    procesar_archivo(input_path, output_path, batch_size=10)
