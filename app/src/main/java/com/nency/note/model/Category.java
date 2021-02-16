package com.nency.note.model;

public class Category {
    private int id;
    private String name;
    private int noOfNotes;

    public Category(int id, String name, int noOfNotes) {
        this.id = id;
        this.name = name;
        this.noOfNotes = noOfNotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNoOfNotes() {
        return noOfNotes;
    }

    public void setNoOfNotes(int noOfNotes) {
        this.noOfNotes = noOfNotes;
    }
}
