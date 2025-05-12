package com.example.bookdiary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReadingSessionDao {

    @Insert
    void insert(ReadingSession readingSession);

    @Update
    void update(ReadingSession readingSession);
    @Delete
    void delete(ReadingSession readingSession);

    @Query("SELECT * FROM ReadingSession WHERE id = :id")
    ReadingSession getSessionById(long id);

    @Query("SELECT * FROM readingsession")
    List<ReadingSession> getAllSessions();

    @Query("DELETE FROM readingsession")
    void deleteAllReadingSessions();

    @Query("SELECT * FROM readingsession WHERE bookId = :bookId ORDER BY startDate DESC")
    List<ReadingSession> getReadingSessionsForBook(long bookId);
}
