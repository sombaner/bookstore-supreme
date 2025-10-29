---
author: Documentation Team
last_updated: 2025-10-29
version: 1.0.0
review_status: Initial Draft
---

# Developer Setup Guide

## Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Development Environment Setup](#development-environment-setup)
- [Building the Application](#building-the-application)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Debugging](#debugging)
- [Common Issues and Troubleshooting](#common-issues-and-troubleshooting)

## Prerequisites

### Required Software

| Software | Minimum Version | Recommended Version | Purpose |
|----------|----------------|---------------------|---------|
| **Java JDK** | 11 | 11 or 17 | Compile and run Java code |
| **Apache Maven** | 3.6.3 | 3.8.x or later | Build automation and dependency management |
| **Git** | 2.x | Latest | Version control |

### Optional Software

| Software | Version | Purpose |
|----------|---------|---------|
| **Docker** | 20.x+ | Container builds and deployment |
| **IDE** | Latest | Code editing (IntelliJ IDEA, Eclipse, VS Code) |
| **Postman** | Latest | API testing |

### System Requirements

- **Operating System**: Linux, macOS, or Windows
- **RAM**: Minimum 4GB, Recommended 8GB+
- **Disk Space**: 2GB free space for dependencies and builds
- **Network**: Internet connection for downloading dependencies

## Quick Start

### For GitHub Codespaces (Recommended)

**Zero configuration required!** This repository is pre-configured for GitHub Codespaces.

1. **Open in Codespaces**:
   - Navigate to the repository on GitHub
   - Click the "Code" button
   - Select "Codespaces" tab
   - Click "Create codespace on main"

2. **Wait for environment setup** (2-3 minutes)
   - Pre-configured container includes JDK 11, Maven, Azure CLI
   - All dependencies automatically installed

3. **Build and run**:
   ```bash
   mvn package
   java -jar target/bookstore-v3-1.0.0-SNAPSHOT.jar
   ```

4. **Access the application**:
   - Codespaces will automatically forward port 8080
   - Click the notification to open in browser
   - Or navigate to the "Ports" tab and click the local address

### For Local Development

If you prefer local development, follow the [Development Environment Setup](#development-environment-setup) section below.

## Development Environment Setup

### 1. Install Java JDK 11

#### macOS (using Homebrew)
```bash
brew install openjdk@11

# Add to PATH
echo 'export PATH="/usr/local/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify installation
java -version
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-11-jdk

# Verify installation
java -version
javac -version
```

#### Windows
1. Download JDK 11 from [Adoptium](https://adoptium.net/)
2. Run the installer
3. Add Java to PATH:
   - System Properties → Environment Variables
   - Add `JAVA_HOME` → `C:\Program Files\Eclipse Adoptium\jdk-11.x.x`
   - Edit `Path` → Add `%JAVA_HOME%\bin`
4. Verify in Command Prompt:
   ```cmd
   java -version
   ```

### 2. Install Apache Maven

#### macOS (using Homebrew)
```bash
brew install maven

# Verify installation
mvn -version
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install maven

# Verify installation
mvn -version
```

#### Windows
1. Download Maven from [maven.apache.org](https://maven.apache.org/download.cgi)
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH:
   - Add `MAVEN_HOME` → `C:\Program Files\Apache\maven`
   - Edit `Path` → Add `%MAVEN_HOME%\bin`
4. Verify in Command Prompt:
   ```cmd
   mvn -version
   ```

### 3. Install Git

#### macOS
```bash
brew install git
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install git
```

#### Windows
Download and install from [git-scm.com](https://git-scm.com/download/win)

### 4. Clone the Repository

```bash
# Clone via HTTPS
git clone https://github.com/sombaner/bookstore-supreme.git

# OR clone via SSH (if SSH keys configured)
git clone git@github.com:sombaner/bookstore-supreme.git

# Navigate to project directory
cd bookstore-supreme
```

### 5. Configure IDE (Optional but Recommended)

#### IntelliJ IDEA

1. **Import Project**:
   - File → Open
   - Select `pom.xml` in project root
   - Click "Open as Project"

2. **Configure JDK**:
   - File → Project Structure → Project
   - Set Project SDK to JDK 11
   - Set Project language level to 11

3. **Maven Integration**:
   - IntelliJ automatically detects Maven
   - Maven tool window appears on right side
   - Click "Reload All Maven Projects" if needed

#### Visual Studio Code

1. **Install Extensions**:
   - Extension Pack for Java (Microsoft)
   - Maven for Java
   - Debugger for Java

2. **Open Project**:
   ```bash
   code bookstore-supreme
   ```

3. **Configure Java**:
   - Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
   - Type "Java: Configure Java Runtime"
   - Ensure JDK 11 is selected

#### Eclipse

1. **Import Maven Project**:
   - File → Import → Maven → Existing Maven Projects
   - Select project root directory
   - Click Finish

2. **Configure JDK**:
   - Right-click project → Properties
   - Java Build Path → Libraries
   - Ensure JDK 11 is configured

## Building the Application

### Initial Build

```bash
# Clean and build (downloads all dependencies)
mvn clean package

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: 15-30 seconds (first build)
```

**First Build Notes**:
- Downloads ~150MB of dependencies from Maven Central
- Creates `target/` directory with compiled classes
- Generates JAR file: `target/bookstore-v3-1.0.0-SNAPSHOT.jar`

### Build Commands

#### Full Build with Tests
```bash
mvn clean package
```

#### Build without Tests (faster)
```bash
mvn clean package -DskipTests
```

#### Build with Code Coverage
```bash
mvn clean package -Pcoverage-per-test
```

#### Verify Code Style
```bash
mvn checkstyle:check
```

#### Display Dependency Tree
```bash
mvn dependency:tree
```

#### Update Dependencies
```bash
mvn versions:display-dependency-updates
```

### Build Outputs

After successful build, the `target/` directory contains:

```
target/
├── bookstore-v3-1.0.0-SNAPSHOT.jar  # Executable JAR (uber-jar)
├── classes/                          # Compiled Java classes
├── generated-sources/                # Generated code
├── maven-archiver/                   # Maven metadata
├── maven-status/                     # Build status
└── test-classes/                     # Compiled test classes
```

### Understanding the JAR

The generated JAR is an "uber-jar" (fat JAR) that includes:
- All application classes
- All dependencies
- Static resources (HTML, CSS, JS, images)
- Configuration files

This allows the application to run standalone without external dependencies.

## Running the Application

### Method 1: Run JAR Directly (Recommended)

```bash
# Build first (if not already built)
mvn package

# Run the JAR
java -jar target/bookstore-v3-1.0.0-SNAPSHOT.jar
```

**Expected Output**:
```
17:59:10.123 [main] INFO  com.github.demo.DemoServer - Started DemoServer; available at: http://localhost:8080
**********************************************************************************************************
Started DemoServer; available at: http://localhost:8080
**********************************************************************************************************
```

**Access the Application**:
- Open browser: http://localhost:8080
- You should see the bookstore homepage with book listings

### Method 2: Run with Maven (Development)

```bash
# Maven exec plugin (if configured)
mvn exec:java -Dexec.mainClass="com.github.demo.DemoServer"
```

### Method 3: Run in IDE

#### IntelliJ IDEA
1. Open `src/main/java/com/github/demo/DemoServer.java`
2. Click green arrow next to `main` method
3. Select "Run 'DemoServer.main()'"

#### VS Code
1. Open `DemoServer.java`
2. Click "Run" above the `main` method
3. Or press `F5` to start debugging

#### Eclipse
1. Right-click `DemoServer.java`
2. Run As → Java Application

### Method 4: Run with Docker

```bash
# Build the container
mvn package
docker build . --build-arg VERSION=1.0.0-SNAPSHOT --tag bookstore:latest

# Run the container
docker run -p 8080:8080 bookstore:latest

# Run with custom port
docker run -p 9090:9090 -e SERVER_PORT=9090 bookstore:latest
```

### Environment Variables

Configure the application behavior with environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | 8080 | HTTP server port |
| `STATIC_RESOURCES` | embedded | Path to static resources (for development) |
| `DATABASE_URL` | jdbc:sqlite::memory: | Database connection URL |
| `DATABASE_USER` | none | Database username |
| `DATABASE_PASSWORD` | none | Database password |
| `DATABASE_RETRIES` | 10 | Max database connection retries |

**Example with Custom Configuration**:
```bash
# Set environment variables
export SERVER_PORT=9090
export DATABASE_URL=jdbc:postgresql://localhost:5432/bookstore
export DATABASE_USER=bookstore_user
export DATABASE_PASSWORD=mypassword

# Run application
java -jar target/bookstore-v3-1.0.0-SNAPSHOT.jar
```

### Stopping the Application

- **Terminal**: Press `Ctrl+C`
- **IDE**: Click stop button in run/debug window
- **Docker**: `docker stop <container_id>`

## Testing

### Running All Tests

```bash
# Run all tests
mvn test

# Expected output:
# [INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
```

### Running Specific Tests

```bash
# Run a single test class
mvn test -Dtest=BookServiceTest

# Run a single test method
mvn test -Dtest=BookServiceTest#testGetBooks

# Run tests matching a pattern
mvn test -Dtest=Book*Test
```

### Test Coverage

```bash
# Generate coverage report
mvn clean test -Pcoverage-per-test

# View report
open target/site/jacoco/index.html  # macOS
xdg-open target/site/jacoco/index.html  # Linux
start target/site/jacoco/index.html  # Windows
```

### Writing Tests

**Example Unit Test**:
```java
package com.github.demo.service;

import com.github.demo.model.Book;
import org.junit.Test;
import static org.junit.Assert.*;

public class BookServiceTest {
    
    @Test
    public void testGetBooks() throws BookServiceException {
        BookService service = new BookService();
        List<Book> books = service.getBooks();
        
        assertNotNull("Books list should not be null", books);
        assertTrue("Should have books", books.size() > 0);
    }
    
    @Test
    public void testGetBooksByRating() throws BookServiceException {
        BookService service = new BookService();
        List<Book> books = service.getBooksByRating(4.0);
        
        for (Book book : books) {
            assertTrue("Book rating should be >= 4.0", 
                      book.getRating() >= 4.0);
        }
    }
}
```

### Test Data

Tests use in-memory SQLite database populated with sample data from `BookUtils.getSampleBooks()`.

**Characteristics**:
- Isolated: Each test gets fresh database
- Fast: In-memory database
- Predictable: Same sample data every time

## Debugging

### Debug in IDE

#### IntelliJ IDEA

1. **Set Breakpoints**:
   - Click in gutter next to line number
   - Red dot appears

2. **Start Debug Mode**:
   - Click bug icon next to `main` method
   - Or right-click → Debug 'DemoServer.main()'

3. **Debug Controls**:
   - F7: Step Into
   - F8: Step Over
   - F9: Resume
   - View variables in Debug panel

#### VS Code

1. **Configure Debugger** (`.vscode/launch.json`):
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug DemoServer",
      "request": "launch",
      "mainClass": "com.github.demo.DemoServer",
      "projectName": "bookstore-v3"
    }
  ]
}
```

2. **Start Debugging**:
   - Press F5
   - Or Run → Start Debugging

### Remote Debugging

Debug a running application remotely:

```bash
# Run with debug port enabled
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 \
     -jar target/bookstore-v3-1.0.0-SNAPSHOT.jar
```

**Connect from IDE**:
1. Create Remote Debug configuration
2. Host: localhost
3. Port: 5005
4. Click Debug

### Logging

Adjust log levels for debugging:

**Edit `src/main/resources/log4j2.xml`**:
```xml
<Configuration status="INFO">
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <!-- Set to DEBUG for verbose logging -->
    <Root level="INFO">
      <AppenderRef ref="Console"/>
    </Root>
    
    <!-- Debug specific package -->
    <Logger name="com.github.demo" level="DEBUG"/>
  </Loggers>
</Configuration>
```

### HTTP Request Debugging

**Using curl**:
```bash
# Verbose output
curl -v http://localhost:8080/

# Include headers
curl -i http://localhost:8080/?rating=4.0

# Save response to file
curl -o response.html http://localhost:8080/
```

**Using browser DevTools**:
1. Open browser DevTools (F12)
2. Navigate to Network tab
3. Load page
4. Inspect requests and responses

## Common Issues and Troubleshooting

### Issue: "Port 8080 already in use"

**Symptom**:
```
java.net.BindException: Address already in use
```

**Solution 1 - Use different port**:
```bash
export SERVER_PORT=9090
java -jar target/bookstore-v3-1.0.0-SNAPSHOT.jar
```

**Solution 2 - Kill process using port**:
```bash
# macOS/Linux
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Issue: "Maven build fails - dependencies not downloaded"

**Symptom**:
```
Failed to read artifact descriptor for org.eclipse.jetty:jetty-server
```

**Solutions**:
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Force update dependencies
mvn clean package -U

# Check Maven settings
cat ~/.m2/settings.xml
```

### Issue: "Java version mismatch"

**Symptom**:
```
Unsupported class file major version 55
```

**Solution**:
```bash
# Check Java version
java -version  # Should be 11

# Check JAVA_HOME
echo $JAVA_HOME

# Set correct Java version (macOS)
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
```

### Issue: "OutOfMemoryError during build"

**Symptom**:
```
java.lang.OutOfMemoryError: Java heap space
```

**Solution**:
```bash
# Increase Maven memory
export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"

# Run build again
mvn clean package
```

### Issue: "Static resources not found"

**Symptom**:
- 404 errors for /static/* resources
- Missing images or CSS

**Solution**:
```bash
# Verify resources in JAR
jar tf target/bookstore-v3-1.0.0-SNAPSHOT.jar | grep static

# Check pom.xml resources configuration
# Ensure src/main/webapp is included in build
```

### Issue: "Database connection failed"

**Symptom**:
```
Database connection is not valid, check logs for failure details
```

**Solutions**:
```bash
# For in-memory database - should work automatically
# For external database - check configuration

# Verify DATABASE_URL
echo $DATABASE_URL

# Test database connection
psql -h localhost -U bookstore_user -d bookstore

# Check database logs
tail -f /var/log/postgresql/postgresql-main.log
```

### Issue: "Tests fail locally but pass in CI"

**Possible Causes**:
- Different Java version
- Cached test data
- Timezone differences
- Race conditions

**Solutions**:
```bash
# Clean everything
mvn clean
rm -rf target/

# Run with fresh cache
mvn test -U

# Check Java version matches CI
java -version  # Should match CI

# Run tests with same configuration as CI
mvn test -Pcoverage-per-test
```

### Getting Help

**Resources**:
- [GitHub Issues](https://github.com/sombaner/bookstore-supreme/issues)
- [System Architecture](../architecture/system-overview.md)
- [API Documentation](../api/api-reference.md)
- Maven Documentation: [maven.apache.org](https://maven.apache.org/)
- Jetty Documentation: [eclipse.org/jetty](https://www.eclipse.org/jetty/)

**Filing Bug Reports**:
Include:
1. Java version (`java -version`)
2. Maven version (`mvn -version`)
3. Operating system
4. Full error message
5. Steps to reproduce

## Next Steps

After setting up your development environment:

1. **Explore the Code**:
   - Review [Code Standards](../code-standards/coding-guidelines.md)
   - Read [Architecture Overview](../architecture/system-overview.md)

2. **Make Changes**:
   - Create feature branch: `git checkout -b feature/my-feature`
   - Follow [Contributing Guidelines](../../.github/CONTRIBUTING.md)

3. **Submit Pull Request**:
   - Push changes: `git push origin feature/my-feature`
   - Create PR on GitHub
   - Ensure CI checks pass

## References

- [System Architecture](../architecture/system-overview.md)
- [API Documentation](../api/api-reference.md)
- [Database Schema](../database/schema.md)
- [Deployment Guide](../operations/deployment-guide.md)
- [Code Standards](../code-standards/coding-guidelines.md)
