package com.nency.note.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Note.class, Category.class}, version = 1, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {

    private static final String DB_NAME = "room_employee_database";

    public abstract NoteDao NoteDoa();
    public abstract CategoryDao CategoryDao();

    private static volatile NoteRoomDatabase INSTANCE;

    public static NoteRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), NoteRoomDatabase.class,DB_NAME).allowMainThreadQueries().build();
        }
        return INSTANCE;
    }

}
