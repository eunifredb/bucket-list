package com.example.bucketlist.models;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class Booking {
    @DocumentId
    private String id;
    private String title;
    private String venue;
    private String date;
    private String time;
    private Date dateTime;
    private String curator;

    public Booking() {
    }

    public Booking(String id, String title, String venue, String date, String time, Date dateTime, String curator) {
        this.id = id;
        this.title = title;
        this.venue = venue;
        this.date = date;
        this.time = time;
        this.dateTime = dateTime;
        this.curator = curator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getCurator() {
        return curator;
    }

    public void setCurator(String curator) {
        this.curator = curator;
    }
    //
}
