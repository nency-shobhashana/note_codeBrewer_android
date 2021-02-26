package com.nency.note.room;

import androidx.annotation.NonNull;

public class CategoryWithNoteCount extends Category{
   private int noOfNotes;

    public CategoryWithNoteCount(@NonNull String name) {
        super(name);
    }

    public int getNoOfNotes() {
        return noOfNotes;
    }

    public void setNoOfNotes(int noOfNotes) {
        this.noOfNotes = noOfNotes;
    }
}
