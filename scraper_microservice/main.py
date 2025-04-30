# main.py
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from scraper import procesar_palabra
import py_eureka_client.eureka_client as eureka_client
import uvicorn
import uuid
import socket
from contextlib import asynccontextmanager
from dotenv import load_dotenv
import os

load_dotenv()

EUREKA_CLIENT_SERVICE = os.getenv('EUREKA_CLIENT_SERVICE')
SERVICE_PORT= int(os.getenv('SERVICE_PORT'))
INSTANCE_HOST = socket.gethostname()

print(f"Host: {INSTANCE_HOST}")
@asynccontextmanager
async def lifespan(app: FastAPI):
    # Código que se ejecuta al inicio
    await register_with_eureka()
    yield
    
app = FastAPI(lifespan=lifespan)

# Configuración del cliente Eureka
async def register_with_eureka():
    await eureka_client.init_async(
        eureka_server=EUREKA_CLIENT_SERVICE,
        app_name="scraping-microservice",
        instance_port=SERVICE_PORT, 
        instance_host=INSTANCE_HOST,
        instance_id= f"scraping-microservice:{uuid.uuid1()}"
    )

# Modelo de datos para la solicitud (opcional, pero recomendado)
class PalabraRequest(BaseModel):
    word: str

# Ruta para procesar una palabra
@app.post("/procesar-palabra")
async def procesar_palabra_endpoint(request: PalabraRequest):
    palabra = request.word
    resultado = procesar_palabra(palabra)

    if resultado is None:
        raise HTTPException(status_code=404, detail=f"La palabra '{palabra}' no fue encontrada en RAE.")
    elif resultado is False:
        raise HTTPException(status_code=500, detail=f"Error al procesar la palabra '{palabra}'.")
    else:
        return resultado

# Ruta de prueba para verificar que el microservicio está funcionando
@app.get("/")
async def root():
    return {"message": "Microservicio de validación de palabras RAE"}


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=SERVICE_PORT)