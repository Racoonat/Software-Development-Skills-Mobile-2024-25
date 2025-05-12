package com.example.bookdiary;

import android.content.Context;
import androidx.room.Room;

public class DatabaseClient {

    private static DatabaseClient instance;
    private final AppDatabase appDatabase;

    private DatabaseClient(Context context) {
        // `fallbackToDestructiveMigration()` para eliminar la base de datos al cambiar el esquema
        appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "BookDiaryDB")
                .fallbackToDestructiveMigration()  // Esto elimina la base de datos si el esquema cambia
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
