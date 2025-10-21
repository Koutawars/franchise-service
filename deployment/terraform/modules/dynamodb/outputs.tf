output "table_name" {
  description = "table name dynamodb."
  value       = aws_dynamodb_table.this.name
}

output "table_arn" {
  description = "ARN table dynamodb."
  value       = aws_dynamodb_table.this.arn
}
