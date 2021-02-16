package com.nency.note.repository;

import com.nency.note.model.Category;
import com.nency.note.model.Note;

import java.util.ArrayList;

public class Database {
    private static ArrayList<Note> notes = new ArrayList<>();
    private static ArrayList<Category> categories = new ArrayList<>();

    public static void initDatabase(){
        categories.add(new Category(1,"Personal", 4));
        categories.add(new Category(2,"Shopping", 4));

        notes.add(new Note("Sample Title 1","Sample description 1","2nd June, 2020","2nd June 2020",null, null, "Canada", categories.get(0)));
        notes.add(new Note("Sample Title 2","Sample description 2","12nd July, 2020","12nd July 2020",null, null, "Canada", categories.get(1)));
        notes.add(new Note("Sample Title 3","Sample description 3","25nd Aug, 2020","25nd Aug 2020",null, null, "Canada", categories.get(1)));
        notes.add(new Note("Sample Title 4","Sample description 4","20nd Jan, 2020","20nd Jan 2020",null, null, "Canada", categories.get(0)));
        notes.add(new Note("Sample Title 5","Sample description 5","2nd June, 2020","2nd June 2020",null, null, "Canada",categories.get(0)));
        notes.add(new Note("Sample Title 6","Sample description 6","12nd July, 2020","12nd July 2020",null, null, "Canada", categories.get(1)));
        notes.add(new Note("Sample Title 7","Sample description 7","25nd Aug, 2020","25nd Aug 2020",null, null, "Canada", categories.get(1)));
        notes.add(new Note("Sample Title 8","Sample description 8","20nd Jan, 2020","20nd Jan 2020",null, null, "Canada", categories.get(0)));
    }

    public static ArrayList<Note> getNotes() {
        return notes;
    }

    public static ArrayList<Category> getCategories() {
        return categories;
    }
}
