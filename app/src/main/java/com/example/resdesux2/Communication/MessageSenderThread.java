package com.example.resdesux2.Communication;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MessageSenderThread implements Runnable {
    private final String path;
    private final Context context;
    private final MessageSenderWatch messageSender;
    private final String message;

    public MessageSenderThread(String message, String path, Context context, MessageSenderWatch messageSender) {
        this.message = message;
        this.path = path;
        this.context = context;
        this.messageSender = messageSender;
    }

    public void run() {
        // Retrieve the connected devices, known as nodes
        Task<List<Node>> wearableList = Wearable.getNodeClient(context.getApplicationContext()).getConnectedNodes();
        try {
            List<Node> nodes = Tasks.await(wearableList);
            for (Node node : nodes) {
                Task<Integer> sendMessageTask = Wearable.getMessageClient(context).sendMessage(node.getId(), path, message.getBytes());
                try {
                    // Block on a task and get the result synchronously
                    Integer result = Tasks.await(sendMessageTask);
                    messageSender.sendMessageToPhone(message);
                } catch (ExecutionException | InterruptedException exception) {
                    // TODO: Handle the exception
                    exception.printStackTrace();
                }
            }
        } catch (ExecutionException | InterruptedException exception) {
            exception.printStackTrace();
            // TODO: Handle the exception
        }
    }
}
