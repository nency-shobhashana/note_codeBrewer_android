package com.nency.note.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.List;

import static androidx.room.ForeignKey.RESTRICT;

@Entity(tableName = "note", foreignKeys = @ForeignKey(entity = Category.class, parentColumns = {
        "id"}, childColumns = {"categoryId"}, onDelete = RESTRICT))
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

//    @NonNull
//    @ColumnInfo(name = "modifiedDate")
//    private String modifiedDate;

    @ColumnInfo(name = "images")
    private List<String> images;

    @ColumnInfo(name = "records")
    private List<String> records;

    @NonNull
    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "lat")
    private double lat;

    @ColumnInfo(name = "lng")
    private double lng;

    @ColumnInfo(name = "categoryId")
    private int categoryId;

    public Note(@NonNull String title,
            @NonNull String description,
            @NonNull String date,
            @NonNull String address,
            double lat,
            double lng, @NonNull List<String> images,
            @NonNull List<String> records, int categoryId) {
        this.title = title;
        this.description = description;
        this.date = date;
//        this.modifiedDate = modifiedDate;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.images = images;
        this.records = records;
        this.categoryId = categoryId;
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

//    @NonNull
//    public String getModifiedDate() {
//        return modifiedDate;
//    }
//
//    public void setModifiedDate(@NonNull String modifiedDate) {
//        this.modifiedDate = modifiedDate;
//    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getRecords() {
        return records;
    }

    public void setRecords(List<String> records) {
        this.records = records;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
