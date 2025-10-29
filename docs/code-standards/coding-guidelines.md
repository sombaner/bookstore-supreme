---
author: Documentation Team
last_updated: 2025-10-29
version: 1.0.0
review_status: Initial Draft
---

# Coding Guidelines and Standards

## Table of Contents

- [Overview](#overview)
- [General Principles](#general-principles)
- [Java Coding Standards](#java-coding-standards)
- [Code Organization](#code-organization)
- [Naming Conventions](#naming-conventions)
- [Documentation Standards](#documentation-standards)
- [Testing Standards](#testing-standards)
- [Security Guidelines](#security-guidelines)
- [Performance Best Practices](#performance-best-practices)
- [Code Review Checklist](#code-review-checklist)

## Overview

This document defines coding standards and best practices for the Bookstore Application. Following these guidelines ensures code quality, maintainability, and consistency across the codebase.

### Goals

- **Readability**: Code should be easy to understand
- **Maintainability**: Code should be easy to modify and extend
- **Consistency**: Code should follow uniform patterns
- **Quality**: Code should be robust and well-tested
- **Security**: Code should follow security best practices

## General Principles

### SOLID Principles

**S - Single Responsibility Principle**
```java
// Good - Single responsibility
public class BookService {
    public List<Book> getBooks() { /* ... */ }
}

public class BookValidator {
    public boolean validate(Book book) { /* ... */ }
}

// Bad - Multiple responsibilities
public class BookManager {
    public List<Book> getBooks() { /* ... */ }
    public boolean validate(Book book) { /* ... */ }
    public void sendEmail(String to) { /* ... */ }
}
```

**O - Open/Closed Principle**
```java
// Good - Open for extension, closed for modification
public interface BookDatabase {
    List<Book> getAll();
}

public class SqliteBookDatabase implements BookDatabase {
    public List<Book> getAll() { /* SQLite implementation */ }
}

public class PostgresBookDatabase implements BookDatabase {
    public List<Book> getAll() { /* PostgreSQL implementation */ }
}
```

**L - Liskov Substitution Principle**
```java
// Good - Subclass can replace parent
BookDatabase db = new BookDatabaseImpl();
List<Book> books = db.getAll();  // Works with any implementation
```

**I - Interface Segregation Principle**
```java
// Good - Specific interfaces
public interface Readable {
    List<Book> getAll();
}

public interface Writable {
    void save(Book book);
}

// Bad - Fat interface
public interface BookRepository {
    List<Book> getAll();
    void save(Book book);
    void delete(int id);
    void update(Book book);
    List<Book> search(String query);
    // ... many more methods
}
```

**D - Dependency Inversion Principle**
```java
// Good - Depend on abstractions
public class BookService {
    private final BookDatabase database;
    
    public BookService(BookDatabase database) {
        this.database = database;
    }
}

// Bad - Depend on concrete class
public class BookService {
    private final BookDatabaseImpl database = new BookDatabaseImpl();
}
```

### DRY (Don't Repeat Yourself)

```java
// Good - Reusable method
private void logError(String message, Exception e) {
    logger.error(message, e);
}

public List<Book> getBooks() {
    try {
        return database.getAll();
    } catch (SQLException e) {
        logError("Failed to get books", e);
        throw new BookServiceException(e);
    }
}

// Bad - Repeated code
public List<Book> getBooks() {
    try {
        return database.getAll();
    } catch (SQLException e) {
        logger.error("Failed to get books", e);
        throw new BookServiceException(e);
    }
}

public List<Book> searchBooks(String query) {
    try {
        return database.search(query);
    } catch (SQLException e) {
        logger.error("Failed to search books", e);  // Repetition
        throw new BookServiceException(e);
    }
}
```

### KISS (Keep It Simple, Stupid)

```java
// Good - Simple and clear
public boolean hasHighRating(Book book) {
    return book.getRating() >= 4.0;
}

// Bad - Unnecessarily complex
public boolean hasHighRating(Book book) {
    return Optional.ofNullable(book)
        .map(Book::getRating)
        .filter(rating -> rating >= 4.0)
        .isPresent();
}
```

## Java Coding Standards

### Formatting

**Indentation**: 4 spaces (no tabs)
```java
public class Book {
    private String title;
    
    public String getTitle() {
        return title;
    }
}
```

**Line Length**: Maximum 120 characters
```java
// Good
public Book(String author, String title, String cover, double rating) {
    this.author = author;
    this.title = title;
    this.cover = cover;
    this.rating = rating;
}

// Bad - Too long
public Book(String author, String title, String cover, double rating, String publisher, String isbn, Date publicationDate, int pages) {
    // ...
}
```

**Braces**: Always use braces, even for single-line blocks
```java
// Good
if (book != null) {
    return book.getTitle();
}

// Bad
if (book != null)
    return book.getTitle();
```

**Blank Lines**:
- One blank line between methods
- One blank line between logical sections

```java
public class BookService {
    private BookDatabase database;
    
    public BookService() {
        // Constructor
    }
    
    public List<Book> getBooks() {
        // Method implementation
    }
    
    public List<Book> searchBooks(String query) {
        // Method implementation
    }
}
```

### Import Statements

**Order**:
1. Java standard library
2. Third-party libraries
3. Application classes

```java
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.demo.model.Book;
import com.github.demo.service.BookDatabase;
```

**Wildcard Imports**: Avoid
```java
// Good
import java.util.List;
import java.util.ArrayList;

// Bad
import java.util.*;
```

### Exception Handling

**Catch Specific Exceptions**:
```java
// Good
try {
    connection = DriverManager.getConnection(url, props);
} catch (SQLException e) {
    logger.error("Database connection failed", e);
    throw new BookServiceException(e);
}

// Bad
try {
    connection = DriverManager.getConnection(url, props);
} catch (Exception e) {  // Too broad
    // ...
}
```

**Never Swallow Exceptions**:
```java
// Good
try {
    stmt.close();
} catch (SQLException e) {
    logger.warn("Failed to close statement", e);
}

// Bad
try {
    stmt.close();
} catch (SQLException e) {
    // Ignored - BAD!
}
```

**Don't Catch Throwable**:
```java
// Bad - Don't do this
try {
    // code
} catch (Throwable t) {  // Catches errors too!
    // ...
}
```

### Resource Management

**Use try-with-resources**:
```java
// Good - Automatic resource management
try (PreparedStatement stmt = connection.prepareStatement(query)) {
    ResultSet rs = stmt.executeQuery();
    // Process results
} catch (SQLException e) {
    logger.error("Query failed", e);
}

// Bad - Manual resource management
PreparedStatement stmt = null;
try {
    stmt = connection.prepareStatement(query);
    // Process
} finally {
    if (stmt != null) {
        try {
            stmt.close();
        } catch (SQLException e) {
            // Ignore
        }
    }
}
```

## Code Organization

### Package Structure

```
com.github.demo
├── model/              # Data models
│   └── Book.java
├── service/            # Business logic
│   ├── BookService.java
│   ├── BookDatabase.java
│   ├── BookDatabaseImpl.java
│   ├── BookServiceException.java
│   └── BookUtils.java
├── servlet/            # Web layer
│   ├── BookServlet.java
│   └── StatusServlet.java
├── util/              # Utilities
│   └── ValidationUtils.java
└── DemoServer.java    # Application entry point
```

### Class Organization

**Order of elements**:
1. Static fields
2. Instance fields
3. Constructors
4. Public methods
5. Protected methods
6. Private methods
7. Inner classes

```java
public class BookService {
    // 1. Static fields
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);
    
    // 2. Instance fields
    private BookDatabase database;
    
    // 3. Constructor
    public BookService() {
        this.database = new BookDatabaseImpl();
    }
    
    // 4. Public methods
    public List<Book> getBooks() throws BookServiceException {
        return database.getAll();
    }
    
    // 5. Private methods
    private void validateDatabase() {
        if (database == null) {
            throw new IllegalStateException("Database not initialized");
        }
    }
}
```

## Naming Conventions

### Classes

**Format**: PascalCase, noun or noun phrase
```java
// Good
public class Book { }
public class BookService { }
public class BookServiceException { }

// Bad
public class book { }
public class bookService { }
public class GetBooks { }  // Verb, not noun
```

### Interfaces

**Format**: PascalCase, often adjective ending in "able"
```java
// Good
public interface Serializable { }
public interface Comparable { }
public interface BookDatabase { }

// Bad
public interface IBookDatabase { }  // Don't prefix with 'I'
public interface BookDatabaseInterface { }  // Redundant
```

### Methods

**Format**: camelCase, verb or verb phrase
```java
// Good
public List<Book> getBooks() { }
public void saveBook(Book book) { }
public boolean isValid() { }
public boolean hasBooks() { }

// Bad
public List<Book> Books() { }  // Missing verb
public void book_save() { }  // Wrong case
```

### Variables

**Format**: camelCase, meaningful names
```java
// Good
private String bookTitle;
private int bookCount;
private BookDatabase database;

// Bad
private String bt;  // Too short
private String book_title;  // Wrong case
private String strBookTitle;  // Hungarian notation
```

### Constants

**Format**: UPPER_SNAKE_CASE
```java
// Good
public static final int MAX_RETRIES = 10;
public static final String DATABASE_URL = "jdbc:sqlite::memory:";

// Bad
public static final int maxRetries = 10;
public static final String databaseUrl = "...";
```

### Boolean Variables and Methods

**Use positive names and question form**:
```java
// Good
boolean isValid;
boolean hasBooks;
boolean canEdit;

boolean isEmpty() { }
boolean isConnected() { }

// Bad
boolean notValid;
boolean doesntHaveBooks;
boolean cantEdit;
```

## Documentation Standards

### Javadoc

**All public classes and methods must have Javadoc**:

```java
/**
 * Service class for managing book operations.
 * Provides methods for retrieving, searching, and filtering books.
 */
public class BookService {
    
    /**
     * Retrieves all books from the database.
     *
     * @return list of all books, never null
     * @throws BookServiceException if database connection fails
     */
    public List<Book> getBooks() throws BookServiceException {
        // Implementation
    }
    
    /**
     * Searches for books by title.
     *
     * @param title the search query (partial match supported)
     * @return list of matching books, empty if no matches
     * @throws BookServiceException if database error occurs
     * @throws IllegalArgumentException if title is null or empty
     */
    public List<Book> searchBooks(String title) throws BookServiceException {
        // Implementation
    }
}
```

### Inline Comments

**Use for complex logic only**:

```java
// Good - Explains why
// Retry connection with exponential backoff to handle transient failures
for (int i = 0; i < maxRetries; i++) {
    try {
        connection = connect();
        break;
    } catch (SQLException e) {
        Thread.sleep((long) Math.pow(2, i) * 1000);
    }
}

// Bad - States the obvious
// Loop through all books
for (Book book : books) {
    // Add book to list
    result.add(book);
}
```

### TODO Comments

**Format**:
```java
// TODO: Implement pagination for large result sets
// TODO(username): Add caching for frequently accessed books
// FIXME: SQL injection vulnerability - use PreparedStatement
```

## Testing Standards

### Test Class Naming

```java
// Format: {ClassName}Test
public class BookServiceTest { }
public class BookDatabaseImplTest { }
```

### Test Method Naming

**Format**: `test{MethodName}_{Scenario}_{ExpectedResult}`

```java
@Test
public void testGetBooks_WithValidDatabase_ReturnsBooks() {
    // Test implementation
}

@Test
public void testGetBooksByRating_WithHighRating_ReturnsFilteredBooks() {
    // Test implementation
}

@Test
public void testSaveBook_WithNullBook_ThrowsException() {
    // Test implementation
}
```

### Test Structure

**Arrange-Act-Assert Pattern**:

```java
@Test
public void testGetBooksByRating_WithMinimumRating_ReturnsFilteredBooks() throws Exception {
    // Arrange
    BookService service = new BookService();
    double minRating = 4.0;
    
    // Act
    List<Book> books = service.getBooksByRating(minRating);
    
    // Assert
    assertNotNull("Books list should not be null", books);
    for (Book book : books) {
        assertTrue("All books should have rating >= 4.0", 
                   book.getRating() >= minRating);
    }
}
```

### Test Coverage

**Minimum Requirements**:
- Unit test coverage: ≥ 80%
- Integration test coverage: ≥ 60%
- Critical path coverage: 100%

## Security Guidelines

### Input Validation

**Always validate user input**:

```java
public List<Book> searchBooks(String query) throws BookServiceException {
    // Validate input
    if (query == null || query.trim().isEmpty()) {
        throw new IllegalArgumentException("Search query cannot be empty");
    }
    
    if (query.length() > 200) {
        throw new IllegalArgumentException("Search query too long");
    }
    
    // Sanitize input
    String sanitized = query.replaceAll("[^a-zA-Z0-9\\s]", "");
    
    return database.search(sanitized);
}
```

### SQL Injection Prevention

**Always use PreparedStatement**:

```java
// Good
String query = "SELECT * FROM books WHERE title LIKE ?";
PreparedStatement stmt = connection.prepareStatement(query);
stmt.setString(1, "%" + searchTerm + "%");

// Bad
String query = "SELECT * FROM books WHERE title LIKE '%" + searchTerm + "%'";
Statement stmt = connection.createStatement();
```

### Logging Security

**Never log sensitive data**:

```java
// Good
logger.info("User authentication successful: userId={}", userId);
logger.info("Database connection established: host={}", dbHost);

// Bad
logger.info("User password: {}", password);
logger.info("Database connection: url={}, user={}, password={}", 
           dbUrl, dbUser, dbPassword);
```

## Performance Best Practices

### Database Access

**Use connection pooling**:
```java
// Recommended for production
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost/bookstore");
config.setMaximumPoolSize(10);
HikariDataSource dataSource = new HikariDataSource(config);
```

**Close resources properly**:
```java
try (Connection conn = dataSource.getConnection();
     PreparedStatement stmt = conn.prepareStatement(query)) {
    // Use connection
}  // Auto-closed
```

### Collection Operations

**Use appropriate collection types**:

```java
// Good - Fast lookup
Map<Integer, Book> booksById = new HashMap<>();

// Good - Fast iteration
List<Book> booksList = new ArrayList<>();

// Bad - Slow for frequent adds/removes from middle
Vector<Book> books = new Vector<>();
```

### String Operations

**Use StringBuilder for concatenation in loops**:

```java
// Good
StringBuilder sb = new StringBuilder();
for (Book book : books) {
    sb.append(book.getTitle()).append(", ");
}
String result = sb.toString();

// Bad
String result = "";
for (Book book : books) {
    result += book.getTitle() + ", ";
}
```

## Code Review Checklist

### Functionality
- [ ] Code implements requirements correctly
- [ ] Edge cases are handled
- [ ] Error handling is appropriate
- [ ] No obvious bugs

### Code Quality
- [ ] Follows naming conventions
- [ ] Methods are focused and concise
- [ ] No code duplication
- [ ] Appropriate use of design patterns

### Security
- [ ] Input validation implemented
- [ ] SQL injection prevented
- [ ] No hardcoded credentials
- [ ] Sensitive data not logged

### Testing
- [ ] Unit tests provided
- [ ] Tests cover main scenarios
- [ ] Tests cover edge cases
- [ ] All tests pass

### Documentation
- [ ] Public methods have Javadoc
- [ ] Complex logic is commented
- [ ] README updated if needed
- [ ] API documentation updated

### Performance
- [ ] No obvious performance issues
- [ ] Database queries optimized
- [ ] Resources properly closed
- [ ] Appropriate data structures used

## References

- [Effective Java (Joshua Bloch)](https://www.oreilly.com/library/view/effective-java/9780134686097/)
- [Clean Code (Robert C. Martin)](https://www.oreilly.com/library/view/clean-code-a/9780136083238/)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Oracle Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-introduction.html)
- [Security Documentation](../security/security-overview.md)
