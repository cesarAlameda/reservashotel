# 🏨 ReservaHotel API

**ReservaHotel** es una API RESTful desarrollada con Java y Spring Boot que gestiona reservas de hotel, habitaciones y servicios extra. Este proyecto me permitió profundizar en Spring Boot, JPA/Hibernate, PostgreSQL y Docker, sentando las bases para más adelante crear un frontend en Angular que la consuma.

---

## 📖 Descripción general

Esta API ofrece los endpoints básicos para:

- **CRUD** completo de usuarios, habitaciones, reservas y servicios.  
- Gestión de disponibilidad y asignación de servicios extra a reservas y habitaciones.  
- Roles de usuario (ADMIN, RECEPTIONIST, CLIENT) preparados para incorporar JWT y control de acceso en futuras versiones.

El objetivo es demostrar:
- Mi capacidad para diseñar y construir una API REST sólida con Spring Boot.  
- Buenas prácticas en manejo de transacciones, seguridad y despliegue en contenedores Docker.  

---

## ✨ Características principales

- **Gestión de Usuarios**: creación, lectura, actualización y borrado de usuarios.  
- **Gestión de Habitaciones**: tipos, precios, capacidad y disponibilidad.  
- **Reservas**: creación de reservas con fecha de entrada/salida y asignación de habitación.  
- **Servicios Extra**: definición y asociación dinámica de servicios (WiFi, desayuno, parking…).  
- **Roles y Seguridad**: estructura preparada para JWT y control de accesos según rol.  
- **Docker**: multi-stage build para compilar y empaquetar la aplicación en una imagen ligera.

---

## 🛠 Tecnologías utilizadas

- **Lenguaje**: Java 21  
- **Framework**: Spring Boot 3.x (Web, Data JPA, Security)  
- **Persistencia**: Hibernate / JPA + PostgreSQL (Supabase + PgBouncer)  
- **Build & Dependencias**: Maven  
- **Contenedores**: Docker (multi-stage builds)  
- **Control de versiones**: Git / GitHub

---

## 🚀 Instalación y uso

1. **Clona el repositorio**  
   ```bash
   git clone https://github.com/tu-usuario/reservashotel.git
   cd reservashotel

2. **Configura la conexión a BD**
Edita src/main/resources/application.properties con tu URL de Supabase y credenciales:
   ```bash
      properties
      Copy
      Edit
      spring.datasource.url=jdbc:postgresql://<host>:<puerto>/<db>?sslmode=require
      spring.datasource.username=<usuario>
      spring.datasource.password=<contraseña>
   
3. **Construye y etiqueta la imagen Docker**
   ```bash
   docker build -t reservashotel .
   
4. **Ejecuta el contenedor**
      ```bash
   docker run -d -p 9525:9525 --name rhotel reservashotel

5. **Prueba los endpoints**
   
   GET /user
   
   POST /user
   
   PUT /user/{id}
   
   DELETE /user/{id}
   
   De igual forma para /rooms, /bookings, /services, etc.







