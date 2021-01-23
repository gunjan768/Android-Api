package com.example.http.Retrofit;

import com.google.gson.annotations.SerializedName;

public class Post
{
    private int userId;
    private Integer id;
    private String title;

    // If json key and variable name differ we will use SerializeName annotation and pass the key as a parameter to the SerializedName. Here when we fetch
    // data from the internet as a json then that object will contain lots of key-value pairs and among them on will be 'body' ( key ). You can avoid
    // whole this mess by using variable name as 'body' instead of 'text'.
    @SerializedName("body")
    private String text;

    public Post(int userId, Integer id, String title, String text) {

        this.userId = userId;
        this.id = id;
        this.title = title;
        this.text = text;
    }

    public int getUserID() {
        return userId;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }
}