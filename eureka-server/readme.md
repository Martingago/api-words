# 🌐 Servidor Eureka – WordRadar

Este servicio actúa como **servidor de descubrimiento** para la arquitectura de microservicios de WordRadar.

Permite que el **API principal** y el **microservicio de Scraping** se comuniquen y se registren dinámicamente en un entorno orquestado.

---

## 📦 Contenedor Docker

Puedes crear y ejecutar el Eureka Server fácilmente con:

```bash
docker build -t rae-eureka-server .
docker run -d -P --name rae-eureka-server rae-eureka-server
```

> ✅ Asegúrate de que el puerto 8761 esté accesible desde los servicios que intentan registrarse.

--- 

## 🛠️ Requisitos previos
- Java 21+
- Spring Boot 3.4.2
- Spring Cloud Eureka Server

---

## 🔗 URL por defecto
Una vez iniciado correctamente, el servidor estará disponible en:

```bash
http://localhost:8761
```

Aquí podrás visualizar la interfaz de Eureka y confirmar que los microservicios (como rae-api-words o rae-scraper-service) están registrados correctamente.

---

## 🚀 Servicios que dependen de Eureka

| Servicio	            | Descripción                           |
|----------------------|---------------------------------------|
| rae-api-words        | 	API REST principal de WordRadar      |
| rae-scraper-service	 | Microservicio de scraping de palabras |

---

## 🧪 Prueba rápida en local

Para realizar pruebas en local, dirígete a la raíz del repositorio de eureka-server y ejecuta:

```bash
./mvnw spring-boot:run
```
Luego visita http://localhost:8761 para confirmar que el servidor está operativo.

---

## 🔐 Seguridad
Este servicio está abierto por defecto para facilitar el descubrimiento de servicios en desarrollo. Si lo despliegas en producción, considera proteger la interfaz con autenticación básica o restricciones por IP.

---

## 🧐 Créditos

Desarrollado por [Martín Gago](https://martingago.dev/) como parte del proyecto WordRadar  
Inspirado en la necesidad de un diccionario español accesible, potente y documentado.

---

## 📄 Licencia

### MIT License

Copyright © 2025 [martingago.dev](https://martingago.dev/)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and 
associated documentation files (the “Software”), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial 
portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.