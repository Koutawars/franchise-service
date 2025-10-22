resource "aws_dynamodb_table" "this" {
  name         = var.table_name
  billing_mode = var.billing_mode
  hash_key     = var.hash_key
  range_key    = var.range_key

  attribute {
    name = var.hash_key
    type = "S"
  }

  attribute {
    name = var.range_key
    type = "S"
  }

  dynamic "attribute" {
    for_each = { for idx in var.global_indexes : idx.hash_key => idx }
    content {
      name = attribute.key
      type = "S"
    }
  }

  dynamic "attribute" {
    for_each = { for idx in var.global_indexes : idx.range_key => idx if idx.range_key != null }
    content {
      name = attribute.key
      type = "S"
    }
  }

  # GSI
  dynamic "global_secondary_index" {
    for_each = var.global_indexes
    content {
      name            = global_secondary_index.value.name
      hash_key        = global_secondary_index.value.hash_key
      range_key       = try(global_secondary_index.value.range_key, null)
      projection_type = global_secondary_index.value.projection_type
      read_capacity   = var.billing_mode == "PROVISIONED" ? 5 : null
      write_capacity  = var.billing_mode == "PROVISIONED" ? 5 : null
    }
  }

  # optional for PROVISIONED
  read_capacity  = var.billing_mode == "PROVISIONED" ? 5 : null
  write_capacity = var.billing_mode == "PROVISIONED" ? 5 : null

  tags = var.tags
}
