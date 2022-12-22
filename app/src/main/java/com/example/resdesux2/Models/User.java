package com.example.resdesux2.Models;

import androidx.annotation.NonNull;

/**
 * This class is a model about the user.
 * It contains all the users properties, ID, username, score and age.
 */
public class User {
    private int ID;
    private String userName;
    private double score;
    private int age;

    public User(int ID, String userName, double score, int age) {
        this.ID = ID;
        this.userName = userName;
        this.score = score;
        this.age = age;
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

    public int getAge() {
        return age;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
