# Bookstore Rating Feature - Playwright Automation Tests

This directory contains Playwright end-to-end tests for the bookstore rating feature.

## Prerequisites

- Node.js (v20 or higher)
- npm (v10 or higher)
- Java 11 (for running the bookstore application)

## Setup

1. Install dependencies:
   ```bash
   npm install
   ```

2. Install Playwright browsers:
   ```bash
   npx playwright install chromium
   ```

## Running the Tests

### Prerequisites
Make sure the bookstore application is running on http://localhost:8080 before running the tests.

To start the application:
```bash
cd ..
mvn package
java -jar target/bookstore-v3-1.0.0-SNAPSHOT.jar
```

### Run Tests
Once the application is running, execute the tests:

```bash
npm test
```

### Run Tests in Headed Mode
To see the browser while tests are running:
```bash
npm run test:headed
```

### Run Tests in UI Mode
To use Playwright's interactive UI mode:
```bash
npm run test:ui
```

## Test Coverage

The test suite covers the following scenarios:

1. **Display all books with star ratings** - Verifies that all books are displayed with their star ratings
2. **Filter books by 4+ star rating** - Tests filtering functionality for books with 4 or more stars
3. **Filter books by 3.5+ star rating** - Tests filtering functionality for books with 3.5 or more stars
4. **Filter books by 3+ star rating** - Tests filtering functionality for books with 3 or more stars
5. **Reset filter to show all books** - Tests resetting the filter to display all books
6. **Display star icons correctly** - Verifies that star icons are rendered properly

## Screenshots

Screenshots are automatically captured during test execution and saved to:
```
images/screenshot/
```

The following screenshots are captured:
- `all-books-with-ratings.png` - All books displayed with ratings
- `filtered-4-star-rating.png` - Books filtered by 4+ stars
- `filtered-3.5-star-rating.png` - Books filtered by 3.5+ stars
- `filtered-3-star-rating.png` - Books filtered by 3+ stars
- `reset-filter-all-books.png` - All books after resetting filter
- `star-icons-display.png` - Star icons display verification

## Test Results

Test results and detailed reports are generated in the `playwright-report` directory after test execution.
