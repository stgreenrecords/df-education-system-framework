module "platform_core_baseline" {
  source = "../../modules/platform-core-kubernetes-baseline"

  provider_name          = "azure"
  namespace              = var.namespace
  image_repository       = var.image_repository
  image_tag              = var.image_tag
  registry_host          = var.registry_host
  service_host           = var.service_host
  secret_store_name      = "azure-key-vault"
  database_endpoint      = var.database_endpoint
  observability_endpoint = var.observability_endpoint
  ingress_class_name     = "azure-application-gateway"

  service_annotations = {
    "service.beta.kubernetes.io/azure-load-balancer-health-probe-request-path" = "/platform/status"
  }

  workload_identity_annotations = {
    "azure.workload.identity/client-id" = "REPLACE_WITH_COUNTRY_MANAGED_IDENTITY_CLIENT_ID"
  }

  tags = var.tags
}

