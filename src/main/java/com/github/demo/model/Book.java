package com.github.demo.model;

/**
 * Model class for book.
 */
public class Book {

    private String title;

    private String author;

    private String cover;

    private double rating;

    public Book() {

    }

    public Book(String author, String title) {
        this.author = author;
        this.title = title;
    }

    public Book(String author, String title, String cover) {
        this.author = author;
        this.title = title;
        this.cover = cover;
        this.rating = 0.0;
    }

    public Book(String author, String title, String cover, double rating) {
        this.author = author;
        this.title = title;
        this.cover = cover;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDetails() {
        return author + " " + title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
