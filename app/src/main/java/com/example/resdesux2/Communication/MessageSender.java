package com.example.resdesux2.Communication;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MessageSender {
    private static final String PATH = "/pettivity_userdata";
    private final Handler messageHandler;
    public MessageSender() {
        messageHandler = new Handler(this::handleMessage);
    }

    /**
     * Sends a message to the connected wearable device
     * @param message the message to be send
     * @param context the current context
     */
    public void sendMessage(String message, Context context) {
        new Thread(new MessageSenderThread(message, PATH, context, this)).start();
    }

    private boolean handleMessage(Message message) {
        Bundle stuff = message.getData();
        Log.e("Test", "handleMessage: " + stuff.getString("messageText"));
        return true;
    }

    // Use a Bundle to encapsulate our message
    public void sendMessageToPhone(String messageText) {
        Bundle bundle = new Bundle();
        bundle.putString("messageText", messageText);
        Message msg = messageHandler.obtainMessage();
        msg.setData(bundle);
        messageHandler.sendMessage(msg);
    }
}
