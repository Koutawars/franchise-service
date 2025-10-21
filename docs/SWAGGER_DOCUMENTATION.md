# Swagger API Documentation

## Overview
This project now includes comprehensive OpenAPI/Swagger documentation for the Franchise Service API.

## Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

## API Endpoints

### Franchises
- `POST /api/v1/franchises` - Create a new franchise
- `PATCH /api/v1/franchises/{franchiseId}/name` - Update franchise name

### Branches  
- `POST /api/v1/franchises/{franchiseId}/branches` - Create a new branch
- `PATCH /api/v1/franchises/{franchiseId}/branches/{branchId}/name` - Update branch name

### Products
- `POST /api/v1/franchises/{franchiseId}/branches/{branchId}/products` - Create a new product
- `DELETE /api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}` - Delete a product
- `PATCH /api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` - Update product stock
- `PATCH /api/v1/franchises/{franchiseId}/branches/{branchId}/products/{productId}/name` - Update product name
- `GET /api/v1/franchises/{franchiseId}/top-products` - Get top products per branch

## Features Added

1. **OpenAPI Configuration**: Complete OpenAPI 3.0 specification
2. **DTO Annotations**: All DTOs include schema descriptions and examples
3. **Model Annotations**: Domain models include comprehensive documentation
4. **Response Documentation**: Detailed response codes and error handling
5. **Interactive UI**: Try-it-out functionality enabled
6. **Grouped APIs**: Organized by functionality

## Configuration

The Swagger configuration is located in:
- `OpenApiConfig.java` - Main OpenAPI configuration
- `application.yaml` - Swagger UI settings

## Usage

1. Start the application
2. Navigate to http://localhost:8080/swagger-ui.html
3. Explore the API endpoints
4. Use the "Try it out" feature to test endpoints
5. View request/response schemas and examples