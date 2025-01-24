import csv
import os
import re

def limpiar_palabra(palabra):
    """
    Limpia la palabra de números, sufijos de género y caracteres especiales.
    También maneja palabras con comillas.
    
    Args:
        palabra (str): Palabra a limpiar
    
    Returns:
        str: Palabra limpia en mayúsculas
    """
    # Eliminar comillas si existen
    palabra = palabra.strip('"')
    
    # Eliminar números de la palabra
    palabra_sin_numeros = ''.join(char for char in palabra if not char.isdigit())
    
    # Si la palabra contiene una coma, tomar solo la primera parte
    if ',' in palabra_sin_numeros:
        palabra_sin_numeros = palabra_sin_numeros.split(',')[0]
    
    # Eliminar guiones y otros caracteres especiales
    # Mantenemos letras, espacios y caracteres acentuados
    palabra_limpia = re.sub(r'[^\w\sáéíóúÁÉÍÓÚüÜñÑ]', '', palabra_sin_numeros)
    
    # Eliminar espacios en blanco al inicio y final
    palabra_limpia = palabra_limpia.strip()
    
    # Convertir a mayúsculas manteniendo acentos
    return palabra_limpia.upper()

def limpiar_csv(input_file, output_file):
    """
    Lee un archivo CSV, limpia las palabras y guarda en un nuevo archivo.
    
    Args:
        input_file (str): Ruta del archivo CSV de entrada
        output_file (str): Ruta del archivo CSV de salida
    """
    # Crear un archivo temporal para guardar los resultados
    temp_file = output_file + '.tmp'
    
    try:
        with open(input_file, 'r', encoding='utf-8') as infile, \
             open(temp_file, 'w', encoding='utf-8', newline='') as outfile:
            
            reader = csv.DictReader(infile)
            writer = csv.DictWriter(outfile, fieldnames=['word', 'status'])
            
            # Escribir el encabezado
            writer.writeheader()
            
            # Conjunto para mantener registro de palabras ya procesadas
            palabras_procesadas = set()
            
            # Procesar cada fila
            for row in reader:
                palabra_original = row['word']
                palabra_limpia = limpiar_palabra(palabra_original)
                
                # Solo agregar si la palabra no está vacía y no ha sido procesada
                if palabra_limpia and palabra_limpia not in palabras_procesadas:
                    writer.writerow({
                        'word': palabra_limpia,
                        'status': 'false'
                    })
                    palabras_procesadas.add(palabra_limpia)
                    print(f"Procesado: {palabra_original} -> {palabra_limpia}")
        
        # Si todo sale bien, reemplazar el archivo original con el temporal
        if os.path.exists(output_file):
            os.remove(output_file)
        os.rename(temp_file, output_file)
        print(f"\nProceso completado. Archivo guardado como: {output_file}")
        print(f"Total de palabras únicas procesadas: {len(palabras_procesadas)}")
        
    except Exception as e:
        print(f"Error durante el procesamiento: {e}")
        # Limpiar el archivo temporal si existe
        if os.path.exists(temp_file):
            os.remove(temp_file)

if __name__ == "__main__":
    # Archivos de entrada y salida
    input_file = "../palabras_relacionadas_save.csv"  # Cambia esto al nombre de tu archivo de entrada
    output_file = "palabras_relacionadas_limpias.csv"
    
    limpiar_csv(input_file, output_file)