import os
from pathlib import Path
from dotenv import load_dotenv

# Cargar el .env del microservicio
env_path = Path(__file__).resolve().parent.parent / ".env"
load_dotenv(dotenv_path=env_path, override=True)

# Variables de entorno con valores por defecto
EUREKA_CLIENT_SERVICE = os.getenv("EUREKA_CLIENT_SERVICE", "http://localhost:8761/eureka")
SERVICE_PORT = int(os.getenv("SERVICE_PORT", 8090))
INSTANCE_HOST = os.getenv("INSTANCE_HOST", "localhost")

# Opcional: imprimir para depuración
print(f"🔧 Eureka Client Service: {EUREKA_CLIENT_SERVICE}")
print(f"🚀 Servicio corriendo en {INSTANCE_HOST}:{SERVICE_PORT}")
