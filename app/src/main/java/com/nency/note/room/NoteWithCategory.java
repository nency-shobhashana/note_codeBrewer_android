package com.nency.note.room;

import androidx.room.Embedded;
import androidx.room.Relation;

public class NoteWithCategory {
    @Embedded
    public Note note;

    @Relation(
            parentColumn = "categoryId",
            entityColumn = "id"
    )
    public Category category;
}
