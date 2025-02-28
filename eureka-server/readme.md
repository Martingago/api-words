# Servidor de Eureka Server

Servidor de eureka Server destinado a gestionar el servicio principal y los diferentes microservicios.


## Crear contenedor

Comandos para crear un contenedor Docker de eureka: 
```bash
docker build  -t rae-eureka-server .
docker run -d -P --name rae-eureka-server rae-eureka-server
```

> **NOTA**: El API y el microservicio de Scrapping están vinculados a un servicio de Eureka. Es necesario que este servicio de Eureka esté funcionando para que el resto se servicios se unan y funcionen correctamente.