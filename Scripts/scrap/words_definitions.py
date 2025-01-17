import csv
import requests

# Configuración de la URL base del API
base_url = "https://api.yourdictionary.com/wordfinder/v1/es/definitions/{}"

# Nombre del archivo de entrada y salida
input_file = "palabras_5_letras.csv"
output_file = "palabras_definiciones.csv"

def fetch_word_data(word):
    """Realiza la petición a la API y devuelve los datos de interés."""
    url = base_url.format(word.lower())
    try:
        response = requests.get(url)
        response.raise_for_status()  # Levanta un error si la respuesta no es 200
        data = response.json()
        if data.get("status") == 200 and data.get("data"):
            for pos_data in data["data"]:
                pos = pos_data.get("pos", [])
                for part in pos:
                    calificacion = part.get("part", "")
                    senses = part.get("senses", [])
                    for sense in senses:
                        subsenses = sense.get("subsenses", [])
                        if subsenses:
                            definition = subsenses[0].get("txt", "")
                            return calificacion, definition
        return None, None
    except Exception as e:
        print(f"Error fetching data for word '{word}': {e}")
        return None, None

def process_csv(input_file, output_file):
    """Lee un archivo CSV, consulta la API para cada palabra y escribe los resultados en un nuevo archivo."""
    with open(input_file, mode="r", encoding="utf-8") as infile, \
         open(output_file, mode="w", encoding="utf-8", newline="") as outfile:

        reader = csv.reader(infile)
        writer = csv.writer(outfile)

        # Leer encabezado y añadir columnas nuevas
        header = next(reader)
        writer.writerow(header + ["status", "calification", "definition", "length", "language"])

        for row in reader:
            if not row:  # Saltar filas vacías
                continue

            word = row[0]
            status = row[1].lower() == "true" if len(row) > 1 and row[1] else False

            if not status:  # Solo procesar si el status es False o está vacío
                calificacion, definition = fetch_word_data(word)
                status = bool(calificacion and definition)
            else:
                calificacion, definition = None, None

            length = len(word)
            language = "esp"

            writer.writerow(row[:2] + ["true" if status else "false", calificacion if calificacion else "N/A", definition if definition else "N/A", length, language])
            print("word: '" + word + "' successfully scraped and added to the csv")

if __name__ == "__main__":
    process_csv(input_file, output_file)
    print(f"Procesamiento completo. Archivo generado: {output_file}")
