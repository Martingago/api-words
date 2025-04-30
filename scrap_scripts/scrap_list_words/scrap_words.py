import requests
from bs4 import BeautifulSoup
import csv
import os
import argparse
from datetime import datetime

def process_page(url):
    print(f"Procesando página: {url}")
    response = requests.get(url)
    response.encoding = 'utf-8'

    if response.status_code == 200:
        soup = BeautifulSoup(response.text, "html.parser")
        span = soup.find("span", class_="mt")
        if span:
            words = [word.strip() for word in span.text.split()]
            return words
        else:
            print(f"⚠️ No se encontró el <span> en {url}.")
            return []
    else:
        print(f"❌ Error al acceder a la página {url}: {response.status_code}")
        return []

def save_words(words, length):
    timestamp = datetime.now().strftime("%Y%m%d-%H%M%S")
    filename = f"result-words-length{length}-{timestamp}.csv"
    output_dir = "output"
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, filename)

    with open(output_path, mode="w", newline="", encoding="utf-8") as file:
        writer = csv.writer(file)
        writer.writerow(["word"])
        for word in words:
            writer.writerow([word])

    print(f"✅ Se han guardado {len(words)} palabras en '{output_path}'")

def main(length, num_pages):
    all_words = []

    # Página 1 (URL especial)
    page1_url = f"https://www.listasdepalabras.es/palabras{length}letras.htm"
    all_words.extend(process_page(page1_url))

    # Páginas 2 a N
    base_url = f"https://www.listasdepalabras.es/palabras{length}letraspagina{{}}.htm"
    for page in range(2, num_pages + 1):
        all_words.extend(process_page(base_url.format(page)))

    save_words(all_words, length)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Scrapeador parametrizable de listasdepalabras.es")
    parser.add_argument("word_length", type=int, help="Número de letras de las palabras (ej: 8)")
    parser.add_argument("paginas", type=int, help="Número total de páginas a scrapear (mínimo 1)")
    args = parser.parse_args()

    main(args.word_length, args.paginas)
    # Ejemplo de uso: python scrap_words.py 8 10
    # Esto generará un archivo CSV con las palabras de 8 letras de las primeras 10 páginas.