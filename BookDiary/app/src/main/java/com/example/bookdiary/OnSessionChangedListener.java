package com.example.bookdiary;

public interface OnSessionChangedListener {
    void onSessionUpdated();
    void onSessionDeleted(long sessionId);
}
