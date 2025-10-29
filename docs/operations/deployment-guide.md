---
author: Documentation Team
last_updated: 2025-10-29
version: 1.0.0
review_status: Initial Draft
---

# Deployment Guide

## Table of Contents

- [Overview](#overview)
- [Deployment Environments](#deployment-environments)
- [Prerequisites](#prerequisites)
- [Local Deployment](#local-deployment)
- [Docker Deployment](#docker-deployment)
- [Azure Web App Deployment](#azure-web-app-deployment)
- [CI/CD Pipeline](#cicd-pipeline)
- [Configuration Management](#configuration-management)
- [Rollback Procedures](#rollback-procedures)
- [Post-Deployment Validation](#post-deployment-validation)

## Overview

The Bookstore Application supports multiple deployment strategies:
- **Local**: Development and testing on developer workstations
- **Docker**: Containerized deployment for consistency across environments
- **Azure Web Apps**: Cloud-based production hosting with auto-scaling
- **CI/CD**: Automated deployment via GitHub Actions

### Deployment Philosophy

- **Infrastructure as Code**: All infrastructure defined in version-controlled files
- **Immutable Deployments**: Containers are never modified, only replaced
- **Zero-Downtime**: Blue-green deployment strategy for production
- **Automated Testing**: Validation at every stage of deployment
- **Rollback Ready**: Quick rollback to previous version if issues occur

## Deployment Environments

| Environment | Purpose | Trigger | URL | Auto-Destroy |
|-------------|---------|---------|-----|--------------|
| **Local** | Development | Manual | http://localhost:8080 | N/A |
| **Test** | Feature Testing | PR Label: `deploy to test` | https://{app}-test.azurewebsites.net | On PR close |
| **QA** | Quality Assurance | PR Label: `deploy to qa` | https://{app}-qa.azurewebsites.net | On PR close |
| **Staging** | Pre-Production | PR Label: `deploy to staging` | https://{app}-staging.azurewebsites.net | On PR close |
| **Production** | Live | Merge to `main` | https://{app}.azurewebsites.net | Never |

### Environment Characteristics

**Development (Local)**:
- In-memory SQLite database
- Hot reload capabilities
- Debug logging enabled
- No authentication required

**Test/QA/Staging (Azure Review Environments)**:
- Ephemeral environments
- Created on-demand via PR labels
- Destroyed automatically when PR closes
- Isolated from production data
- Cost-optimized (smaller instance sizes)

**Production (Azure)**:
- Persistent environment
- Auto-scaling enabled
- Production-grade monitoring
- HTTPS enforced
- Regular backups
- High availability configuration

## Prerequisites

### For All Deployments

- Java JDK 11+
- Apache Maven 3.6.3+
- Git

### For Docker Deployments

- Docker Engine 20.x+
- Docker Compose (optional, for multi-container setups)

### For Azure Deployments

- Azure CLI (`az`) 2.x+
- Azure subscription with appropriate permissions
- GitHub Actions configured with Azure credentials

### Required Permissions

**Azure**:
- Contributor role on resource group
- Permission to create/modify Web Apps
- Permission to create/modify Container Registries

**GitHub**:
- Write access to repository
- Permission to create/modify Actions secrets
- Permission to label pull requests

## Local Deployment

### Step 1: Build Application

```bash
# Clean build
mvn clean package

# Verify JAR created
ls -lh target/bookstore-v3-1.0.0-SNAPSHOT.jar
```

### Step 2: Run Application

```bash
# Default port (8080)
java -jar target/bookstore-v3-1.0.0-SNAPSHOT.jar

# Custom port
export SERVER_PORT=9090
java -jar target/bookstore-v3-1.0.0-SNAPSHOT.jar
```

### Step 3: Verify Deployment

```bash
# Health check
curl http://localhost:8080/status

# Test main page
curl -I http://localhost:8080/

# Open in browser
open http://localhost:8080  # macOS
xdg-open http://localhost:8080  # Linux
start http://localhost:8080  # Windows
```

## Docker Deployment

### Step 1: Build Application JAR

```bash
# Build the application
mvn clean package

# Verify JAR exists
ls -lh target/bookstore-v3-1.0.0-SNAPSHOT.jar
```

### Step 2: Build Docker Image

```bash
# Build with default version
docker build . \
  --build-arg VERSION=1.0.0-SNAPSHOT \
  --tag bookstore:latest

# Build with specific version
docker build . \
  --build-arg VERSION=1.0.0-${GIT_SHA:0:7} \
  --build-arg revision=${GIT_SHA} \
  --tag bookstore:1.0.0
```

### Step 3: Run Container

```bash
# Run on default port (8080)
docker run -d \
  --name bookstore \
  -p 8080:8080 \
  bookstore:latest

# Run with custom configuration
docker run -d \
  --name bookstore \
  -p 9090:9090 \
  -e SERVER_PORT=9090 \
  -e DATABASE_URL=jdbc:postgresql://db:5432/bookstore \
  -e DATABASE_USER=bookstore_user \
  -e DATABASE_PASSWORD=secret \
  bookstore:latest

# Run with volume mount (for logs)
docker run -d \
  --name bookstore \
  -p 8080:8080 \
  -v $(pwd)/logs:/opt/app/logs \
  bookstore:latest
```

### Step 4: Manage Container

```bash
# View logs
docker logs bookstore
docker logs -f bookstore  # Follow logs

# Check status
docker ps | grep bookstore

# Inspect container
docker inspect bookstore

# Execute command in container
docker exec -it bookstore sh

# Stop container
docker stop bookstore

# Remove container
docker rm bookstore
```

### Docker Compose (Optional)

**Create `docker-compose.yml`**:
```yaml
version: '3.8'

services:
  bookstore:
    build:
      context: .
      args:
        VERSION: 1.0.0-SNAPSHOT
    ports:
      - "8080:8080"
    environment:
      - SERVER_PORT=8080
      - DATABASE_URL=jdbc:postgresql://db:5432/bookstore
      - DATABASE_USER=bookstore
      - DATABASE_PASSWORD=secret
    depends_on:
      - db
    restart: unless-stopped

  db:
    image: postgres:13-alpine
    environment:
      - POSTGRES_DB=bookstore
      - POSTGRES_USER=bookstore
      - POSTGRES_PASSWORD=secret
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

volumes:
  postgres_data:
```

**Run with Docker Compose**:
```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## Azure Web App Deployment

### Manual Deployment

#### Step 1: Build and Push Container

```bash
# Login to Azure Container Registry
az acr login --name myregistry

# Build and tag image
docker build . \
  --build-arg VERSION=1.0.0 \
  --tag myregistry.azurecr.io/bookstore:1.0.0

# Push to registry
docker push myregistry.azurecr.io/bookstore:1.0.0
```

#### Step 2: Create Azure Resources (First Time)

```bash
# Set variables
RESOURCE_GROUP="bookstore-rg"
LOCATION="eastus"
APP_NAME="bookstore-app"
ACR_NAME="myregistry"

# Create resource group
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION

# Create container registry
az acr create \
  --resource-group $RESOURCE_GROUP \
  --name $ACR_NAME \
  --sku Basic

# Create App Service plan
az appservice plan create \
  --name bookstore-plan \
  --resource-group $RESOURCE_GROUP \
  --is-linux \
  --sku B1

# Create Web App
az webapp create \
  --resource-group $RESOURCE_GROUP \
  --plan bookstore-plan \
  --name $APP_NAME \
  --deployment-container-image-name $ACR_NAME.azurecr.io/bookstore:1.0.0
```

#### Step 3: Configure Web App

```bash
# Enable container logging
az webapp log config \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --docker-container-logging filesystem

# Set environment variables
az webapp config appsettings set \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --settings \
    SERVER_PORT=8080 \
    WEBSITES_PORT=8080 \
    DATABASE_URL="jdbc:postgresql://dbserver.postgres.database.azure.com:5432/bookstore" \
    DATABASE_USER="bookstore@dbserver" \
    DATABASE_PASSWORD="SecurePassword123!"

# Configure continuous deployment
az webapp deployment container config \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --enable-cd true
```

#### Step 4: Deploy New Version

```bash
# Update container image
az webapp config container set \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP \
  --docker-custom-image-name $ACR_NAME.azurecr.io/bookstore:1.1.0

# Restart Web App
az webapp restart \
  --name $APP_NAME \
  --resource-group $RESOURCE_GROUP
```

### Using Bicep Templates

The repository includes Bicep templates for infrastructure provisioning.

**Deploy with Bicep**:
```bash
# Deploy infrastructure
az deployment group create \
  --resource-group $RESOURCE_GROUP \
  --template-file bicep/webapp.bicep \
  --parameters \
    webAppName=$APP_NAME \
    location=$LOCATION \
    dockerImageName="$ACR_NAME.azurecr.io/bookstore:1.0.0"
```

**Bicep Template** (`bicep/webapp.bicep`):
```bicep
param webAppName string
param location string = resourceGroup().location
param dockerImageName string

resource appServicePlan 'Microsoft.Web/serverfarms@2021-02-01' = {
  name: '${webAppName}-plan'
  location: location
  sku: {
    name: 'B1'
    tier: 'Basic'
  }
  kind: 'linux'
  properties: {
    reserved: true
  }
}

resource webApp 'Microsoft.Web/sites@2021-02-01' = {
  name: webAppName
  location: location
  properties: {
    serverFarmId: appServicePlan.id
    siteConfig: {
      linuxFxVersion: 'DOCKER|${dockerImageName}'
      appSettings: [
        {
          name: 'WEBSITES_PORT'
          value: '8080'
        }
        {
          name: 'SERVER_PORT'
          value: '8080'
        }
      ]
    }
  }
}
```

## CI/CD Pipeline

### GitHub Actions Workflows

The repository includes several GitHub Actions workflows for automated deployment.

#### Build, Test, and Publish Workflow

**File**: `.github/workflows/build_test_publish.yml`

**Triggers**:
- Push to `main` branch
- Pull request creation/update

**Steps**:
1. Checkout code
2. Set up Java 11
3. Build with Maven
4. Run tests
5. Build Docker image
6. Scan container for vulnerabilities
7. Push to GitHub Container Registry
8. Deploy to production (if main branch)

#### PR Review Environment Deployment

**File**: `.github/workflows/label_deployment_trigger.yml`

**Triggers**:
- PR labeled with:
  - `deploy to test`
  - `deploy to qa`
  - `deploy to staging`

**Steps**:
1. Build container image
2. Push to container registry
3. Deploy to Azure Web App (environment-specific)
4. Comment on PR with deployment URL

#### Environment Cleanup

**File**: `.github/workflows/cleanup_pr_review_environment.yml`

**Triggers**:
- PR closed

**Steps**:
1. Determine deployed environments
2. Run Ansible playbook to destroy resources
3. Remove Azure Web Apps
4. Clean up container images

### Configuring GitHub Actions

#### Required Secrets

Configure these secrets in GitHub repository settings:

| Secret Name | Description | Example |
|-------------|-------------|---------|
| `AZURE_CREDENTIALS` | Azure service principal credentials | JSON object |
| `AZURE_SUBSCRIPTION_ID` | Azure subscription ID | GUID |
| `AZURE_RESOURCE_GROUP` | Resource group name | bookstore-rg |
| `ACR_LOGIN_SERVER` | Container registry URL | myregistry.azurecr.io |
| `ACR_USERNAME` | Container registry username | myregistry |
| `ACR_PASSWORD` | Container registry password | Secure password |

#### Creating Azure Service Principal

```bash
# Create service principal
az ad sp create-for-rbac \
  --name "bookstore-github-actions" \
  --role contributor \
  --scopes /subscriptions/{subscription-id}/resourceGroups/bookstore-rg \
  --sdk-auth

# Output (save as AZURE_CREDENTIALS secret):
{
  "clientId": "...",
  "clientSecret": "...",
  "subscriptionId": "...",
  "tenantId": "...",
  "activeDirectoryEndpointUrl": "...",
  "resourceManagerEndpointUrl": "...",
  "activeDirectoryGraphResourceId": "...",
  "sqlManagementEndpointUrl": "...",
  "galleryEndpointUrl": "...",
  "managementEndpointUrl": "..."
}
```

## Configuration Management

### Environment-Specific Configuration

**Development**:
```bash
SERVER_PORT=8080
DATABASE_URL=jdbc:sqlite::memory:
LOG_LEVEL=DEBUG
```

**Production**:
```bash
SERVER_PORT=8080
DATABASE_URL=jdbc:postgresql://prod-db.postgres.database.azure.com:5432/bookstore
DATABASE_USER=bookstore@prod-db
DATABASE_PASSWORD=<from Azure Key Vault>
LOG_LEVEL=INFO
```

### Secrets Management

**Azure Key Vault Integration**:

```bash
# Create Key Vault
az keyvault create \
  --name bookstore-kv \
  --resource-group bookstore-rg \
  --location eastus

# Store secrets
az keyvault secret set \
  --vault-name bookstore-kv \
  --name database-password \
  --value "SecurePassword123!"

# Grant Web App access
az webapp identity assign \
  --name bookstore-app \
  --resource-group bookstore-rg

# Configure Web App to use Key Vault
az webapp config appsettings set \
  --name bookstore-app \
  --resource-group bookstore-rg \
  --settings \
    DATABASE_PASSWORD="@Microsoft.KeyVault(SecretUri=https://bookstore-kv.vault.azure.net/secrets/database-password/)"
```

## Rollback Procedures

### Rollback Scenarios

1. **Application error detected post-deployment**
2. **Performance degradation**
3. **Security vulnerability discovered**
4. **Database migration failure**

### Quick Rollback (Docker)

```bash
# Stop current version
docker stop bookstore

# Start previous version
docker run -d \
  --name bookstore \
  -p 8080:8080 \
  bookstore:previous-version

# Verify
curl http://localhost:8080/status
```

### Azure Web App Rollback

#### Method 1: Swap Deployment Slots

```bash
# Swap staging to production
az webapp deployment slot swap \
  --name bookstore-app \
  --resource-group bookstore-rg \
  --slot staging \
  --target-slot production
```

#### Method 2: Redeploy Previous Version

```bash
# Get previous container image tag
PREVIOUS_TAG=$(git describe --tags --abbrev=0)

# Update Web App
az webapp config container set \
  --name bookstore-app \
  --resource-group bookstore-rg \
  --docker-custom-image-name myregistry.azurecr.io/bookstore:$PREVIOUS_TAG

# Restart
az webapp restart \
  --name bookstore-app \
  --resource-group bookstore-rg
```

#### Method 3: Use GitHub Actions Rollback

1. Navigate to Actions tab in GitHub
2. Find successful deployment from previous version
3. Click "Re-run all jobs"
4. Monitor deployment

### Rollback Validation Checklist

- [ ] Application starts successfully
- [ ] Health check endpoint returns 200 OK
- [ ] Main page loads correctly
- [ ] Database connectivity verified
- [ ] No error logs after 5 minutes
- [ ] Performance metrics within acceptable range
- [ ] User-reported issues resolved

## Post-Deployment Validation

### Automated Validation

**Health Check**:
```bash
#!/bin/bash
APP_URL="https://bookstore-app.azurewebsites.net"

# Check status endpoint
STATUS=$(curl -s -o /dev/null -w "%{http_code}" $APP_URL/status)
if [ $STATUS -eq 200 ]; then
  echo "✓ Health check passed"
else
  echo "✗ Health check failed: HTTP $STATUS"
  exit 1
fi

# Check main page
HOMEPAGE=$(curl -s -o /dev/null -w "%{http_code}" $APP_URL/)
if [ $HOMEPAGE -eq 200 ]; then
  echo "✓ Homepage accessible"
else
  echo "✗ Homepage failed: HTTP $HOMEPAGE"
  exit 1
fi

# Check response time
RESPONSE_TIME=$(curl -s -o /dev/null -w "%{time_total}" $APP_URL/)
if (( $(echo "$RESPONSE_TIME < 2.0" | bc -l) )); then
  echo "✓ Response time acceptable: ${RESPONSE_TIME}s"
else
  echo "⚠ Response time slow: ${RESPONSE_TIME}s"
fi

echo "Deployment validation complete"
```

### Manual Validation

**Checklist**:
- [ ] Application accessible at expected URL
- [ ] SSL certificate valid (HTTPS)
- [ ] Books displayed correctly
- [ ] Rating filter functions
- [ ] Images load properly
- [ ] Version number correct in footer
- [ ] No JavaScript errors in browser console
- [ ] Mobile responsive design works
- [ ] Navigation links functional

### Smoke Tests

```bash
# Run smoke tests
curl -f https://bookstore-app.azurewebsites.net/status || exit 1
curl -f https://bookstore-app.azurewebsites.net/ || exit 1
curl -f https://bookstore-app.azurewebsites.net/?rating=4.0 || exit 1
curl -f https://bookstore-app.azurewebsites.net/static/bootstrap-3.4.1-dist/css/bootstrap.min.css || exit 1
```

### Performance Validation

```bash
# Load test with Apache Bench
ab -n 1000 -c 10 https://bookstore-app.azurewebsites.net/

# Check average response time (should be < 500ms)
# Check requests per second (should be > 50)
```

## Deployment Troubleshooting

### Common Issues

**Issue: Container fails to start**
```bash
# Check logs
az webapp log tail \
  --name bookstore-app \
  --resource-group bookstore-rg

# Common causes:
# - Port configuration mismatch
# - Missing environment variables
# - Database connection failure
```

**Issue: "Site not found" after deployment**
```bash
# Verify Web App exists
az webapp show \
  --name bookstore-app \
  --resource-group bookstore-rg

# Check if app is running
az webapp browse \
  --name bookstore-app \
  --resource-group bookstore-rg
```

**Issue: Slow performance**
```bash
# Scale up
az appservice plan update \
  --name bookstore-plan \
  --resource-group bookstore-rg \
  --sku S1

# Enable auto-scale
az monitor autoscale create \
  --resource-group bookstore-rg \
  --resource bookstore-app \
  --resource-type Microsoft.Web/sites \
  --name autoscale-rules \
  --min-count 2 \
  --max-count 10 \
  --count 2
```

## References

- [System Architecture](../architecture/system-overview.md)
- [Developer Setup Guide](../development/setup-guide.md)
- [Operations Runbook](../operations/runbook.md)
- [Security Documentation](../security/security-overview.md)
- [Azure Web Apps Documentation](https://docs.microsoft.com/azure/app-service/)
- [Docker Documentation](https://docs.docker.com/)
