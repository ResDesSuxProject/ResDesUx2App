package com.example.pettivitywatch.communication;

import android.content.Context;
import android.util.Log;

import com.example.pettivitywatch.models.DebugBPMHandler;
import com.example.pettivitywatch.models.HeartRateQueue;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

public class Communication implements HeartRateQueue {
    private static final String TAG = "Communication";
    private String transcriptionNodeId = null;
    private final Context context;
    private final DebugBPMHandler debugBPMHandler;
    public static final String HEART_RATE_MESSAGE_PATH = "/heartRates";//heartRates";
    private final ReentrantLock lock;
    private final List<Integer> heartRateQueue;

    public Communication(Context context, DebugBPMHandler debugBPMHandler) {
        this.context = context;
        this.debugBPMHandler = debugBPMHandler;

        lock = new ReentrantLock();
        heartRateQueue = new ArrayList<>();

        new Thread(this::run).start();
    }

    /**
     * Adds a heart rate to the queue so it can be averaged and sent to the app
     * @param heartRate the heartRate in BPM
     */
    public void addToQueue(int heartRate) {
        synchronized (heartRateQueue) {
            heartRateQueue.add(heartRate);
        }
    }

    /**
     * Setup the nodes and then keeps listening to the queue and sends it to the app
     */
    private void run() {
        // Find the nearest phone
        Set<Node> nodes;
        try {
            nodes = getNodes(context);
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "run: Couldn't get nearby nodes", new RuntimeException(e));
            return;
        }
        lock.lock();
         transcriptionNodeId = pickBestNodeId(nodes);
        lock.unlock();
        sendData("RequestScore");
        // Constantly print the queue in steps of 15 seconds if it is not empty
        while (true) {
            synchronized (heartRateQueue) {
                if (debugBPMHandler.isEnabled()){
                    sendData("" + debugBPMHandler.getBPM());
                }
                else if (heartRateQueue.size() > 0) {
                    sendData("" + average(heartRateQueue));
                    heartRateQueue.clear();
                }
            }
            delay(10000);
        }
    }

    /**
     * Send data over the datalayer to the nearest phone.
     * Thread safe.
     * @param data data in string which should be send
     */
    public void sendData(String data) {
        lock.lock();
        if (transcriptionNodeId != null) {
            // Send data
            Task<Integer> sendTask = Wearable.getMessageClient(context)
                    .sendMessage(transcriptionNodeId, HEART_RATE_MESSAGE_PATH, data.getBytes());

            // Add callbacks when it succeeded or failed
            sendTask.addOnSuccessListener(integer -> Log.v(TAG, "added data to sending queue: " + data));
            sendTask.addOnFailureListener(error -> {
                Log.e(TAG, "requestTranscription: Couldn't send message: " + error.getMessage() + "\n");
                error.printStackTrace();
            });
        }
        lock.unlock();
    }

    /**
     * Retrieves all the nodes (phones) connected to the watch
     * @param context the context of the app
     * @return a set of Nodes
     * @throws ExecutionException await is used
     * @throws InterruptedException await is used
     */
    private Set<Node> getNodes(Context context) throws ExecutionException, InterruptedException {
        List<Node> nodes = Tasks.await(Wearable.getNodeClient(context).getConnectedNodes());
        return new HashSet<>(nodes);
    }

    /**
     * Picks the first nearby node using the Node.isNearby() function
     * @param nodes a set of nodes
     * @return the id of the nearest node
     */
    private String pickBestNodeId(Set<Node> nodes) {
        String bestNodeId = null;
        // Find a nearby node (phone) or pick one arbitrarily
        for (Node node : nodes) {
            if (node.isNearby()) {
                return node.getId();
            }
            bestNodeId = node.getId();
        }
        return bestNodeId;
    }

    private int average(List<Integer> numbers) {
        int total = 0;
        for (Integer number : numbers) {
            total += number;
        }
        return total / numbers.size();
    }

    private void delay(long duration) {
        try {
            synchronized (this) {
                wait(duration);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
