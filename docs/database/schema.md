---
author: Documentation Team
last_updated: 2025-10-29
version: 1.0.0
review_status: Initial Draft
---

# Database Schema Documentation

## Table of Contents

- [Overview](#overview)
- [Database Engine](#database-engine)
- [Schema Design](#schema-design)
- [Entity Relationship Diagram](#entity-relationship-diagram)
- [Table Specifications](#table-specifications)
- [Indexes](#indexes)
- [Data Migration Strategy](#data-migration-strategy)
- [Data Seeding](#data-seeding)

## Overview

The Bookstore Application uses a simple relational database schema to store book information. The schema is designed for demonstration purposes and uses an in-memory SQLite database by default, with support for external database connections.

### Design Principles

- **Simplicity**: Single table design for demo purposes
- **Flexibility**: Environment-configurable database connection
- **Performance**: In-memory database for fast development iterations
- **Portability**: SQLite compatibility for easy setup

## Database Engine

### Default Configuration (Development)

**Engine**: SQLite  
**Mode**: In-Memory  
**Connection String**: `jdbc:sqlite::memory:`

**Characteristics**:
- Database created on application startup
- Data lost when application stops
- No persistence between restarts
- Automatically populated with sample data
- Zero configuration required

### Production Configuration (Optional)

**Supported Engines**: 
- PostgreSQL (recommended for production)
- MySQL
- SQLite file-based

**Configuration via Environment Variables**:
```bash
DATABASE_URL=jdbc:postgresql://hostname:5432/bookstore
DATABASE_USER=bookstore_user
DATABASE_PASSWORD=secure_password
DATABASE_RETRIES=10  # Optional, defaults to 10
```

### Connection Management

**Retry Logic**:
- Maximum retries: 10 (configurable via `DATABASE_RETRIES`)
- Backoff period: 3000ms between retries
- Automatic reconnection on failure
- Logs connection attempts and failures

**Connection Pool**: 
- Currently: Single connection (in-memory database)
- Recommendation: Implement connection pooling for production (HikariCP, Apache DBCP)

## Schema Design

### Current Schema Version: 1.0

**Database**: bookstore (or in-memory)  
**Character Set**: UTF-8  
**Collation**: Default for database engine

## Entity Relationship Diagram

```
┌─────────────────────────────────────────────┐
│                  books                      │
├─────────────────────────────────────────────┤
│ PK │ id          │ INTEGER │ PRIMARY KEY   │
│    │ title       │ TEXT    │ NOT NULL      │
│    │ author      │ TEXT    │               │
│    │ image       │ TEXT    │               │
│    │ rating      │ REAL    │               │
└─────────────────────────────────────────────┘
```

**Notes**:
- Single table design (no relationships in current version)
- Future enhancement: Normalize into `books`, `authors`, and `categories` tables

## Table Specifications

### `books` Table

Stores information about books in the catalog.

#### Table Definition

```sql
CREATE TABLE IF NOT EXISTS books (
    id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    author TEXT,
    image TEXT,
    rating REAL
);
```

#### Column Specifications

| Column | Data Type | Nullable | Default | Description |
|--------|-----------|----------|---------|-------------|
| `id` | INTEGER | No | AUTO | Unique identifier for each book (Primary Key) |
| `title` | TEXT | No | - | Full title of the book |
| `author` | TEXT | Yes | NULL | Author's full name (may include multiple authors) |
| `image` | TEXT | Yes | NULL | Filename of book cover image |
| `rating` | REAL | Yes | NULL | Average rating (0.0 to 5.0 scale) |

#### Column Details

**id** (PRIMARY KEY)
- Auto-incrementing integer
- Uniquely identifies each book
- Cannot be null
- Used for future API endpoints (`GET /books/{id}`)

**title**
- Maximum length: Unlimited (TEXT type in SQLite)
- Contains full book title including series information
- Examples:
  - "The Hunger Games (The Hunger Games, #1)"
  - "Harry Potter and the Sorcerer's Stone (Harry Potter, #1)"
- Required field (NOT NULL constraint)
- Used for search and display

**author**
- Maximum length: Unlimited (TEXT type)
- May contain multiple authors separated by comma
- Examples:
  - "Suzanne Collins"
  - "J.K. Rowling, Mary GrandPré"
  - "George Orwell, Erich Fromm, Celâl Üster"
- Optional field (can be NULL)
- Used for display and future author-based search

**image**
- Maximum length: Unlimited (TEXT type)
- Stores filename only (not full path)
- References file in `/static/images/covers/` directory
- Examples:
  - "2767052.jpg"
  - "3.jpg"
  - "41865.jpg"
- Optional field (can be NULL)
- Used to display book cover images
- Missing images should have graceful fallback

**rating**
- Data type: REAL (floating point)
- Range: 0.0 to 5.0 (inclusive)
- Precision: Typically 2 decimal places (e.g., 4.34)
- Represents average reader rating
- Optional field (can be NULL, defaults to 0.0 in application)
- Used for filtering and sorting
- Displayed as star rating in UI

#### Constraints

**Primary Key**:
```sql
PRIMARY KEY (id)
```

**Not Null Constraints**:
```sql
title TEXT NOT NULL
```

**Check Constraints** (Recommended for production):
```sql
-- Add rating validation
ALTER TABLE books ADD CONSTRAINT check_rating 
CHECK (rating IS NULL OR (rating >= 0.0 AND rating <= 5.0));

-- Add title length validation
ALTER TABLE books ADD CONSTRAINT check_title_length 
CHECK (LENGTH(title) > 0 AND LENGTH(title) <= 500);
```

#### Sample Data

```sql
INSERT INTO books (title, author, image, rating) VALUES
('The Hunger Games (The Hunger Games, #1)', 'Suzanne Collins', '2767052.jpg', 4.34),
('Harry Potter and the Sorcerer''s Stone (Harry Potter, #1)', 'J.K. Rowling, Mary GrandPré', '3.jpg', 4.44),
('Twilight (Twilight, #1)', 'Stephenie Meyer', '41865.jpg', 3.57),
('To Kill a Mockingbird', 'Harper Lee', '2657.jpg', 4.25),
('The Great Gatsby', 'F. Scott Fitzgerald', '4671.jpg', 3.89);
```

## Indexes

### Current Indexes

**Primary Key Index** (Automatic):
```sql
-- Automatically created with PRIMARY KEY constraint
CREATE UNIQUE INDEX IF NOT EXISTS idx_books_id ON books(id);
```

### Recommended Indexes for Production

**Title Search Index**:
```sql
-- For efficient title-based searches
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
```

**Rating Filter Index**:
```sql
-- For efficient rating-based filtering
CREATE INDEX IF NOT EXISTS idx_books_rating ON books(rating DESC);
```

**Author Search Index**:
```sql
-- For future author-based searches
CREATE INDEX IF NOT EXISTS idx_books_author ON books(author);
```

**Composite Index for Rating Queries**:
```sql
-- Optimizes "rating >= X ORDER BY rating DESC" queries
CREATE INDEX IF NOT EXISTS idx_books_rating_desc ON books(rating DESC, id);
```

### Index Performance Impact

| Index | Query Type | Performance Gain | Storage Overhead |
|-------|-----------|------------------|------------------|
| idx_books_title | LIKE '%keyword%' | Moderate | Low |
| idx_books_rating | WHERE rating >= X | High | Very Low |
| idx_books_author | WHERE author = X | High | Low |

## Data Migration Strategy

### Version Control

**Current Version**: 1.0.0  
**Schema Changes**: Tracked in application version control

### Migration Approach

**For In-Memory Database**:
- No migrations needed (database recreated on startup)
- Schema changes applied automatically

**For Production Database**:
1. **Flyway** (Recommended):
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>8.5.0</version>
</dependency>
```

2. **Migration Files**:
```
src/main/resources/db/migration/
├── V1__Initial_schema.sql
├── V2__Add_category_table.sql
└── V3__Add_rating_index.sql
```

3. **Migration Naming Convention**:
- Format: `V{version}__{description}.sql`
- Example: `V1.0.0__Initial_schema.sql`

### Backward Compatibility

**Strategy**:
- Always add columns as nullable initially
- Deprecate columns before removal
- Maintain compatibility for 2 major versions
- Document breaking changes in release notes

### Rollback Strategy

**Approach**:
1. Create backup before migration
2. Test migration on staging environment
3. Keep previous version container image
4. Document rollback procedures

**Rollback Steps**:
```bash
# 1. Stop application
docker stop bookstore

# 2. Restore database backup
psql -U bookstore -d bookstore < backup_YYYYMMDD.sql

# 3. Deploy previous version
docker run -d previous-version-image

# 4. Verify application health
curl http://localhost:8080/status
```

## Data Seeding

### Sample Data Source

**Location**: `/data/books.csv`  
**Format**: CSV with header row  
**Encoding**: UTF-8  
**Record Count**: ~1000 books (from Goodreads dataset)

### CSV Schema

```
book_id,goodreads_book_id,best_book_id,work_id,books_count,isbn,isbn13,
authors,original_publication_year,original_title,title,language_code,
average_rating,ratings_count,work_ratings_count,work_text_reviews_count,
ratings_1,ratings_2,ratings_3,ratings_4,ratings_5,image_url,small_image_url
```

### Seeding Process

**Automatic Seeding** (In-Memory Database):
1. Application starts and detects in-memory database
2. `BookDatabaseImpl.initializeAndPopulateDatabase()` is called
3. Table schema is created
4. `BookUtils.getSampleBooks()` loads books from CSV
5. Books are inserted via `populate()` method
6. Logging confirms number of books loaded

**Manual Seeding** (External Database):
```java
// For production database with empty books table
BookService service = new BookService();
Collection<Book> sampleBooks = BookUtils.getSampleBooks();
service.populate(sampleBooks);
```

### Sample Data Characteristics

- **Total Books**: Varies (subset of Goodreads dataset)
- **Rating Range**: 0.0 to 5.0
- **Genres**: Mixed (fiction, non-fiction, classics, contemporary)
- **Languages**: Primarily English
- **Time Period**: Books from 1800s to modern day

### Custom Data Loading

**Load from CSV**:
```java
public static Collection<Book> loadFromCSV(String filename) {
    List<Book> books = new ArrayList<>();
    // Read CSV file
    // Parse each line
    // Create Book objects
    // Add to collection
    return books;
}
```

**Load from JSON**:
```java
public static Collection<Book> loadFromJSON(String filename) {
    // Parse JSON array
    // Map to Book objects
    // Return collection
}
```

## Database Maintenance

### Backup Strategy (Production)

**Frequency**:
- Full backup: Daily
- Incremental backup: Hourly
- Transaction log backup: Every 15 minutes

**Backup Command** (PostgreSQL):
```bash
pg_dump -U bookstore -d bookstore -F c -b -v -f /backups/bookstore_$(date +%Y%m%d_%H%M%S).backup
```

**Retention Policy**:
- Daily backups: 30 days
- Weekly backups: 90 days
- Monthly backups: 1 year

### Data Archival

**Strategy**:
- Archive old ratings after 2 years
- Maintain audit log for deleted books
- Compress historical data

**Archival Table**:
```sql
CREATE TABLE books_archive (
    id INTEGER,
    title TEXT,
    author TEXT,
    image TEXT,
    rating REAL,
    archived_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Vacuum and Optimization (SQLite)

```sql
-- Reclaim unused space
VACUUM;

-- Update statistics for query optimizer
ANALYZE;

-- Rebuild indexes
REINDEX books;
```

## Data Integrity

### Validation Rules

1. **Title Validation**:
   - Required field
   - Minimum length: 1 character
   - Maximum length: 500 characters
   - No HTML or special characters

2. **Author Validation**:
   - Optional field
   - Maximum length: 200 characters
   - Format: "FirstName LastName" or "Author1, Author2"

3. **Rating Validation**:
   - Range: 0.0 to 5.0
   - Precision: 2 decimal places
   - Default: 0.0 (if NULL)

4. **Image Validation**:
   - Optional field
   - Valid filename format
   - Corresponding file must exist in `/static/images/covers/`

### Referential Integrity

**Current State**: No foreign keys (single table design)

**Future Enhancement**:
```sql
-- Authors table
CREATE TABLE authors (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    biography TEXT
);

-- Book-Author relationship (many-to-many)
CREATE TABLE book_authors (
    book_id INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    PRIMARY KEY (book_id, author_id),
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE CASCADE
);
```

## Performance Tuning

### Query Optimization

**Slow Query Detection**:
```sql
-- Enable query logging (PostgreSQL)
ALTER DATABASE bookstore SET log_min_duration_statement = 100;
```

**Common Slow Queries**:
1. Title search with `LIKE '%keyword%'` - Add full-text search index
2. Unindexed rating filter - Add rating index
3. Large result sets - Implement pagination

### Connection Pool Configuration (Recommended)

**HikariCP Configuration**:
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:5432/bookstore");
config.setUsername("bookstore_user");
config.setPassword("secure_password");
config.setMaximumPoolSize(10);
config.setMinimumIdle(5);
config.setIdleTimeout(300000);
config.setConnectionTimeout(20000);

HikariDataSource ds = new HikariDataSource(config);
```

## Security Considerations

### SQL Injection Prevention

**Current Status**:
- ✅ `getBooksByRating()` uses prepared statements
- ⚠️ `getBooksByTitle()` uses string concatenation (vulnerable)

**Fix Required**:
```java
// Current vulnerable code
String query = "SELECT * FROM books WHERE title LIKE '%" + name + "%'";

// Secure alternative
String query = "SELECT * FROM books WHERE title LIKE ?";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setString(1, "%" + name + "%");
```

### Data Encryption

**Recommendations**:
- Encrypt database connection (SSL/TLS)
- Encrypt sensitive data at rest
- Use Azure Key Vault for credentials

```bash
DATABASE_URL="jdbc:postgresql://hostname:5432/bookstore?ssl=true&sslmode=require"
```

### Access Control

**Database User Permissions**:
```sql
-- Create read-only user for reporting
CREATE USER bookstore_readonly WITH PASSWORD 'readonly_password';
GRANT SELECT ON ALL TABLES IN SCHEMA public TO bookstore_readonly;

-- Create application user with limited permissions
CREATE USER bookstore_app WITH PASSWORD 'app_password';
GRANT SELECT, INSERT, UPDATE ON books TO bookstore_app;
```

## Future Schema Enhancements

### Planned Tables

1. **users** - User accounts and profiles
2. **categories** - Book categories/genres
3. **book_categories** - Many-to-many relationship
4. **reviews** - User book reviews
5. **cart_items** - Shopping cart entries
6. **orders** - Purchase orders
7. **order_items** - Order line items

### Normalization Improvements

**Current**: Single table with repeated author names  
**Proposed**: Normalized schema with separate authors table

**Benefits**:
- Eliminate data redundancy
- Easier author management
- Support for author metadata (biography, photo)
- Better query performance for author-based searches

## References

- [System Architecture](../architecture/system-overview.md)
- [API Documentation](../api/api-reference.md)
- [Developer Setup Guide](../development/setup-guide.md)
- [Operations Guide](../operations/deployment-guide.md)
