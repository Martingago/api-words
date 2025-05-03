# Usamos una imagen base de Python
FROM python:3.13.3-alpine	

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar solo lo necesario primero (para mejorar cacheo)
COPY requirements.txt .

# Instalar las dependencias
RUN pip install --no-cache-dir -r requirements.txt

# Copiar el resto del código
COPY . .

# Exponer el puerto del servicio (ajustado al que uses)
EXPOSE 8090

# Comando para ejecutar la aplicación con Uvicorn (run.py lo maneja)
CMD ["python", "run.py"]
