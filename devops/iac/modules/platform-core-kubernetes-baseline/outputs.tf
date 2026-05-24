output "deployment_contract" {
  value = local.deployment_contract
}

output "runtime_environment_keys" {
  value = [
    "EDU_DB_URL",
    "EDU_DB_USERNAME",
    "EDU_DB_PASSWORD"
  ]
}

