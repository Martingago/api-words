import csv

def process_csv(input_file, output_file):
    temp_file = "temp_file.csv"
    column_to_check = "definition"  # La columna que quieres procesar
    new_delimiter = "#"  # Nuevo delimitador temporal

    # Paso 1: Cambiar delimitador al temporal y procesar la columna
    with open(input_file, "r", encoding="utf-8") as infile, open(temp_file, "w", encoding="utf-8", newline="") as tempfile:
        reader = csv.DictReader(infile)  # Leer como diccionario para procesar por nombre de columna
        fieldnames = reader.fieldnames
        writer = csv.DictWriter(tempfile, fieldnames=fieldnames, delimiter=new_delimiter, quoting=csv.QUOTE_MINIMAL)
        writer.writeheader()

        for row in reader:
            # Verificar y ajustar la columna de interés
            definition = row[column_to_check]
            if not (definition.startswith('"') and definition.endswith('"')):  # Si no tiene comillas dobles
                row[column_to_check] = f'"{definition}"'  # Encerrar en comillas dobles
            writer.writerow(row)

    # Paso 2: Reemplazar el delimitador temporal con una coma
    with open(temp_file, "r", encoding="utf-8") as tempfile, open(output_file, "w", encoding="utf-8", newline="") as outfile:
        reader = csv.reader(tempfile, delimiter=new_delimiter)
        writer = csv.writer(outfile, delimiter=",", quoting=csv.QUOTE_MINIMAL)
        for row in reader:
            writer.writerow(row)

    print(f"Archivo procesado guardado en: {output_file}")

# Uso del script
input_file = "archivo_corregido.csv"  # Ruta del archivo original
output_file = "archivo_corregido_2.csv"  # Ruta del archivo corregido
process_csv(input_file, output_file)
