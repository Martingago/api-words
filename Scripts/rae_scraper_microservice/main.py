# api.py
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from scraper import procesar_palabra
import py_eureka_client.eureka_client as eureka_client
import uuid
import socket
import uvicorn

# Crear una instancia de FastAPI
app = FastAPI()

# Configuración del cliente Eureka
async def register_with_eureka():
    port = get_free_port()
    instance_id = f"scraping-microservice:{uuid.uuid4()}"

    await eureka_client.init_async(
        eureka_server="http://localhost:8761/eureka",  # URL del servidor Eureka
        app_name="rae-microservice",               # Nombre único del servicio
        instance_port=port,                           # Puerto en el que corre el microservicio
        instance_host="localhost",                    # Host del microservicio
        instance_id = instance_id
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

# Registrar el microservicio en Eureka al iniciar
@app.on_event("startup")
async def startup_event():
    await register_with_eureka()

def get_free_port():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind(('0.0.0.0', 0))  # Asigna un puerto disponible
        return s.getsockname()[1]  # Devuelve el puerto asignado


if __name__ == "__main__":
    port = get_free_port()  # Obtén el puerto dinámico
    uvicorn.run(app, host="0.0.0.0", port=port)