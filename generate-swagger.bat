@echo off
echo Generating Swagger YAML...
curl http://localhost:8080/v3/api-docs.yaml -o docs/swagger.yaml
echo Swagger YAML generated at docs/swagger.yaml
