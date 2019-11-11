package com.searchengine.demo.Model;



public class Movie {
    private String title;
    private int year;
    private String plot;
    private String type;
    private String celebrities;

    public int getYear() {
        return year;
    }

    public String getPlot() {
        return plot;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getCelebrities() {
        return celebrities;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setCelebrities(String celebrities) {
        this.celebrities = celebrities;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

}

