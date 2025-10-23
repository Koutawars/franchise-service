# Franchise Service - Clean Architecture

Servicio de gestión de franquicias implementado con Clean Architecture, Spring Boot WebFlux y DynamoDB.

## Descripción

Este servicio permite gestionar franquicias, sucursales y productos con operaciones CRUD completas. Implementa programación reactiva con WebFlux y utiliza DynamoDB como base de datos NoSQL.

## Funcionalidades

- **Franquicias**: Crear y actualizar nombre
- **Sucursales**: Crear y actualizar nombre por franquicia
- **Productos**: CRUD completo con gestión de stock
- **Consultas**: Top productos por sucursal
- **Documentación**: API documentada con OpenAPI/Swagger

## Tecnologías

- Java 17
- Spring Boot 3.5.4
- Spring WebFlux (Programación Reactiva)
- DynamoDB
- Lombok
- OpenAPI/Swagger
- Gradle
- Docker
- Terraform

## Estructura del Proyecto

```
franchise-service/
├── applications/app-service/          # Configuración y punto de entrada
├── domain/
│   ├── model/                         # Entidades del dominio
│   └── usecase/                       # Casos de uso
├── infrastructure/
│   ├── driven-adapters/dynamo-db/     # Adaptador DynamoDB
│   ├── entry-points/reactive-web/     # Controladores REST
│   └── helpers/                       # Utilidades (logger, metrics)
├── deployment/                        # Docker y Terraform
└── docs/                             # Documentación
```

## API Endpoints

### Franquicias
- `POST /api/v1/franchises` - Crear franquicia
- `PATCH /api/v1/franchises/{franchiseId}/name` - Actualizar nombre

### Sucursales
- `POST /api/v1/franchises/{franchiseId}/branches` - Crear sucursal
- `PATCH /api/v1/franchises/{franchiseId}/branches/{branchId}/name` - Actualizar nombre

### Productos
- `POST /api/v1/franchises/{franchiseId}/branches/{branchId}/products` - Crear producto
- `DELETE /api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}` - Eliminar producto
- `PATCH /api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` - Actualizar stock
- `PATCH /api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name` - Actualizar nombre
- `GET /api/v1/franchises/{franchiseId}/top-products` - Top productos por sucursal

## Inicio Rápido

