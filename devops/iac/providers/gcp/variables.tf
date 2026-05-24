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
  default = "europe-west1-docker.pkg.dev/country-project/education-system"
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
  default = "https://cloudtrace.country.example.edu/otlp"
}

variable "tags" {
  type    = map(string)
  default = {}
}

