from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from app import procesar_palabra
from app.config import EUREKA_CLIENT_SERVICE, SERVICE_PORT
import py_eureka_client.eureka_client as eureka_client
import uuid
from contextlib import asynccontextmanager
import socket
import logging

# ---------------- Configuración de logging ---------------- #
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("scraper_microservice")

# ---------------- Preparar instancia ---------------- #
INSTANCE_ID = f"scraping-microservice:{uuid.uuid1()}"
INSTANCE_HOSTNAME = socket.gethostname()

logger.info(f"🛰️ Eureka Client Service: {EUREKA_CLIENT_SERVICE}")
logger.info(f"🛰️ Instance hostname: {INSTANCE_HOSTNAME}")

# ---------------- Registro en Eureka ---------------- #
async def register_with_eureka():
    try:
        await eureka_client.init_async(
            eureka_server=EUREKA_CLIENT_SERVICE,
            app_name="scraping-microservice",
            instance_port=SERVICE_PORT,
            instance_host=INSTANCE_HOSTNAME,
            instance_id=INSTANCE_ID,
        )
        logger.info(f"✅ Registrado en Eureka como {INSTANCE_ID}")
    except Exception as e:
        logger.error(f"❌ Error al registrar con Eureka: {e}")

# ---------------- Lifespan de la app ---------------- #
@asynccontextmanager
async def lifespan(app: FastAPI):
    await register_with_eureka()
    yield

# ---------------- Crear app FastAPI ---------------- #
app = FastAPI(
    title="Microservicio Scraper",
    description="Procesa palabras contra la RAE",
    version="1.0.0",
    lifespan=lifespan
)

# ---------------- Modelos ---------------- #
class PalabraRequest(BaseModel):
    word: str

# ---------------- Rutas ---------------- #
@app.post("/procesar-palabra")
async def procesar_palabra_endpoint(request: PalabraRequest):
    palabra = request.word.strip()
    resultado = procesar_palabra(palabra)

    if resultado is None:
        raise HTTPException(status_code=404, detail=f"La palabra '{palabra}' no fue encontrada en RAE.")
    elif resultado is False:
        raise HTTPException(status_code=500, detail=f"Error al procesar la palabra '{palabra}'.")
    
    return resultado

@app.get("/")
async def root():
    return {"message": "✅ Microservicio de validación de palabras RAE activo"}
