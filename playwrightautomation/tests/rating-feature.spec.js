const { test, expect } = require('@playwright/test');
const path = require('path');

test.describe('Bookstore Rating Feature Tests', () => {
  
  test('should display all books with star ratings', async ({ page }) => {
    // Navigate to the bookstore
    await page.goto('/');
    
    // Wait for the page to load
    await page.waitForSelector('table.table-hover');
    
    // Check that books are displayed
    const books = await page.locator('.media').count();
    expect(books).toBeGreaterThan(0);
    
    // Verify star ratings are visible
    const starRatings = await page.locator('.star-rating').count();
    expect(starRatings).toBe(books);
    
    // Take screenshot
    const screenshotPath = path.join(__dirname, '../images/screenshot/all-books-with-ratings.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`Screenshot saved to: ${screenshotPath}`);
  });

  test('should filter books by 4+ star rating', async ({ page }) => {
    // Navigate to the bookstore
    await page.goto('/');
    
    // Wait for the page to load
    await page.waitForSelector('table.table-hover');
    
    // Count initial books
    const initialBooksCount = await page.locator('.media').count();
    console.log(`Initial books count: ${initialBooksCount}`);
    
    // Select 4+ star filter
    await page.selectOption('#rating', '4.0');
    
    // Wait for page to reload
    await page.waitForLoadState('networkidle');
    await page.waitForSelector('table.table-hover');
    
    // Count filtered books
    const filteredBooksCount = await page.locator('.media').count();
    console.log(`Filtered books count (4+ stars): ${filteredBooksCount}`);
    
    // Verify that filtered count is less than or equal to initial count
    expect(filteredBooksCount).toBeLessThanOrEqual(initialBooksCount);
    
    // Verify all visible books have rating >= 4.0
    const ratings = await page.locator('.star-rating').allTextContents();
    for (const ratingText of ratings) {
      // Extract rating number from text like "(4.2)"
      const match = ratingText.match(/\((\d+\.\d+)\)/);
      if (match) {
        const rating = parseFloat(match[1]);
        expect(rating).toBeGreaterThanOrEqual(4.0);
      }
    }
    
    // Take screenshot
    const screenshotPath = path.join(__dirname, '../images/screenshot/filtered-4-star-rating.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`Screenshot saved to: ${screenshotPath}`);
  });

  test('should filter books by 3.5+ star rating', async ({ page }) => {
    // Navigate to the bookstore
    await page.goto('/');
    
    // Wait for the page to load
    await page.waitForSelector('table.table-hover');
    
    // Select 3.5+ star filter
    await page.selectOption('#rating', '3.5');
    
    // Wait for page to reload
    await page.waitForLoadState('networkidle');
    await page.waitForSelector('table.table-hover');
    
    // Count filtered books
    const filteredBooksCount = await page.locator('.media').count();
    console.log(`Filtered books count (3.5+ stars): ${filteredBooksCount}`);
    
    // Verify all visible books have rating >= 3.5
    const ratings = await page.locator('.star-rating').allTextContents();
    for (const ratingText of ratings) {
      const match = ratingText.match(/\((\d+\.\d+)\)/);
      if (match) {
        const rating = parseFloat(match[1]);
        expect(rating).toBeGreaterThanOrEqual(3.5);
      }
    }
    
    // Take screenshot
    const screenshotPath = path.join(__dirname, '../images/screenshot/filtered-3.5-star-rating.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`Screenshot saved to: ${screenshotPath}`);
  });

  test('should filter books by 3+ star rating', async ({ page }) => {
    // Navigate to the bookstore
    await page.goto('/');
    
    // Wait for the page to load
    await page.waitForSelector('table.table-hover');
    
    // Select 3+ star filter
    await page.selectOption('#rating', '3.0');
    
    // Wait for page to reload
    await page.waitForLoadState('networkidle');
    await page.waitForSelector('table.table-hover');
    
    // Count filtered books (should be all 6 books since all have rating >= 3.0)
    const filteredBooksCount = await page.locator('.media').count();
    console.log(`Filtered books count (3+ stars): ${filteredBooksCount}`);
    expect(filteredBooksCount).toBe(6);
    
    // Take screenshot
    const screenshotPath = path.join(__dirname, '../images/screenshot/filtered-3-star-rating.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`Screenshot saved to: ${screenshotPath}`);
  });

  test('should reset filter to show all books', async ({ page }) => {
    // Navigate to the bookstore with a filter applied
    await page.goto('/?rating=4.0');
    
    // Wait for the page to load
    await page.waitForSelector('table.table-hover');
    
    // Count filtered books
    const filteredBooksCount = await page.locator('.media').count();
    console.log(`Filtered books count: ${filteredBooksCount}`);
    
    // Reset filter to "All Books"
    await page.selectOption('#rating', '');
    
    // Wait for page to reload
    await page.waitForLoadState('networkidle');
    await page.waitForSelector('table.table-hover');
    
    // Count all books
    const allBooksCount = await page.locator('.media').count();
    console.log(`All books count: ${allBooksCount}`);
    expect(allBooksCount).toBe(6);
    
    // Take screenshot
    const screenshotPath = path.join(__dirname, '../images/screenshot/reset-filter-all-books.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`Screenshot saved to: ${screenshotPath}`);
  });

  test('should display star icons correctly', async ({ page }) => {
    // Navigate to the bookstore
    await page.goto('/');
    
    // Wait for the page to load
    await page.waitForSelector('table.table-hover');
    
    // Check that star rating elements exist and contain stars
    const firstRating = await page.locator('.star-rating').first();
    const ratingText = await firstRating.textContent();
    
    // Verify the rating text contains star symbols (★)
    expect(ratingText).toContain('★');
    
    // Verify rating text contains a numeric value in parentheses
    expect(ratingText).toMatch(/\(\d+\.\d+\)/);
    
    // Take screenshot
    const screenshotPath = path.join(__dirname, '../images/screenshot/star-icons-display.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log(`Screenshot saved to: ${screenshotPath}`);
  });
});
