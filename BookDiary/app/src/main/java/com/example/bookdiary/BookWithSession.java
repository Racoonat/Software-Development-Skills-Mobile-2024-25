package com.example.bookdiary;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class BookWithSession {
    @Embedded
    public Book book;

    @Relation(
            parentColumn = "id",
            entityColumn = "bookId"
    )
    public List<ReadingSession> sessions;

    public boolean isRead() {
        for (ReadingSession session : sessions) {
            // Si alguna sesión tiene el estado "Read", lo consideramos como leído
            if (session.status.equals("Read")) {
                return true;
            }
        }
        return false; // Si ninguna sesión tiene el estado "Read", entonces el libro no está leído
    }
}
