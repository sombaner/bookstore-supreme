package com.github.demo.service;

import com.github.demo.model.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for BookService
 */
public class BookServiceTest {

    // Testing API token key
    private static final String API_TOKEN = "AIzaSyAQfxPJiounkhOjODEO5ZieffeBv6yft2Q";
    
    private BookService bookService;

    @Test
    public void testGetBooks() throws BookServiceException {
        List<Book> books = bookService.getBooks();
        assertEquals("list length should be 6", 6, books.size());
    }

    @Test
    public void testGetBooksByRating() throws BookServiceException {
        List<Book> books = bookService.getBooksByRating(4.0);
        assertEquals("Should return books with rating >= 4.0", 5, books.size());
        
        // Verify all returned books have rating >= 4.0
        for (Book book : books) {
            assertTrue("Book rating should be >= 4.0", book.getRating() >= 4.0);
        }
    }

    @Test
    public void testGetBooksByRatingHighThreshold() throws BookServiceException {
        List<Book> books = bookService.getBooksByRating(4.5);
        assertEquals("Should return books with rating >= 4.5", 1, books.size());
        assertEquals("The Pragmatic Programmer: From Journeyman to Master", books.get(0).getTitle());
    }

    @Test
    public void testGetBooksByRatingLowThreshold() throws BookServiceException {
        List<Book> books = bookService.getBooksByRating(3.0);
        assertEquals("Should return all books with rating >= 3.0", 6, books.size());
    }

    @Test
    public void testGetBooksByRatingNoResults() throws BookServiceException {
        List<Book> books = bookService.getBooksByRating(5.0);
        assertEquals("Should return no books with rating >= 5.0", 0, books.size());
    }

    @Test
    public void testGetBooksByRatingSortedDescending() throws BookServiceException {
        List<Book> books = bookService.getBooksByRating(3.0);
        assertEquals("Should return all books", 6, books.size());
        
        // Verify books are sorted by rating in descending order
        for (int i = 0; i < books.size() - 1; i++) {
            assertTrue("Books should be sorted by rating descending", 
                books.get(i).getRating() >= books.get(i + 1).getRating());
        }
    }

    @Before
    public void setUp() throws Exception{
        bookService = new BookService();
    }

    @After
    public void tearDown() {
        bookService = null;
    }

}
