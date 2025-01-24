import requests
from bs4 import BeautifulSoup
import csv

# Base URL para las páginas 2 a 22
base_url = "https://www.listasdepalabras.es/palabras5letraspagina{}.htm"

# URL para la página 1
page1_url = "https://www.listasdepalabras.es/palabras5letras.htm"

# Archivo CSV donde guardar las palabras
output_file = "palabras_5_letras.csv"

# Lista para almacenar las palabras
all_words = []

# Función para procesar una página
def process_page(url):
    print(f"Procesando página: {url}")
    response = requests.get(url)
    # Configuramos explícitamente la codificación a UTF-8
    response.encoding = 'utf-8'
    
    if response.status_code == 200:
        # Especificamos el parser y la codificación
        soup = BeautifulSoup(response.text, "html.parser", from_encoding='utf-8')
        # Buscamos el <span> con la clase 'mt'
        span = soup.find("span", class_="mt")
        if span:
            # Obtenemos el texto y lo separamos en palabras
            words = span.text.split()
            # Limpiamos posibles caracteres problemáticos
            words = [word.strip() for word in words]
            return words
        else:
            print(f"No se encontró el span en la página {url}.")
            return []
    else:
        print(f"Error al acceder a la página {url}: {response.status_code}")
        return []

# Procesamos la página 1
all_words.extend(process_page(page1_url))

# Procesamos las páginas 2 a 22
for page in range(2, 23):  # Desde la página 2 hasta la 22
    all_words.extend(process_page(base_url.format(page)))

# Escribimos las palabras en un archivo CSV
with open(output_file, mode="w", newline="", encoding="utf-8") as file:
    writer = csv.writer(file)
    writer.writerow(["Palabras"])  # Encabezado del CSV
    for word in all_words:
        writer.writerow([word])

print(f"Se han guardado {len(all_words)} palabras en el archivo '{output_file}'.")
