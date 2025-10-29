---
author: Documentation Team
last_updated: 2025-10-29
version: 1.0.0
review_status: Initial Draft
---

# API Reference Documentation

## Table of Contents

- [Overview](#overview)
- [Base URL](#base-url)
- [Authentication](#authentication)
- [Endpoints](#endpoints)
  - [Root Endpoint](#root-endpoint)
  - [Status Endpoint](#status-endpoint)
  - [Static Resources](#static-resources)
- [Data Models](#data-models)
- [Error Handling](#error-handling)
- [Rate Limiting](#rate-limiting)

## Overview

The Bookstore Application provides a simple HTTP-based API for browsing and filtering books. The API follows RESTful principles and returns HTML content rendered via server-side templates (Thymeleaf).

### API Characteristics

- **Protocol**: HTTP/1.1
- **Content Type**: HTML (text/html; charset=UTF-8)
- **Template Engine**: Thymeleaf
- **Response Format**: Server-rendered HTML pages
- **State**: Stateless (no session management currently implemented)

## Base URL

### Local Development
```
http://localhost:8080
```

### Azure Production
```
https://{app-name}.azurewebsites.net
```

### Environment-Specific
- **Test**: `https://{app-name}-test.azurewebsites.net`
- **QA**: `https://{app-name}-qa.azurewebsites.net`
- **Staging**: `https://{app-name}-staging.azurewebsites.net`

## Authentication

**Current Implementation**: No authentication required (demo application)

**Future Considerations**: 
- OAuth 2.0 / OpenID Connect for user authentication
- API keys for programmatic access
- JWT tokens for session management

## Endpoints

### Root Endpoint

#### Get All Books

Retrieves and displays all books in the catalog.

**Endpoint**: `GET /`

**Parameters**: None

**Request Example**:
```http
GET / HTTP/1.1
Host: localhost:8080
Accept: text/html
```

**Response Example**:
```http
HTTP/1.1 200 OK
Content-Type: text/html; charset=UTF-8
Content-Length: 15234

<!DOCTYPE html>
<html>
  <head><title>Bookstore</title></head>
  <body>
    <!-- Rendered book listing -->
  </body>
</html>
```

**Response Codes**:
- `200 OK` - Books retrieved successfully
- `500 Internal Server Error` - Database connection error or internal server error

**Template Variables**:
| Variable | Type | Description |
|----------|------|-------------|
| `books` | `List<Book>` | Collection of all books in catalog |
| `version` | `String` | Application version number |
| `modified` | `Calendar` | Current timestamp |

---

#### Filter Books by Rating

Retrieves and displays books filtered by minimum rating.

**Endpoint**: `GET /?rating={minRating}`

**Parameters**:
| Parameter | Type | Required | Description | Valid Values |
|-----------|------|----------|-------------|--------------|
| `rating` | double | No | Minimum rating threshold | 0.0 - 5.0 |

**Request Example**:
```http
GET /?rating=4.0 HTTP/1.1
Host: localhost:8080
Accept: text/html
```

**Query Parameter Details**:
- **rating**: Filters books to show only those with rating >= specified value
- Empty or invalid rating parameter shows all books
- Non-numeric values are ignored, defaults to showing all books

**Response Example**:
```http
HTTP/1.1 200 OK
Content-Type: text/html; charset=UTF-8

<!DOCTYPE html>
<html>
  <!-- Books with rating >= 4.0 -->
</html>
```

**Response Codes**:
- `200 OK` - Filtered books retrieved successfully
- `500 Internal Server Error` - Database error

**Template Variables**:
| Variable | Type | Description |
|----------|------|-------------|
| `books` | `List<Book>` | Filtered collection of books |
| `filterType` | `String` | Type of filter applied ("rating") |
| `filterValue` | `String` | Value of filter applied (e.g., "4.0") |
| `version` | `String` | Application version number |
| `modified` | `Calendar` | Current timestamp |

**Examples**:

Get books with 4+ stars:
```bash
curl http://localhost:8080/?rating=4.0
```

Get books with 3.5+ stars:
```bash
curl http://localhost:8080/?rating=3.5
```

Get all books (no filter):
```bash
curl http://localhost:8080/
```

---

### Status Endpoint

#### Get Application Status

Returns application health status information.

**Endpoint**: `GET /status`

**Parameters**: None

**Request Example**:
```http
GET /status HTTP/1.1
Host: localhost:8080
Accept: text/html
```

**Response Example**:
```http
HTTP/1.1 200 OK
Content-Type: text/html; charset=UTF-8

<!DOCTYPE html>
<html>
  <body>
    <h1>Application Status</h1>
    <p>Status: OK</p>
    <p>Version: 1.0.0-SNAPSHOT</p>
  </body>
</html>
```

**Response Codes**:
- `200 OK` - Application is healthy and operational

**Use Cases**:
- Health checks for load balancers
- Monitoring system probes
- Deployment validation
- Uptime monitoring

---

### Static Resources

#### Get Static Files

Serves static assets including images, CSS, and JavaScript files.

**Endpoint**: `GET /static/*`

**Parameters**: 
| Parameter | Type | Description |
|-----------|------|-------------|
| `path` | String | Path to static resource |

**Request Examples**:
```http
GET /static/bootstrap-3.4.1-dist/css/bootstrap.min.css HTTP/1.1
Host: localhost:8080
```

```http
GET /static/images/covers/2767052.jpg HTTP/1.1
Host: localhost:8080
```

**Available Static Resources**:
- `/static/bootstrap-3.4.1-dist/` - Bootstrap framework files
  - `css/bootstrap.min.css` - Bootstrap styles
  - `js/bootstrap.min.js` - Bootstrap JavaScript
- `/static/images/` - Image assets
  - `covers/*.jpg` - Book cover images
  - `favicon.ico` - Site favicon
  - `octocat.png` - GitHub Octocat logo
  - `invertocat.svg` - Inverted Octocat logo
  - `heart.png` - Heart icon

**Response Codes**:
- `200 OK` - Resource found and returned
- `404 Not Found` - Resource does not exist
- `304 Not Modified` - Resource not modified (with conditional request headers)

**Cache Control**:
Static resources are served with appropriate caching headers for optimal performance.

---

## Data Models

### Book Model

Represents a book entity in the system.

**Fields**:
| Field | Type | Required | Description | Constraints |
|-------|------|----------|-------------|-------------|
| `title` | String | Yes | Book title | Non-empty string |
| `author` | String | Yes | Book author name | Non-empty string |
| `cover` | String | No | Cover image filename | Valid filename with extension |
| `rating` | double | No | Book rating | 0.0 - 5.0 (inclusive) |

**JSON Representation** (for future API endpoints):
```json
{
  "title": "The Hunger Games",
  "author": "Suzanne Collins",
  "cover": "2767052.jpg",
  "rating": 4.34
}
```

**Validation Rules**:
- Title cannot be null or empty
- Author cannot be null or empty
- Rating must be between 0.0 and 5.0 (inclusive)
- Cover is optional and defaults to empty string

**Business Rules**:
- Books are displayed in database order (insertion order for in-memory DB)
- When filtering by rating, books are ordered by rating descending
- Star ratings are displayed as visual stars (★) in the UI
- Price is currently hardcoded to $13.99 (not stored in model)

---

## Error Handling

### Error Response Format

When an error occurs, the application renders an error page using Thymeleaf.

**Error Template Variables**:
| Variable | Type | Description |
|----------|------|-------------|
| `error` | String | Error message describing what went wrong |
| `version` | String | Application version |
| `modified` | Calendar | Timestamp of error |

**Common Error Scenarios**:

#### 1. Database Connection Error

**Cause**: Unable to connect to database

**Status Code**: `500 Internal Server Error`

**Error Message**:
```
Database connection is not valid, check logs for failure details.
```

**Resolution**:
- Check database connection parameters
- Verify DATABASE_URL environment variable
- Review application logs for detailed error information

#### 2. Database Query Error

**Cause**: SQL query execution failed

**Status Code**: `500 Internal Server Error`

**Error Message**:
```
Failed to obtain books
```

**Resolution**:
- Check database integrity
- Review application logs for SQL errors
- Verify database schema is correctly initialized

#### 3. Invalid Parameter

**Cause**: Non-numeric rating parameter provided

**Status Code**: `200 OK` (graceful degradation)

**Behavior**: Shows all books (ignores invalid parameter)

**Example**:
```
GET /?rating=invalid
```
Result: Displays all books without filtering

#### 4. Template Rendering Error

**Cause**: Error rendering Thymeleaf template

**Status Code**: `500 Internal Server Error`

**Resolution**:
- Verify template files exist in classpath
- Check template syntax
- Review application logs for template errors

---

## HTTP Methods Support

| Endpoint | GET | POST | PUT | DELETE |
|----------|-----|------|-----|--------|
| `/` | ✅ | ✅ (aliased to GET) | ❌ | ❌ |
| `/status` | ✅ | ❌ | ❌ | ❌ |
| `/static/*` | ✅ | ❌ | ❌ | ❌ |

**Note**: POST requests to root endpoint (`/`) are currently aliased to GET behavior (no form data processing).

---

## Rate Limiting

**Current Implementation**: No rate limiting

**Recommendations for Production**:
- Implement rate limiting at load balancer/API gateway level
- Suggested limits:
  - 100 requests per minute per IP for anonymous users
  - 1000 requests per minute per API key for authenticated users
- Return `429 Too Many Requests` when limit exceeded
- Include `Retry-After` header with rate limit reset time

---

## Versioning

**Current Approach**: No API versioning (demo application)

**Future Recommendations**:
- URI versioning: `/api/v1/books`, `/api/v2/books`
- Header versioning: `Accept: application/vnd.bookstore.v1+json`
- Maintain backward compatibility for at least 2 major versions

---

## CORS (Cross-Origin Resource Sharing)

**Current Implementation**: CORS not configured

**Recommendations for Production**:
```java
// Example CORS configuration for future implementation
response.setHeader("Access-Control-Allow-Origin", "https://trusted-domain.com");
response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
```

---

## Security Considerations

### Current Security Measures

1. **SQL Injection Prevention**: Partial
   - `getBooksByRating()` uses prepared statements ✅
   - `getBooksByTitle()` uses string concatenation ⚠️ (vulnerable)

2. **XSS Prevention**: 
   - Thymeleaf automatically escapes output ✅

3. **HTTPS**: 
   - Not enforced at application level
   - Should be handled by load balancer/reverse proxy

### Security Recommendations

1. **Fix SQL Injection**:
```java
// Replace string concatenation with prepared statement
String query = "SELECT * FROM books WHERE title LIKE ?";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setString(1, "%" + name + "%");
```

2. **Add Security Headers**:
```java
response.setHeader("X-Content-Type-Options", "nosniff");
response.setHeader("X-Frame-Options", "DENY");
response.setHeader("X-XSS-Protection", "1; mode=block");
response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
```

3. **Implement Authentication**: Add OAuth 2.0 or JWT-based authentication

4. **Input Validation**: Validate all query parameters and form inputs

---

## Performance Considerations

### Response Times

| Endpoint | Expected Response Time | Notes |
|----------|----------------------|-------|
| `GET /` | < 100ms | In-memory database |
| `GET /?rating=X` | < 100ms | Indexed query |
| `GET /status` | < 10ms | Static content |
| `GET /static/*` | < 50ms | Cached resources |

### Optimization Recommendations

1. **Enable HTTP/2**: Reduce latency for static resources
2. **Add ETag Headers**: Enable client-side caching
3. **Implement CDN**: Serve static assets from edge locations
4. **Database Connection Pooling**: Reuse database connections
5. **Template Caching**: Cache compiled Thymeleaf templates

---

## Examples & Use Cases

### Use Case 1: Browse All Books

**Scenario**: User wants to see entire book catalog

**Steps**:
1. Navigate to application home page
2. View all books with ratings and covers

**API Call**:
```bash
curl http://localhost:8080/
```

---

### Use Case 2: Find Highly Rated Books

**Scenario**: User wants to see only books with 4+ star ratings

**Steps**:
1. Select "4+ Stars" from rating filter dropdown
2. Form submits automatically
3. View filtered results

**API Call**:
```bash
curl "http://localhost:8080/?rating=4.0"
```

---

### Use Case 3: Health Check for Load Balancer

**Scenario**: Load balancer needs to verify application health

**Steps**:
1. Load balancer periodically sends GET request to `/status`
2. If response is 200 OK, route traffic to instance
3. If response is error or timeout, remove instance from rotation

**API Call**:
```bash
curl -I http://localhost:8080/status
```

---

## Future API Enhancements

### Planned Features

1. **JSON API Endpoints**: Add REST API with JSON responses
```
GET /api/v1/books - List all books as JSON
GET /api/v1/books/{id} - Get single book by ID
POST /api/v1/books - Create new book (admin only)
PUT /api/v1/books/{id} - Update book
DELETE /api/v1/books/{id} - Delete book
GET /api/v1/books/search?q={query} - Full-text search
```

2. **Pagination**: Support large book catalogs
```
GET /api/v1/books?page=1&limit=20
```

3. **Sorting**: Allow custom sort order
```
GET /api/v1/books?sort=rating&order=desc
```

4. **Advanced Filtering**: Multiple filter criteria
```
GET /api/v1/books?rating=4.0&author=Tolkien
```

5. **Shopping Cart API**: Add cart management
```
POST /api/v1/cart/items
GET /api/v1/cart
DELETE /api/v1/cart/items/{id}
```

---

## References

- [System Architecture](../architecture/system-overview.md)
- [Database Schema](../database/schema.md)
- [Developer Setup Guide](../development/setup-guide.md)
- [Security Documentation](../security/security-overview.md)
