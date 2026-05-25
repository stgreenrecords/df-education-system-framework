variable "namespace" {
  type    = string
  default = "platform-core"
}

variable "image_repository" {
  type    = string
  default = "education-system-framework/platform-core"
}

variable "image_tag" {
  type    = string
  default = "latest"
}

variable "registry_host" {
  type    = string
  default = "123456789012.dkr.ecr.eu-central-1.amazonaws.com"
}

variable "service_host" {
  type    = string
  default = "platform-core.country.example.edu"
}

variable "database_endpoint" {
  type    = string
  default = "postgres.country.internal:5432/education_system"
}

variable "observability_endpoint" {
  type    = string
  default = "https://observability.country.example.edu/otlp"
}

variable "tags" {
  type    = map(string)
  default = {}
}

