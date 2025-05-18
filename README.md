# Odoonto - Sistema de Gestión para Clínicas Dentales

## Descripción

Odoonto es un Producto Mínimo Viable (MVP) para la gestión integral de clínicas dentales. El sistema está dividido en dos componentes principales:

- **Odoonto.Back**: Backend desarrollado con Spring Boot siguiendo una arquitectura Domain-Driven Design (DDD)
- **Odoonto.Front**: Frontend desarrollado con React y Vite

## Documentación

### Backend (Odoonto.Back)

- [Documentación principal del Backend](Odoonto.Back/README.md)
  - [Capa de Dominio](Odoonto.Back/src/main/java/odoonto/domain/README.md) - Entidades, agregados y reglas de negocio
  - [Capa de Aplicación](Odoonto.Back/src/main/java/odoonto/application/README.md) - Casos de uso e implementación
  - [Capa de Infraestructura](Odoonto.Back/src/main/java/odoonto/infrastructure/README.md) - Persistencia y servicios externos
  - [Capa de Presentación](Odoonto.Back/src/main/java/odoonto/presentation/README.md) - API REST y controladores

### Frontend (Odoonto.Front)

- [Documentación del Frontend](Odoonto.Front/README.md)

## Iniciar la aplicación

Para iniciar toda la aplicación (backend y frontend), puede utilizar el script PowerShell incluido:

```powershell
.\iniciar-aplicacion.ps1
```

## Desarrollo

Para instrucciones detalladas sobre cómo configurar y desarrollar cada componente, consulte los READMEs específicos de cada módulo. 