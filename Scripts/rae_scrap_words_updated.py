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

            # Extrae la calificación
            first_div = definition_div.find('div')
            calification_word = first_div.find('abbr')['title'] if first_div and first_div.find('abbr') else None

            # Construir la definición incluyendo texto plano y spans con data-id
            definition_parts = []
            examples = []
            definition_word = None  # Inicializar la variable de definición
            if first_div:
                for content in first_div.contents:
                    text = ''
                    if isinstance(content, str):  # Texto plano (incluye signos de puntuación)
                        text = content.strip()
                    elif content.name == 'span' and content.has_attr('data-id'):  # Solo spans con data-id
                        text = content.get_text(strip=True)
                    elif content.name == 'a':  # Enlaces <a>
                        text = formatear_palabra(content.get_text(strip=True)) + " "
                    elif content.name == 'span' and 'h' in content.get('class', []):  # Ejemplo con clase "h"
                        # Extraer texto incluyendo signos de puntuación y espacios entre spans
                        example_text_parts = []
                        for span_content in content.contents:
                            if isinstance(span_content, str):  # Texto plano dentro del span "h"
                                example_text_parts.append(span_content.strip())
                            elif span_content.name == 'span':  # Subspans dentro del span "h"
                                example_text_parts.append(span_content.get_text(strip=True))

                        # Construir el texto del ejemplo con espacios
                        example_text = ''.join(
                            f" {part}" if part not in ".,;:" else part
                            for part in example_text_parts
                        ).strip()  # Eliminar espacio inicial

                        if example_text:
                            examples.append(example_text)
                   
                    if text:
                        # Si no es el primer elemento y el texto actual no empieza con puntuación cerrada
                        if definition_parts and not text[0] in ")]}',.:;":
                            definition_parts.append(' ')
                        
                        definition_parts.append(text)
                        
                        # Si el texto termina con punto, coma, o punto y coma, no agregar espacio adicional
                        if text.endswith(('.', ';', ':')):
                            continue
                
                # Si se han encontrado partes de definición, construir la definición
            if definition_parts:
                definition_word = ''.join(definition_parts).strip()
            else:
                    # Si no hay definición en spans con data-id, buscar un enlace <a> y extraer su texto
                link_element = first_div.find('a', class_='a')
                if link_element:
                    definition_word = formatear_palabra(link_element.get_text(strip=True))


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
                "qualification": calification_word,
                "definition": definition_word,
                "synonyms": sinonimos,
                "antonyms": antonimos,
                "examples": examples
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
    output_file = related_path
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
            status = row.get('status', '')
            status = status.strip().lower() if status is not None else ''
            
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
                    
                # Sincronizar CSV y JSON cada batch_size acciones
                if len(processed_words) % batch_size == 0:
                    # Escribir en JSON
                    if all_words:
                        escribir_a_json(all_words, output_json)
                        print(f"Procesadas {len(all_words)} palabras, JSON actualizado.")
                        all_words.clear()
                    
                    # Actualizar el CSV
                    actualizar_csv_original(input_path, processed_words)
                    print(f"CSV actualizado después de procesar {len(processed_words)} palabras.")

            except Exception as e:
                print(f"Error procesando palabra '{palabra}': {e}")
                # No modificamos el status en caso de error
                continue

        # Escribir las palabras restantes al JSON y CSV al finalizar
        if all_words:
            escribir_a_json(all_words, output_json)
            print(f"JSON actualizado con las palabras restantes.")
        if processed_words:
            actualizar_csv_original(input_path, processed_words)
            print(f"CSV actualizado con los datos restantes.")


            
if __name__ == "__main__":
    input_path = "./words_placeholders.csv"
    output_path = "./palabras_definiciones_11.json"
    related_path = "./palabras_relacionadas.csv"
    procesar_archivo(input_path, output_path, batch_size=50)
