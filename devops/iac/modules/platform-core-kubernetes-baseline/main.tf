locals {
  image_reference = trimspace(var.registry_host) != "" ? "${trimspace(var.registry_host)}/${var.image_repository}:${var.image_tag}" : "${var.image_repository}:${var.image_tag}"

  deployment_contract = {
    provider_name           = var.provider_name
    kubernetes_base_path    = "devops/kubernetes/platform-core/base"
    kubernetes_overlay_path = "devops/kubernetes/platform-core/overlays/${var.provider_name}"
    namespace               = var.namespace
    image_reference         = local.image_reference
    ingress_class_name      = var.ingress_class_name
    service_host            = var.service_host
    runtime_config_map_name = var.config_map_name
    runtime_secret_name     = var.secret_name

    configurable_dependencies = {
      registry               = var.registry_host
      secret_store           = var.secret_store_name
      database_endpoint      = var.database_endpoint
      observability_endpoint = var.observability_endpoint
    }

    provider_specific_annotations = {
      service           = var.service_annotations
      workload_identity = var.workload_identity_annotations
    }

    tags = merge(
      {
        "deployment.darkfactory.io/provider"  = var.provider_name
        "deployment.darkfactory.io/ownership" = "country-sovereign"
      },
      var.tags
    )
  }
}

