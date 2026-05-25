module "platform_core_baseline" {
  source = "../../modules/platform-core-kubernetes-baseline"

  provider_name          = "aws"
  namespace              = var.namespace
  image_repository       = var.image_repository
  image_tag              = var.image_tag
  registry_host          = var.registry_host
  service_host           = var.service_host
  secret_store_name      = "aws-secrets-manager"
  database_endpoint      = var.database_endpoint
  observability_endpoint = var.observability_endpoint
  ingress_class_name     = "alb"

  service_annotations = {
    "service.beta.kubernetes.io/aws-load-balancer-type"   = "nlb"
    "service.beta.kubernetes.io/aws-load-balancer-scheme" = "internet-facing"
  }

  workload_identity_annotations = {
    "eks.amazonaws.com/role-arn" = "REPLACE_WITH_COUNTRY_EKS_ROLE_ARN"
  }

  tags = var.tags
}

