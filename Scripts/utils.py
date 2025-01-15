import csv

# Leer el archivo CSV y añadir la nueva columna
with open('palabras_5_letras.csv', 'r') as archivo_entrada:
    lector = csv.DictReader(archivo_entrada)
    filas = [fila for fila in lector]
    cabeceras = lector.fieldnames + ['status']

# Escribir el nuevo archivo CSV con la columna añadida
with open('palabras_test.csv', 'w', newline='') as archivo_salida:
    escritor = csv.DictWriter(archivo_salida, fieldnames=cabeceras)
    escritor.writeheader()
    for fila in filas:
        fila['status'] = "false"
        escritor.writerow(fila)
