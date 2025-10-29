---
author: Documentation Team
last_updated: 2025-10-29
version: 1.0.0
review_status: Initial Draft
---

# Bookstore Application - Documentation Index

Welcome to the comprehensive documentation for the Bookstore Application. This documentation covers all aspects of the application from architecture to deployment.

## Quick Links

- **Getting Started**: [Developer Setup Guide](development/setup-guide.md)
- **Architecture**: [System Overview](architecture/system-overview.md)
- **API Reference**: [API Documentation](api/api-reference.md)
- **Deployment**: [Deployment Guide](operations/deployment-guide.md)

## Documentation Structure

### 📚 Architecture Documentation

Comprehensive architectural documentation covering system design, components, and technology stack.

- **[System Overview](architecture/system-overview.md)** - High-level architecture, components, data flow, and deployment architecture

### 🔌 API Documentation

Complete API reference for all endpoints, data models, and usage examples.

- **[API Reference](api/api-reference.md)** - REST API endpoints, parameters, request/response formats, and examples

### 💾 Database Documentation

Database schema, design decisions, and data management strategies.

- **[Database Schema](database/schema.md)** - Table specifications, relationships, indexes, and migration strategies

### 👨‍💻 Development Documentation

Guides for setting up development environment and contributing to the project.

- **[Setup Guide](development/setup-guide.md)** - Prerequisites, installation, building, running, testing, and debugging
- **[Coding Guidelines](code-standards/coding-guidelines.md)** - Code standards, best practices, and conventions

### 🚀 Operations Documentation

Deployment procedures, infrastructure management, and operational runbooks.

- **[Deployment Guide](operations/deployment-guide.md)** - Local, Docker, and Azure deployment procedures with CI/CD configuration

### 🔒 Security Documentation

Security architecture, best practices, and compliance information.

- **[Security Overview](security/security-overview.md)** - Authentication, encryption, vulnerability management, and incident response

## Documentation by Audience

### For Developers

**Getting Started**:
1. Read [System Overview](architecture/system-overview.md) to understand the architecture
2. Follow [Setup Guide](development/setup-guide.md) to configure your environment
3. Review [Coding Guidelines](code-standards/coding-guidelines.md) before contributing
4. Refer to [API Reference](api/api-reference.md) when working with endpoints

**Essential Reading**:
- [System Overview](architecture/system-overview.md) - Understand how components work together
- [Database Schema](database/schema.md) - Learn the data model
- [Setup Guide](development/setup-guide.md) - Get your environment running
- [Coding Guidelines](code-standards/coding-guidelines.md) - Write consistent code

### For Operations Teams

**Getting Started**:
1. Review [System Overview](architecture/system-overview.md) for infrastructure understanding
2. Study [Deployment Guide](operations/deployment-guide.md) for deployment procedures
3. Read [Security Overview](security/security-overview.md) for security requirements

**Essential Reading**:
- [Deployment Guide](operations/deployment-guide.md) - Deployment procedures and troubleshooting
- [Security Overview](security/security-overview.md) - Security configuration and monitoring

### For Security Teams

**Getting Started**:
1. Read [Security Overview](security/security-overview.md) for comprehensive security documentation
2. Review [API Reference](api/api-reference.md) for endpoint security
3. Check [Database Schema](database/schema.md) for data security measures

**Essential Reading**:
- [Security Overview](security/security-overview.md) - Complete security architecture and practices

### For Business Stakeholders

**Getting Started**:
1. Read [System Overview](architecture/system-overview.md) - Introduction section
2. Review deployment environments in [Deployment Guide](operations/deployment-guide.md)

## Documentation by Task

### Setting Up Development Environment

1. **[Developer Setup Guide](development/setup-guide.md)** - Complete installation and configuration instructions
2. **[System Overview](architecture/system-overview.md)** - Understand the technology stack
3. **[Database Schema](database/schema.md)** - Learn the data model

### Making Code Changes

1. **[Coding Guidelines](code-standards/coding-guidelines.md)** - Follow coding standards
2. **[API Reference](api/api-reference.md)** - Understand existing endpoints
3. **[Database Schema](database/schema.md)** - Work with data structures
4. **[Security Overview](security/security-overview.md)** - Security best practices

### Deploying the Application

1. **[Deployment Guide](operations/deployment-guide.md)** - Step-by-step deployment procedures
2. **[System Overview](architecture/system-overview.md)** - Deployment architecture
3. **[Security Overview](security/security-overview.md)** - Security configuration

### Troubleshooting Issues

1. **[Setup Guide](development/setup-guide.md)** - Common issues and troubleshooting
2. **[Deployment Guide](operations/deployment-guide.md)** - Deployment troubleshooting
3. **[System Overview](architecture/system-overview.md)** - Component interactions

## Technology Overview

