package it.ecubit.java.training.beans;

import java.io.Serializable;
import java.util.*;

public class Movie implements Serializable {

    private int id;
    private String title;
    private String overview;
    private int year;
    private String imdb;
    private int duration;
    private float rating;

    private long votes;
    private List<Genre> genres = new ArrayList<>();
    private List<Director> directors = new ArrayList<>();
    private List<Actor> actors = new ArrayList<>();

    public Movie() {
    }

    public Movie(int id, String title, String overview, int year, String imdb, int duration, float rating, long votes) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.year = year;
        this.imdb = imdb;
        this.duration = duration;
        this.rating = rating;
        this.votes = votes;

    }

    // getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public void setDirectors(List<Director> directors) {
        this.directors = directors;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    @Override
    public String toString() {
        return "Movie [title=" + title + ", year=" + year + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}