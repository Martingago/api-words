# Usamos una imagen base de Python
FROM python:3.12.9-alpine3.21

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos los archivos necesarios al contenedor
COPY . .

# Instalamos las dependencias
RUN pip install --no-cache-dir -r requirements.txt

# Exponemos el puerto que usará FastAPI
EXPOSE 8000

# Comando para ejecutar la aplicación
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
