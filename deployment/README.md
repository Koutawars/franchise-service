# Guía de Despliegue

## Descripción de la Arquitectura

Este despliegue utiliza recursos del free tier de AWS:

- **ECR**: Registro de contenedores (500 MB de almacenamiento gratis)
- **ECS Fargate**: Orquestación de contenedores (free tier: 20 GB-horas por mes)
- **ALB**: Application Load Balancer
- **API Gateway**: Endpoint REST API (1M peticiones gratis por mes)
- **DynamoDB**: Base de datos NoSQL (25 GB de almacenamiento gratis)
- **VPC**: Virtual Private Cloud con 2 subnets públicas

## Prerrequisitos

1. AWS CLI configurado con credenciales apropiadas
2. Docker instalado
3. Terraform instalado
4. Java 17+ y Gradle

## Pasos de Despliegue

### 1. Desplegar Infraestructura

```bash
cd deployment/terraform
terraform init
terraform plan
terraform apply
```

### 2. Construir y Subir Imagen Docker

```bash
cd deployment/scripts
chmod +x build-and-push.sh
./build-and-push.sh
```

### 3. Actualizar Servicio ECS

```bash
aws ecs update-service \
  --cluster franchise-service-cluster \
  --service franchise-service-service \
  --force-new-deployment \
  --region us-east-1
```

### 4. Despliegue con Un Solo Comando

```bash
cd deployment/scripts
chmod +x deploy.sh
./deploy.sh
```

## Puntos de Acceso

Después del despliegue, tendrás dos puntos de acceso:

1. **API Gateway**: `https://{api-id}.execute-api.us-east-1.amazonaws.com/dev`
2. **ALB Directo**: `http://{alb-dns-name}`

## Consideraciones del Free Tier

- **ECS Fargate**: 256 CPU, 512 MB memoria (mínimo para free tier)
- **ALB**: Se aplican cargos después de los límites del free tier
- **API Gateway**: 1M peticiones/mes gratis
- **DynamoDB**: 25 GB almacenamiento, 25 RCU/WCU gratis
- **ECR**: 500 MB almacenamiento gratis

## Monitoreo

- Logs de CloudWatch: `/ecs/franchise-service`
- Métricas del servicio ECS en CloudWatch
- Health checks del target group del ALB

## Limpieza

```bash
cd deployment/terraform
terraform destroy
```

## Solución de Problemas

1. **Tarea ECS no inicia**: Revisar logs de CloudWatch
2. **Health check fallando**: Verificar endpoint `/actuator/health`
3. **API Gateway 502**: Revisar salud del target group del ALB
4. **Acceso a DynamoDB**: Verificar roles y políticas IAM