package com.example.pettivitywatch.models;

public interface ServerInteraction {
    void login(String username, String password, ChangeListener<Integer> listener);
    void setScoreListener(Change2Listener<Integer, Integer> listener);
    void setConnectionFailedListener(ChangeListener<Boolean> listener);

    String getServerIP();
}
