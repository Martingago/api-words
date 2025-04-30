import sys
import json
from scraper import procesar_palabra

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Uso: python main.py <palabra>")
        sys.exit(1)

    palabra = sys.argv[1]
    resultado = procesar_palabra(palabra)

    if resultado:
        print(json.dumps(resultado, indent=4, ensure_ascii=False))
    else:
        print(f"No se pudo procesar la palabra '{palabra}'.")