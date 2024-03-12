ui                 = true
disable_clustering = true
api_addr           = "https://vault.santelos.com"
disable_mlock = true

storage "file" {
  path = "/vault/file"
}

listener "tcp" {
  address       = "0.0.0.0:8200"
  tls_cert_file = "/etc/certs/tls.crt"
  tls_key_file  = "/etc/certs/tls.key"
}
