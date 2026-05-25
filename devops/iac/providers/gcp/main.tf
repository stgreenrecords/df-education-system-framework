module "platform_core_baseline" {
  source = "../../modules/platform-core-kubernetes-baseline"

  provider_name          = "gcp"
  namespace              = var.namespace
  image_repository       = var.image_repository
  image_tag              = var.image_tag
  registry_host          = var.registry_host
  service_host           = var.service_host
  secret_store_name      = "google-secret-manager"
  database_endpoint      = var.database_endpoint
  observability_endpoint = var.observability_endpoint
  ingress_class_name     = "gce"

  service_annotations = {
    "cloud.google.com/neg" = jsonencode({ ingress = true })
  }

  workload_identity_annotations = {
    "iam.gke.io/gcp-service-account" = "platform-core@PROJECT_ID.iam.gserviceaccount.com"
  }

  tags = var.tags
}

