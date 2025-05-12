package com.example.bookdiary;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(
        foreignKeys = @ForeignKey(
                entity = Book.class,
                parentColumns = "id",
                childColumns = "bookId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("bookId")}  // Crear un índice en la columna bookId
)
public class ReadingSession {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long bookId;  // Clave foránea que hace referencia al libro

    public String status;

    public String startDate;
    public String endDate;
    public float rating;
    public String personalNotes;


    public ReadingSession(long bookId,String status, String startDate, String endDate, float rating, String personalNotes) {
        this.bookId = bookId;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rating = rating;
        this.personalNotes = personalNotes;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPersonalNotes() {
        return personalNotes;
    }

    public void setPersonalNotes(String personalNotes) {
        this.personalNotes = personalNotes;
    }


    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getId() {
        return id;
    }
}
