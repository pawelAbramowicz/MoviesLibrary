package com.example.pawel.moviesapp.Utilities.Models;

/**
 * Created by Paweł on 2017-07-05.
 */

public class UniqueModel {

    private int id;
    private String title;
    private String posterPath;

    public UniqueModel(int id, String title, String posterPath) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
    }

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

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
