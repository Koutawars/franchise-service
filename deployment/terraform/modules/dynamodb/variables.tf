variable "table_name" {
  type = string
}

variable "billing_mode" {
  type    = string
  default = "PAY_PER_REQUEST"
}

variable "read_capacity" {
  type    = number
  default = 5
}

variable "write_capacity" {
  type    = number
  default = 5
}

variable "hash_key" {
  type        = string
  description = "attribute name partition key / pk"
}

variable "range_key" {
  type        = string
  description = "attribute name sort ket / sk"
}

variable "ttl_enabled" {
  type    = bool
  default = false
}

variable "ttl_attribute" {
  type    = string
  default = "ttl"
}

variable "global_indexes" {
  type = list(object({
    name              = string
    hash_key          = string
    range_key         = optional(string)
    projection_type   = string
    read_capacity     = optional(number)
    write_capacity    = optional(number)
  }))
  default = []
}

variable "tags" {
  type = map(string)
  default = {}
}
