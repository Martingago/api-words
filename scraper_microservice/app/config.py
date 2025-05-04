import os
from pathlib import Path
from dotenv import load_dotenv

# Cargar el .env del microservicio solo si existe
env_path = Path(__file__).resolve().parent.parent / ".env"
if env_path.exists():
    load_dotenv(dotenv_path=env_path, override=True)  # Override en local
    print(f"📦 .env del microservicio cargado desde: {env_path}")
else:
    print("🐳 Ejecutando en entorno Docker, usando variables del contenedor.")

# Variables de entorno con valores por defecto
EUREKA_CLIENT_SERVICE = os.getenv("EUREKA_CLIENT_SERVICE")
SERVICE_PORT = int(os.getenv("SERVICE_PORT"))
