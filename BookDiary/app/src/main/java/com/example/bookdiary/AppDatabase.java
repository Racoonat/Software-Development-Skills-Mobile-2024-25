package com.example.bookdiary;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Book.class,ReadingSession.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract BookDao bookDao();
    public abstract ReadingSessionDao readingSessionDao();

}
