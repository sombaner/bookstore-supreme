package com.github.demo.service;

import com.github.demo.model.Book;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

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
        assertEquals("should have 5 books with rating >= 4.0", 5, books.size());
        for (Book book : books) {
            if (book.getRating() < 4.0) {
                throw new AssertionError("Book " + book.getTitle() + " has rating " + book.getRating() + " which is less than 4.0");
            }
        }
    }

    @Test
    public void testGetBooksByRatingMinimum() throws BookServiceException {
        List<Book> books = bookService.getBooksByRating(3.0);
        assertEquals("should have 6 books with rating >= 3.0", 6, books.size());
    }

    @Test
    public void testGetBooksByRatingHigh() throws BookServiceException {
        List<Book> books = bookService.getBooksByRating(4.5);
        assertEquals("should have 1 book with rating >= 4.5", 1, books.size());
        assertEquals("The Pragmatic Programmer: From Journeyman to Master", books.get(0).getTitle());
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
