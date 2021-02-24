package com.nency.note.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insertCategory(Category category);

    @Query("DELETE FROM category")
    void deleteAllCategories();

    @Query("DELETE FROM category WHERE id = :id")
    void deleteCategory(int id);

    @Query("UPDATE category SET name = :name WHERE id = :id")
    void updateCategory(int id, String name);

    @Query("SELECT * FROM category ORDER BY name")
    List<Category> getAllCategories();

    @Query("SELECT * FROM category WHERE name == 'UnCategorised'")
    Category getUnCategorised();
}
