package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String name;
    private final String year;
    private final String id;



    public Movie(String name, String year, String id) {
        this.name = name;
        this.year = year;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }
    public String getID() {
        return year;
    }


}