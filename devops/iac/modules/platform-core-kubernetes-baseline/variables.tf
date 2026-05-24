variable "provider_name" {
  type = string
}

variable "namespace" {
  type = string
}

variable "image_repository" {
  type = string
}

variable "image_tag" {
  type = string
}

variable "registry_host" {
  type = string
}

variable "service_host" {
  type = string
}

variable "secret_store_name" {
  type = string
}

variable "database_endpoint" {
  type = string
}

variable "observability_endpoint" {
  type = string
}

variable "config_map_name" {
  type    = string
  default = "platform-core-runtime-config"
}

variable "secret_name" {
  type    = string
  default = "platform-core-runtime-secrets"
}

variable "ingress_class_name" {
  type = string
}

variable "service_annotations" {
  type    = map(string)
  default = {}
}

variable "workload_identity_annotations" {
  type    = map(string)
  default = {}
}

variable "tags" {
  type    = map(string)
  default = {}
}

