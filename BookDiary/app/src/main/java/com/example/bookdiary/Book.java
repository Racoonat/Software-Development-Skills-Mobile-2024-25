package com.example.bookdiary;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class Book {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String title;

    public String author;
    public String isbn;
    public int pageCount;
    public String coverImageUrl;



    public Book(@NonNull String title, String author, String isbn, int pageCount, String coverImageUrl) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.coverImageUrl = coverImageUrl;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPages() {
        return pageCount;
    }

    public String getCoverImage() {
        return coverImageUrl;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPages(int pages) {
        this.pageCount = pages;
    }

    public void setCoverImage(String coverImage) {
        this.coverImageUrl = coverImage;
    }
}
