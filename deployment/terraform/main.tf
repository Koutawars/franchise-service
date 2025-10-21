module "vpc" {
  source         = "./modules/vpc"
  project_name   = var.project_name
  container_port = var.container_port
}

module "ecr" {
  source          = "./modules/ecr"
  repository_name = var.project_name
}

module "alb" {
  source            = "./modules/alb"
  project_name      = var.project_name
  vpc_id            = module.vpc.vpc_id
  subnet_ids        = module.vpc.public_subnet_ids
  security_group_id = module.vpc.alb_security_group_id
  container_port    = var.container_port
  health_check_path = var.health_check_path
}

module "dynamodb_franchise_table" {
  source       = "./modules/dynamodb"
  table_name   = "${var.project_name}-table-${var.environment}"
  hash_key     = "pk"
  range_key    = "sk"
  ttl_enabled  = false

  tags = {
    Environment = var.environment
    Project     = var.project_name
  }
}

module "ecs" {
  source              = "./modules/ecs"
  project_name        = var.project_name
  vpc_id              = module.vpc.vpc_id
  subnet_ids          = module.vpc.public_subnet_ids
  security_group_id   = module.vpc.ecs_security_group_id
  target_group_arn    = module.alb.target_group_arn
  ecr_repository_url  = module.ecr.repository_url
  container_port      = var.container_port
  aws_region          = var.aws_region
  dynamodb_table_arn  = module.dynamodb_franchise_table.table_arn
  dynamodb_table_name = module.dynamodb_franchise_table.table_name
}

module "api_gateway" {
  source       = "./modules/api-gateway"
  project_name = var.project_name
  environment  = var.environment
  alb_dns_name = module.alb.alb_dns_name
}
