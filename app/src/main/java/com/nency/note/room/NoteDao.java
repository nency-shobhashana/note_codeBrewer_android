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

    @Query("UPDATE note SET title = :title, description = :description, images = :images where id= :id")
    void updateNote(int id, String title, String description, String images);

    @Query("SELECT * FROM note ORDER BY title")
    List<Note> getAllNotes();

    @Query("SELECT * FROM note where id= :id")
    Note getNote(int id);
}