### Backend
- **Language**: Java 11
- **Framework**: Eclipse Jetty 10.0.0
- **Template Engine**: Thymeleaf 3.0.12
- **Database**: SQLite (in-memory) / PostgreSQL (production)
- **Build Tool**: Apache Maven 3.x

### Frontend
- **Framework**: Bootstrap 3.4.1
- **Template Engine**: Thymeleaf

### Infrastructure
- **Containerization**: Docker
- **Cloud Platform**: Azure Web Apps
- **CI/CD**: GitHub Actions
- **IaC**: Bicep

### Security
- **SAST**: CodeQL
- **Container Scanning**: Trivy
- **Dependency Scanning**: Dependabot

## Key Features

- 📚 Browse complete book catalog
- ⭐ Filter books by rating
- 📖 View book details with cover images
- 📱 Responsive design (mobile, tablet, desktop)
- 🔍 Search functionality (planned)
- 🛒 Shopping cart (planned)
- 👤 User accounts (planned)

## Application Architecture

```
┌──────────────┐
│   Browser    │ ← Users interact via web browser
└──────┬───────┘
       │ HTTP/HTTPS
┌──────▼───────┐
│ Jetty Server │ ← Embedded web server (Port 8080)
└──────┬───────┘
       │
┌──────▼──────────┐
│   Servlets      │ ← BookServlet, StatusServlet
└──────┬──────────┘
       │
┌──────▼──────────┐
│  BookService    │ ← Business logic layer
└──────┬──────────┘
       │
┌──────▼──────────┐
│ BookDatabase    │ ← Data access layer
└──────┬──────────┘
       │
┌──────▼──────────┐
│ SQLite Database │ ← In-memory or persistent storage
└─────────────────┘
```

## Environment Configuration

### Development
- **Port**: 8080 (configurable via `SERVER_PORT`)
- **Database**: In-memory SQLite
- **Logging**: DEBUG level
- **Hot Reload**: Supported

### Production
- **Port**: 8080 (Azure manages external port 80/443)
- **Database**: Azure PostgreSQL or configurable via `DATABASE_URL`
- **Logging**: INFO level
- **HTTPS**: Enforced at Azure Web App level

## CI/CD Pipeline

The application uses GitHub Actions for automated CI/CD:

1. **Build & Test**: On every push and PR
   - Compile Java code
   - Run unit tests
   - Generate code coverage reports
   - Run Checkstyle validation

2. **Security Scanning**: On every push
   - CodeQL analysis
   - Dependency vulnerability scanning
   - Container image scanning

3. **Deployment**: Automated based on triggers
   - **Main branch**: Deploy to production
   - **PR labels**: Deploy to test/QA/staging environments
   - **PR close**: Cleanup review environments

## Common Tasks

### Run Application Locally
```bash
mvn clean package
java -jar target/bookstore-v3-1.0.0-SNAPSHOT.jar
```

### Build Docker Container
```bash
mvn package
docker build . --build-arg VERSION=1.0.0-SNAPSHOT --tag bookstore:latest
docker run -p 8080:8080 bookstore:latest
```

### Run Tests
```bash
mvn test
```

### Deploy to Azure
```bash
# Via GitHub Actions (recommended)
git push origin main  # Triggers production deployment

# Manual deployment
az webapp config container set \
  --name bookstore-app \
  --resource-group bookstore-rg \
  --docker-custom-image-name myregistry.azurecr.io/bookstore:1.0.0
```

## Getting Help

### Documentation Issues
If you find errors or gaps in the documentation:
1. Open an issue on GitHub
2. Use the label `documentation`
3. Provide specific details about what's unclear or incorrect

### Technical Support
- **GitHub Issues**: For bugs and feature requests
- **Pull Requests**: For code contributions
- **Discussions**: For questions and community support

### Contributing
Contributions are welcome! Please:
1. Read the [Coding Guidelines](code-standards/coding-guidelines.md)
2. Follow the [Development Setup Guide](development/setup-guide.md)
3. Submit pull requests with clear descriptions
4. Ensure all tests pass
5. Update documentation as needed

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2025-10-29 | Initial comprehensive documentation |

## Document Maintenance

### Review Schedule
- **Monthly**: Review for accuracy and completeness
- **Per Release**: Update with new features and changes
- **Quarterly**: Major documentation audit

### Update Process
1. Make changes to documentation
2. Update `last_updated` date in frontmatter
3. Increment version number if significant changes
4. Submit PR for review
5. Update this index if adding new documents

## Feedback

We value your feedback on this documentation! Please:
- Report errors or omissions via GitHub Issues
- Suggest improvements in pull requests
- Share what works well and what could be better

---

**Last Updated**: 2025-10-29  
**Documentation Version**: 1.0.0  
**Application Version**: 1.12.2
