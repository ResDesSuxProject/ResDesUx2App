package com.example.pettivitywatch.communication;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Communication {
    private static final String TAG = "Communication";
    private String transcriptionNodeId = null;
    private final Context context;
    public static final String HEART_RATE_MESSAGE_PATH = "/my_path";//heartRates";

    public Communication(Context context) {
        this.context = context;
        new Thread(this::run).start();
    }

    private void run() {
        Set<Node> nodes;
        try {
            nodes = getNodes(context);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "run: Couldn't get nearby nodes", new RuntimeException(e));
            return;
        }

        transcriptionNodeId = pickBestNodeId(nodes);

        while (true) {
            sendData("hoi".getBytes());
            try {
                synchronized (this) {
                    wait(10000);
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private void sendData(byte[] voiceData) {
        if (transcriptionNodeId != null) {
            Task<Integer> sendTask =
                    Wearable.getMessageClient(context).sendMessage(
                            transcriptionNodeId, HEART_RATE_MESSAGE_PATH, voiceData);
            // You can add success and/or failure listeners,
            // Or you can call Tasks.await() and catch ExecutionException
            sendTask.addOnSuccessListener(integer -> Log.d(TAG, "Send data: " + integer));
            sendTask.addOnFailureListener(error -> {
                Log.e(TAG, "requestTranscription: Couldn't send message: " + error.getMessage() + "\n");
                error.printStackTrace();
            });
        }  // Unable to retrieve node with transcription capability

    }


    private Set<Node> getNodes(Context context) throws ExecutionException, InterruptedException {
        Set<Node> nodesSet = new HashSet<>();
        List<Node> nodes =
                Tasks.await(Wearable.getNodeClient(context).getConnectedNodes());
        for (Node node : nodes) {
            nodesSet.add(node);
        }
        return nodesSet;
    }


    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }


}
