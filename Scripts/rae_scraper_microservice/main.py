# api.py
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from scraper import procesar_palabra

# Crear una instancia de FastAPI
app = FastAPI()

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

# Ejecutar el servidor (esto se usa solo para desarrollo)
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)