package com.nency.note.model;

import java.util.List;

public class Note {
    private String title;
    private String description;
    private String date;
    private String modifiedDate;
    private List<String> images;
    private List<String> records;
    private String placeAddress;
    private Category category;

    public Note(String title, String description, String date, String modifiedDate, List<String> images, List<String> records, String placeAddress, Category category) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.modifiedDate = modifiedDate;
        this.images = images;
        this.records = records;
        this.placeAddress = placeAddress;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
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

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
