package com.example.resdesux2.Models;

import androidx.annotation.NonNull;

public class User {
    final public int ID;
    final public String userName;
    final public double score;

    public User(int ID, String userName, double score) {
        this.ID = ID;
        this.userName = userName;
        this.score = score;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" + ID + ", " + userName + ", " + score + "}";
    }
}
