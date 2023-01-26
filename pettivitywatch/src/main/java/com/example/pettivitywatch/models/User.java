package com.example.pettivitywatch.models;

import androidx.annotation.NonNull;

/**
 * This class is a model about the user.
 * It contains all the users properties, ID, username, score and age.
 */
public class User {
    private int ID;
    private String userName;
    private int intensityScore;
    private int frequencyScore;
    private int age;

    public User(int ID, String userName, int intensityScore, int frequencyScore, int age) {
        this.ID = ID;
        this.userName = userName;
        this.intensityScore = intensityScore;
        this.frequencyScore = frequencyScore;
        this.age = age;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" + ID + ", " + userName + ", {" + intensityScore + ", " + frequencyScore + "}" + "}";
    }

    public int getID() {
        return ID;
    }

    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public Score getScore() {
        return new Score(intensityScore, frequencyScore);
    }

    public int getAge() {
        return age;
    }

    public String transformToString() {
        return ID + "," + userName + "," + intensityScore + "," + frequencyScore + "," + age;
    }
    public static User generateFromString(String userStr) {
        if (userStr == null) return null;
        String[] userData = userStr.split(",");
        if (userData.length != 5) return null;
        return new User(Integer.parseInt(userData[0]),
                userData[1],
                Integer.parseInt(userData[2]),
                Integer.parseInt(userData[3]),
                Integer.parseInt(userData[4]));
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }

    /**
     * Sets the intensity and frequency score of the user.
     * If you only want to set one of the two you can use `-1` for the score you don't want
     * to change
     * @param intensityScore the new intensity score of the user, -1 for no change
     * @param frequencyScore the new frequency score of the user, -1 for no change
     */
    public void setScore(int intensityScore, int frequencyScore) {
        if (intensityScore != -1)
            this.intensityScore = intensityScore;

        if (frequencyScore != -1)
            this.frequencyScore = frequencyScore;
    }
    public void setScore(Score score) {
        intensityScore = score.getIntensityScore();
        frequencyScore = score.getFrequencyScore();
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static class Score{
        private final int intensityScore;
        private final int frequencyScore;

        public Score(int intensityScore, int frequencyScore) {
            this.intensityScore = intensityScore;
            this.frequencyScore = frequencyScore;
        }

        public int getIntensityScore() {
            return intensityScore;
        }

        public int getFrequencyScore() {
            return frequencyScore;
        }
    }
}
