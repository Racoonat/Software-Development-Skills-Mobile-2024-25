package com.example.bookdiary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {

    @Insert
    long insert(Book book);

    @Update
    void update(Book book);

    @Delete
    void delete(Book book);

    @Query("SELECT * FROM books")
    List<Book> getAllBooks();

    @Query("SELECT * FROM books WHERE id = :id")
    Book getBookById(long id);

    @Query("DELETE FROM books")
    void deleteAllBooks();

    @Transaction
    @Query("SELECT * FROM books")
    List<BookWithSession> getBooksWithSessions();

}
