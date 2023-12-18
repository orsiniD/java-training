package it.ecubit.java.training.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Actor implements Serializable {
    private int id;
    private String name;
    private String imdb;

    public Actor() {
    }

    public Actor(int id, String name, String imdb) {
        this.id = id;
        this.name = name;
        this.imdb = imdb;
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

    public String getImdb() {
        return imdb;
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    @Override
    public String toString() {
        return "Actor [id=" + id + ", name=" + name + ", imdb=" + imdb + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Actor actor = (Actor) o;
        return id == actor.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
