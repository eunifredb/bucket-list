package com.example.bucketlist.models;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class Event {
    @DocumentId
    private String id;
    private Date dateTime;
    private String title;
    private String info;
    private String date;
    private String time;
    private String venue;
    private double rating;
    private String curator;
    private String imageResource;
    private long price;


    public Event() {
    }

    public Event(String id, Date dateTime, String title, String info, String date, String time, String venue, double rating, String curator, String imageResource, long price) {
        this.id = id;
        this.dateTime = dateTime;
        this.title = title;
        this.info = info;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.rating = rating;
        this.curator = curator;
        this.imageResource = imageResource;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getCurator() {
        return curator;
    }

    public void setCurator(String curator) {
        this.curator = curator;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
