def limpiar_palabra(palabra):
    palabra_sin_numeros = ''.join(char for char in palabra if not char.isdigit())
    # Si la palabra contiene una coma, tomar solo la primera parte
    if ',' in palabra_sin_numeros:
        palabra_sin_numeros = palabra_sin_numeros.split(',')[0]

    # Eliminar espacios en blanco al inicio y final
    palabra_limpia = palabra_sin_numeros.strip()

    return palabra_limpia.lower()

def formatear_palabra(palabra):
    palabra_sin_numeros = ''.join(char for char in palabra if not char.isdigit())
    return palabra_sin_numeros