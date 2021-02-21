package com.nency.note.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insertNote(Note note);

    @Query("DELETE FROM note")
    void deleteAllNotes();

    @Query("DELETE FROM note WHERE id = :id")
    void deleteNote(int id);

    @Query("UPDATE note SET title = :title, description = :description, date = :date, modifiedDate = :modifiedDate, placeAddress = :placeAddress")
    void updateNote(String title, String description, String date, String modifiedDate, String placeAddress);

    @Query("SELECT * FROM note ORDER BY title")
    List<Note> getAllNotes();
}
