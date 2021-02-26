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

    @Query("UPDATE note SET title = :title, description = :description, images = :images, records = :records, categoryId = :categoryId where id == :id")
    void updateNote(int id,
            String title,
            String description,
            String images,
            String records,
            int categoryId);

    @Query("SELECT * FROM note ORDER BY title")
    List<Note> getAllNotes();

    @Query("SELECT * FROM note WHERE categoryId NOT in (:categoriesId) ORDER BY " +
            "CASE :sortByDate WHEN 'false' THEN title " +
            " WHEN 'true' THEN date END")
    List<NoteWithCategory> getAllFilterNotes(List<Integer> categoriesId, String sortByDate);

    @Query("SELECT * FROM note where id == :id")
    NoteWithCategory getNote(int id);
}
