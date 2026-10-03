package com.itatiers;

import com.itatiers.profile.PlayerProfile;
import com.itatiers.profile.Status;

import java.util.ArrayList;
import java.util.concurrent.*;

public class PlayerProfileQueue {
    private static final ConcurrentLinkedDeque<PlayerProfile> queue = new ConcurrentLinkedDeque<>();
    private static final int MAX_QUEUE_SIZE = 90;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "itatiers-profile-queue");
        thread.setDaemon(true);
        return thread;
    });

    static {
        scheduler.scheduleAtFixedRate(PlayerProfileQueue::processQueue, 0, 7000, TimeUnit.MILLISECONDS);
    }

    public static void enqueue(PlayerProfile profile) {
        queue.add(profile);
    }

    private static void processQueue() {
        ArrayList<PlayerProfile> toProcess = new ArrayList<>();
        ConcurrentLinkedDeque<PlayerProfile> playerQueue = new ConcurrentLinkedDeque<>(queue);
        ConcurrentLinkedDeque<PlayerProfile> exceedingQueue = new ConcurrentLinkedDeque<>();
        if (queue.size() > MAX_QUEUE_SIZE) {
            int counter = 0;
            for (PlayerProfile playerProfile : playerQueue) {
                counter++;
                if (counter > MAX_QUEUE_SIZE) {
                    exceedingQueue.add(playerProfile);
                    queue.remove(playerProfile);
                }
            }
        }
        for (PlayerProfile playerProfile : queue) {
            if (playerProfile != null && playerProfile.status == Status.SEARCHING) {
                if (!playerProfile.name.matches("^[a-zA-Z0-9_]{3,16}$") || playerProfile.name.contains(".")) {
                    playerProfile.status = Status.NOT_EXISTING;
                    continue;
                }

                toProcess.add(playerProfile);
            }
        }
        queue.clear();
        queue.addAll(exceedingQueue);
        PlayerProfile.buildItaTiersRequests(toProcess);
    }

    public static void putFirstInQueue(PlayerProfile profile) {
        queue.remove(profile);
        queue.addFirst(profile);
        processQueue();
    }

    public static void changeToFirstInQueue(PlayerProfile profile) {
        if (queue.contains(profile))
            putFirstInQueue(profile);
    }

    public static void clearQueue() {
        queue.clear();
    }
}