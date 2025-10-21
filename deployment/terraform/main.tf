module "dynamodb_franchise_table" {
  source       = "./modules/dynamodb"
  table_name   = "franchise-table-dev"
  hash_key     = "pk"
  range_key    = "sk"
  ttl_enabled  = false

  tags = {
    Environment = "dev"
    Project     = "franchise-service"
  }
}
