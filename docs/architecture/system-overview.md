---
author: Documentation Team
last_updated: 2025-10-29
version: 1.0.0
review_status: Initial Draft
---

# Bookstore Application - System Overview

## Table of Contents

- [Introduction](#introduction)
- [System Architecture](#system-architecture)
- [Technology Stack](#technology-stack)
- [Component Overview](#component-overview)
- [Data Flow](#data-flow)
- [Deployment Architecture](#deployment-architecture)
- [Integration Points](#integration-points)

## Introduction

The Bookstore Application is a web-based book catalog and browsing system built with Java, demonstrating modern DevOps practices, security scanning, and cloud deployment capabilities. The application serves as a demonstration platform for GitHub's development workflow, security features (GHAS), and Azure cloud deployment.

### Purpose

- Provide a searchable catalog of books with ratings and cover images
- Demonstrate CI/CD pipelines with GitHub Actions
- Showcase security scanning capabilities (CodeQL, container scanning, dependency review)
- Illustrate cloud deployment patterns to Azure Web Apps
- Serve as a reference implementation for Java web applications

### Key Features

- Browse complete book catalog
- Filter books by minimum rating
- View book details including author, title, cover image, and ratings
- Real-time star rating display
- Responsive design for mobile and desktop
- In-memory SQLite database with sample data
- RESTful API architecture

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         Client Layer                         │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐              │
│  │  Browser  │  │  Mobile   │  │   Tablet  │              │
│  └─────┬─────┘  └─────┬─────┘  └─────┬─────┘              │
│        │              │              │                      │
└────────┼──────────────┼──────────────┼──────────────────────┘
         │              │              │
         └──────────────┼──────────────┘
                        │ HTTP/HTTPS
         ┌──────────────┴──────────────┐
         │                             │
┌────────▼─────────────────────────────▼────────────────────┐
│                    Web Server Layer                        │
│  ┌─────────────────────────────────────────────────┐      │
│  │         Jetty Embedded Web Server               │      │
│  │         (Port 8080 - Configurable)              │      │
│  └─────┬───────────────────────────────────────┬───┘      │
│        │                                       │          │
│  ┌─────▼──────────┐                  ┌────────▼────────┐  │
│  │ Static Content │                  │   Servlets      │  │
│  │   Handler      │                  │   Handler       │  │
│  └────────────────┘                  └────────┬────────┘  │
│                                               │          │
└───────────────────────────────────────────────┼──────────┘
                                                │
┌───────────────────────────────────────────────▼──────────┐
│                  Application Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │BookServlet   │  │StatusServlet │  │Template Engine │  │
│  └──────┬───────┘  └──────────────┘  └────────────────┘  │
│         │                                                 │
│  ┌──────▼───────┐                                         │
│  │ BookService  │  Business Logic Layer                   │
│  └──────┬───────┘                                         │
└─────────┼─────────────────────────────────────────────────┘
          │
┌─────────▼─────────────────────────────────────────────────┐
│                   Data Access Layer                        │
│  ┌───────────────────┐      ┌──────────────────┐          │
│  │  BookDatabase     │      │   BookUtils      │          │
│  │  (Interface)      │      │   (Utilities)    │          │
│  └────────┬──────────┘      └──────────────────┘          │
│           │                                                │
│  ┌────────▼──────────┐                                     │
│  │ BookDatabaseImpl  │  JDBC Connection Management        │
│  └────────┬──────────┘                                     │
└───────────┼────────────────────────────────────────────────┘
            │
┌───────────▼────────────────────────────────────────────────┐
│                    Database Layer                          │
│  ┌─────────────────────────────────────────────────┐      │
│  │         SQLite In-Memory Database               │      │
│  │  ┌──────────────┐                               │      │
│  │  │  books       │  Table with book records      │      │
│  │  │  - id        │                               │      │
│  │  │  - title     │                               │      │
│  │  │  - author    │                               │      │
│  │  │  - image     │                               │      │
│  │  │  - rating    │                               │      │
│  │  └──────────────┘                               │      │
│  └─────────────────────────────────────────────────┘      │
└────────────────────────────────────────────────────────────┘
```

## Technology Stack

### Backend Technologies

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Runtime** | Eclipse Temurin JRE | 11.0.14_9 | Java Runtime Environment (Alpine Linux base) |
| **Language** | Java | 11 | Core programming language |
| **Build Tool** | Apache Maven | 3.x | Dependency management and build automation |
| **Web Server** | Eclipse Jetty | 10.0.0 | Embedded servlet container |
| **Template Engine** | Thymeleaf | 3.0.12 | Server-side template rendering |
| **Database** | SQLite JDBC | 3.32.3.2 | Embedded database with JDBC driver |
| **JSON Processing** | org.json | 20210307 | JSON parsing and generation |
| **Logging** | Log4j 2 (SLF4J) | 2.17.2 | Application logging framework |

### Frontend Technologies

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **CSS Framework** | Bootstrap | 3.4.1 | Responsive UI components and styling |
| **Icons** | Glyphicons | (included in Bootstrap) | UI icons |
| **Template Engine** | Thymeleaf | 3.0.12 | Dynamic HTML generation |

### Development & Testing

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Testing Framework** | JUnit | 4.13 | Unit and integration testing |
| **Code Coverage** | JaCoCo | 0.8.6 | Code coverage analysis |
| **Code Quality** | Checkstyle | 8.29 | Code style enforcement |

### Infrastructure & Deployment

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **Container Runtime** | Docker | Application containerization |
| **Base Image** | eclipse-temurin:11-jre-alpine | Lightweight JRE container image |
| **Cloud Platform** | Azure Web Apps | Production hosting environment |
| **CI/CD** | GitHub Actions | Automated build, test, and deployment |
| **IaC** | Bicep | Azure infrastructure as code |
| **Configuration Management** | Ansible | Environment cleanup and management |

### Security & Monitoring

| Component | Technology | Purpose |
|-----------|-----------|---------|
| **SAST** | CodeQL | Static application security testing |
| **Container Scanning** | GitHub Container Scanning | Vulnerability detection in containers |
| **Dependency Scanning** | Dependency Review | Security vulnerability detection in dependencies |

## Component Overview

### Core Components

#### 1. DemoServer (Entry Point)

**Location**: `com.github.demo.DemoServer`

**Responsibilities**:
- Initialize Jetty web server
- Configure servlet context and routing
- Load static resources
- Manage server lifecycle (start, stop)
- Configure environment-specific settings

**Key Features**:
- Configurable server port via `SERVER_PORT` environment variable (default: 8080)
- Configurable static resources location via `STATIC_RESOURCES` environment variable
- Embedded static resources for standalone deployment
- Graceful startup and shutdown

#### 2. BookServlet (Request Handler)

**Location**: `com.github.demo.servlet.BookServlet`

**Responsibilities**:
- Handle HTTP GET/POST requests for book listing
- Process rating filter parameters
- Render book listing page using Thymeleaf templates
- Handle error scenarios

**Endpoints**:
- `GET /` - Display all books
- `GET /?rating={minRating}` - Display books filtered by minimum rating

#### 3. StatusServlet (Health Check)

**Location**: `com.github.demo.servlet.StatusServlet`

**Responsibilities**:
- Provide application health status
- Support monitoring and load balancer health checks

**Endpoints**:
- `GET /status` - Return application status

#### 4. BookService (Business Logic)

**Location**: `com.github.demo.service.BookService`

**Responsibilities**:
- Orchestrate book-related business operations
- Manage database connectivity
- Provide high-level API for book operations

**Key Methods**:
- `getBooks()` - Retrieve all books
- `searchBooks(String name)` - Search books by title
- `getBooksByRating(double minRating)` - Filter books by minimum rating

#### 5. BookDatabase Interface & Implementation

**Location**: 
- Interface: `com.github.demo.service.BookDatabase`
- Implementation: `com.github.demo.service.BookDatabaseImpl`

**Responsibilities**:
- Abstract database operations
- Implement CRUD operations for books
- Manage database connections with retry logic
- Handle database initialization and population

**Key Features**:
- Connection retry mechanism (configurable via `DATABASE_RETRIES`)
- Automatic in-memory database initialization
- Prepared statements to prevent SQL injection
- Connection pooling and lifecycle management

#### 6. Book Model

**Location**: `com.github.demo.model.Book`

**Responsibilities**:
- Represent book entity
- Encapsulate book properties

**Properties**:
- `title` (String) - Book title
- `author` (String) - Book author
- `cover` (String) - Cover image filename
- `rating` (double) - Book rating (0.0 - 5.0)

#### 7. BookUtils (Utility)

**Location**: `com.github.demo.service.BookUtils`

**Responsibilities**:
- Load sample book data from CSV
- Parse book data into Book objects
- Provide default dataset for development

## Data Flow

### Request Flow - Browse Books

```
1. User → Browser: Navigate to application URL
2. Browser → Jetty: HTTP GET /
3. Jetty → BookServlet: Route request
4. BookServlet → BookService: getBooks()
5. BookService → BookDatabaseImpl: getAll()
6. BookDatabaseImpl → SQLite: SELECT * FROM books
7. SQLite → BookDatabaseImpl: ResultSet with book records
8. BookDatabaseImpl → BookService: List<Book>
9. BookService → BookServlet: List<Book>
10. BookServlet → Thymeleaf: Render template with book data
11. Thymeleaf → BookServlet: Generated HTML
12. BookServlet → Jetty: HTTP Response with HTML
13. Jetty → Browser: Display rendered page
```

### Request Flow - Filter by Rating

```
1. User → Browser: Select rating filter (e.g., 4+ stars)
2. Browser → Jetty: HTTP GET /?rating=4.0
3. Jetty → BookServlet: Route request with query parameter
4. BookServlet: Parse rating parameter (4.0)
5. BookServlet → BookService: getBooksByRating(4.0)
6. BookService → BookDatabaseImpl: getBooksByRating(4.0)
7. BookDatabaseImpl → SQLite: SELECT * FROM books WHERE rating >= 4.0
8. SQLite → BookDatabaseImpl: Filtered ResultSet
9. BookDatabaseImpl → BookService: List<Book>
10. BookService → BookServlet: Filtered List<Book>
11. BookServlet → Thymeleaf: Render with filtered data
12. Thymeleaf → BookServlet: Generated HTML
13. BookServlet → Jetty: HTTP Response
14. Jetty → Browser: Display filtered results
```

## Deployment Architecture

### Deployment Environments

| Environment | Trigger | Purpose | URL Pattern |
|-------------|---------|---------|-------------|
| **Development** | Local build | Developer testing | http://localhost:8080 |
| **Test** | PR label: `deploy to test` | Feature testing | https://{app-name}-test.azurewebsites.net |
| **QA** | PR label: `deploy to qa` | Quality assurance | https://{app-name}-qa.azurewebsites.net |
| **Staging** | PR label: `deploy to staging` | Pre-production validation | https://{app-name}-staging.azurewebsites.net |
| **Production** | Merge to `main` branch | Live environment | https://{app-name}.azurewebsites.net |

## Integration Points

### GitHub Integration

- **GitHub Actions**: CI/CD pipeline automation
- **GitHub Container Registry**: Docker image storage and distribution
- **GitHub Security**: CodeQL analysis, Dependabot alerts, secret scanning
- **GitHub Packages**: Artifact storage

### Azure Integration

- **Azure Web Apps**: Application hosting
- **Azure Container Registry** (optional): Private container images
- **Azure Application Insights** (optional): Monitoring and diagnostics
- **Azure Key Vault** (optional): Secrets management

## References

- [API Documentation](../api/api-reference.md)
- [Database Schema](../database/schema.md)
- [Deployment Guide](../operations/deployment-guide.md)
- [Developer Setup Guide](../development/setup-guide.md)
- [Security Documentation](../security/security-overview.md)
