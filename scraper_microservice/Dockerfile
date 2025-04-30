# Usamos una imagen base de Python
FROM python:3.13.3-alpine	

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos los archivos necesarios al contenedor
COPY . .

# Instalamos las dependencias
RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 8090

# Comando para ejecutar la aplicación
CMD ["python", "main.py"]
