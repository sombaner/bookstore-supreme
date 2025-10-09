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
        assertEquals("should return books with rating >= 4.0", 5, books.size());
        
        // Verify all returned books have rating >= 4.0
        for (Book book : books) {
            assertTrue("book rating should be >= 4.0", book.getRating() >= 4.0);
        }
    }

    @Test
    public void testGetBooksByRatingHigherThreshold() throws BookServiceException {
        List<Book> books = bookService.getBooksByRating(4.3);
        assertEquals("should return books with rating >= 4.3", 2, books.size());
        
        // Verify all returned books have rating >= 4.3
        for (Book book : books) {
            assertTrue("book rating should be >= 4.3", book.getRating() >= 4.3);
        }
    }

    @Test
    public void testGetBooksByRatingAllBooks() throws BookServiceException {
        List<Book> books = bookService.getBooksByRating(3.0);
        assertEquals("should return all books with rating >= 3.0", 6, books.size());
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
