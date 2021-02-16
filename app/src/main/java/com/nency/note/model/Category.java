package com.nency.note.model;

public class Category {
    private int id;
    private int name;
    private int noOfNotes;

    public Category(int id, int name, int noOfNotes) {
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

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getNoOfNotes() {
        return noOfNotes;
    }

    public void setNoOfNotes(int noOfNotes) {
        this.noOfNotes = noOfNotes;
    }
}