### Prerrequisitos
- Java 17+
- Variables de entorno AWS configuradas (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
- Las variables de AWS que se configura, tienen que tener permisos para lectura y escitura de la tabla de dynamodb. 

### Ejecución Local

1. **Clonar repositorio**
```bash
git clone <repository-url>
cd franchise-service
```

2. **Ejecutar aplicación**
```bash
gradle bootRun --args="--spring.profiles.active=local"
```

4. **Acceder a Swagger UI**
```
http://localhost:8080/swagger-ui.html
```

### Docker

1. **Construir JAR**
```bash
gradle build
```

2. **Construir imagen**
```bash
docker build -f deployment/docker/Dockerfile -t franchise-service .
```

3. **Ejecutar contenedor**
```bash
docker run -p 8080:8080 \
  -e AWS_ACCESS_KEY_ID=<tu-access-key> \
  -e AWS_SECRET_ACCESS_KEY=<tu-secret-key> \
  -e AWS_REGION=us-east-1 \
  franchise-service
```

## Configuración

### Variables de Entorno

- `AWS_REGION`: Región de AWS (default: us-east-1)
- `SERVER_PORT`: Puerto del servidor (default: 8080)

### Profiles

- `local`: Configuración para desarrollo local
- `prod`: Configuración para producción

## Testing

```bash
# Ejecutar tests
./gradlew test

# Reporte de cobertura
./gradlew jacocoTestReport
```

## Deployment

### Terraform

La infraestructura está definida en `deployment/terraform/`:

```bash
cd deployment/terraform
terraform init
terraform plan
terraform apply
```

## Arquitectura Clean Architecture

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

### Domain (Dominio)

Contiene la lógica de negocio pura:
- **Model**: Entidades del dominio (Franchise, Branch, Product)
- **UseCase**: Casos de uso con lógica de aplicación
- **Gateways**: Interfaces para acceso a datos

### Infrastructure (Infraestructura)

#### Entry Points
- **reactive-web**: Controladores REST con WebFlux

#### Driven Adapters
- **dynamo-db**: Implementación del repositorio con DynamoDB

#### Helpers
- **logger**: Utilidades de logging
- **metrics**: Métricas de aplicación

### Application (Aplicación)

Punto de entrada y configuración:
- Configuración de Spring Boot
- Inyección de dependencias
- Configuración de beans

## Patrones Implementados

- **Clean Architecture**: Separación de responsabilidades
- **Repository Pattern**: Abstracción de acceso a datos
- **Dependency Inversion**: Inversión de dependencias
- **Reactive Programming**: Programación reactiva con Mono/Flux

## Monitoreo

- Logs estructurados con contexto de trazabilidad
- Métricas de aplicación
- Health checks

## Gestión del Despliegue en AWS

### Subir Cambios de Código

Para actualizar la aplicación desplegada en AWS con nuevos cambios:

```bash
# 1. Compilar el código
gradle clean build

# 2. Construir nueva imagen Docker
docker build -f deployment/docker/Dockerfile -t franchise-service .

# 3. Etiquetar para ECR
docker tag franchise-service:latest <aws-account-id>.dkr.ecr.us-east-1.amazonaws.com/franchise-service:latest

# 4. Autenticarse en ECR (usar CMD, no PowerShell)
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <aws-account-id>.dkr.ecr.us-east-1.amazonaws.com

# 5. Subir imagen a ECR
docker push <aws-account-id>.dkr.ecr.us-east-1.amazonaws.com/franchise-service:latest

# 6. Forzar nuevo despliegue en ECS
aws ecs update-service --cluster franchise-service-cluster --service franchise-service-service --force-new-deployment --region us-east-1
```

### Gestión del Servicio

**Detener el servicio (mantiene infraestructura):**
```bash
aws ecs update-service --cluster franchise-service-cluster --service franchise-service-service --desired-count 0 --region us-east-1
```

**Reiniciar el servicio:**
```bash
aws ecs update-service --cluster franchise-service-cluster --service franchise-service-service --desired-count 1 --region us-east-1
```

**Verificar estado del servicio:**
```bash
aws ecs describe-services --cluster franchise-service-cluster --services franchise-service-service --region us-east-1 --query "services[0].{RunningCount:runningCount,DesiredCount:desiredCount}"
```

**Ver logs de la aplicación:**
```bash
aws logs tail /ecs/franchise-service --follow --region us-east-1
```

### Destruir Infraestructura Completa

**⚠️ CUIDADO: Esto elimina todo permanentemente**
```bash
cd deployment/terraform
terraform destroy
```

### URLs de Acceso

- **API Gateway**: `https://<api-gateway-id>.execute-api.us-east-1.amazonaws.com/dev`
- **ALB Directo**: `http://<alb-dns-name>.us-east-1.elb.amazonaws.com`

### Troubleshooting

**Si las tareas fallan al iniciar:**
1. Verificar logs: `aws logs tail /ecs/franchise-service --since 10m --region us-east-1`
2. Verificar health checks del ALB
3. Confirmar que la imagen se subió correctamente a ECR

**Si hay múltiples tareas fallando:**
```bash
# Detener todas las tareas
aws ecs update-service --cluster franchise-service-cluster --service franchise-service-service --desired-count 0 --region us-east-1

# Esperar y reiniciar
aws ecs update-service --cluster franchise-service-cluster --service franchise-service-service --desired-count 1 --region us-east-1
```

## Contribución

1. Fork del proyecto
2. Crear feature branch
3. Commit cambios
4. Push al branch
5. Crear Pull Request

## Documentación Adicional

- [Swagger Documentation](docs/SWAGGER_DOCUMENTATION.md)
- [Clean Architecture Article](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)