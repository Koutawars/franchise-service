# Deployment Guide

## Architecture Overview

This deployment uses AWS free tier resources:

- **ECR**: Container registry (500 MB storage free)
- **ECS Fargate**: Container orchestration (free tier: 20 GB-hours per month)
- **ALB**: Application Load Balancer
- **API Gateway**: REST API endpoint (1M requests free per month)
- **DynamoDB**: NoSQL database (25 GB storage free)
- **VPC**: Virtual Private Cloud with 2 public subnets

## Prerequisites

1. AWS CLI configured with appropriate credentials
2. Docker installed
3. Terraform installed
4. Java 17+ and Gradle

## Deployment Steps

### 1. Deploy Infrastructure

```bash
cd deployment/terraform
terraform init
terraform plan
terraform apply
```

### 2. Build and Push Docker Image

```bash
cd deployment/scripts
chmod +x build-and-push.sh
./build-and-push.sh
```

### 3. Update ECS Service

```bash
aws ecs update-service \
  --cluster franchise-service-cluster \
  --service franchise-service-service \
  --force-new-deployment \
  --region us-east-1
```

### 4. One-Command Deployment

```bash
cd deployment/scripts
chmod +x deploy.sh
./deploy.sh
```

## Access Points

After deployment, you'll have two access points:

1. **API Gateway**: `https://{api-id}.execute-api.us-east-1.amazonaws.com/dev`
2. **ALB Direct**: `http://{alb-dns-name}`

## Free Tier Considerations

- **ECS Fargate**: 256 CPU, 512 MB memory (minimal for free tier)
- **ALB**: Charges apply after free tier limits
- **API Gateway**: 1M requests/month free
- **DynamoDB**: 25 GB storage, 25 RCU/WCU free
- **ECR**: 500 MB storage free

## Monitoring

- CloudWatch logs: `/ecs/franchise-service`
- ECS service metrics in CloudWatch
- ALB target group health checks

## Cleanup

```bash
cd deployment/terraform
terraform destroy
```

## Troubleshooting

1. **ECS Task not starting**: Check CloudWatch logs
2. **Health check failing**: Verify `/actuator/health` endpoint
3. **API Gateway 502**: Check ALB target group health
4. **DynamoDB access**: Verify IAM roles and policies