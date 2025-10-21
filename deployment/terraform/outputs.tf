output "franchise_table_name" {
  description = "Name in dynamodb of table Franchise."
  value       = module.dynamodb_franchise_table.table_name
}

output "franchise_table_arn" {
  description = "ARN dynamodb of table Franchise."
  value       = module.dynamodb_franchise_table.table_arn
}
