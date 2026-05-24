module "platform_core_baseline" {
  source = "../../modules/platform-core-kubernetes-baseline"

  provider_name          = "self-hosted"
  namespace              = var.namespace
  image_repository       = var.image_repository
  image_tag              = var.image_tag
  registry_host          = var.registry_host
  service_host           = var.service_host
  secret_store_name      = "sealed-secrets-or-external-secrets"
  database_endpoint      = var.database_endpoint
  observability_endpoint = var.observability_endpoint
  ingress_class_name     = "nginx"

  service_annotations = {
    "metallb.universe.tf/address-pool" = "country-public"
  }

  workload_identity_annotations = {
    "deployment.darkfactory.io/registry-auth" = "country-managed-image-pull-secret"
  }

  tags = var.tags
}

