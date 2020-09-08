package com.example.http.Retrofit;

import com.google.gson.annotations.SerializedName;

public class Comment
{
    private int postId, id;
    private String name, email;

    @SerializedName("body")
    private String text;

    public int getPostId() {
        return postId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getText() {
        return text;
    }
}