package com.nency.note.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note")
public class Note {


    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @ColumnInfo(name = "date")
    private String date;

    @NonNull
    @ColumnInfo(name = "modifiedDate")
    private String modifiedDate;

//    private List<String> images;
//    private List<String> records;

    @NonNull
    @ColumnInfo(name = "placeAddress")
    private String placeAddress;
//    private Category category;


    public Note(int id, @NonNull String title, @NonNull String description, @NonNull String date, @NonNull String modifiedDate, @NonNull String placeAddress) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.modifiedDate = modifiedDate;
        this.placeAddress = placeAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(@NonNull String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @NonNull
    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(@NonNull String placeAddress) {
        this.placeAddress = placeAddress;
    }
}
