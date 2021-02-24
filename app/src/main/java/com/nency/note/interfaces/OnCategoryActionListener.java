package com.nency.note.interfaces;

import com.nency.note.room.Category;

public interface OnCategoryActionListener {
    void onCategorySelected(Category category);
    void onCategoryEditSelected(Category category);
    void onCategoryRemoveSelected(Category category);
}
