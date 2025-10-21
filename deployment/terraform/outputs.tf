output "franchise_table_name" {
  description = "Name in dynamodb of table Franchise."
  value       = module.dynamodb_franchise_table.table_name
}

output "franchise_table_arn" {
  description = "ARN dynamodb of table Franchise."
  value       = module.dynamodb_franchise_table.table_arn
}

output "ecr_repository_url" {
  description = "ECR repository URL"
  value       = module.ecr.repository_url
}

output "alb_dns_name" {
  description = "ALB DNS name"
  value       = module.alb.alb_dns_name
}

output "api_gateway_url" {
  description = "API Gateway URL"
  value       = module.api_gateway.api_gateway_url
}

output "ecs_cluster_name" {
  description = "ECS cluster name"
  value       = module.ecs.cluster_name
}
