package com.nency.note.room;

import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

public interface CategoryDao {
    @Insert
    void insertCategory(Category category);

    @Query("DELETE FROM category")
    void deleteAllCategories();

    @Query("DELETE FROM note WHERE id = :id")
    void deleteCategory(int id);

    @Query("UPDATE category SET name = :name, noOfNotes = :noOfNotes")
    void updateCategory(String name, String noOfNotes);

    @Query("SELECT * FROM category ORDER BY name")
    List<Note> getAllCategories();
}
