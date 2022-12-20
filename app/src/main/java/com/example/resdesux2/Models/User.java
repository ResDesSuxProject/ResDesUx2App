package com.example.resdesux2.Models;

import androidx.annotation.NonNull;

public class User {
    final private int ID;
    final private String userName;
    final private double score;

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

    public int getID() {
        return ID;
    }

    public String getUserName() {
        return userName;
    }

    public double getScore() {
        return score;
    }
}
