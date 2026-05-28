package com.itatiers;

import com.itatiers.profile.PlayerProfile;
import com.itatiers.profile.Status;

import java.util.ArrayList;
import java.util.concurrent.*;

public class PlayerProfileQueue {
    private static final ConcurrentLinkedDeque<PlayerProfile> queue = new ConcurrentLinkedDeque<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    static {
        scheduler.scheduleAtFixedRate(PlayerProfileQueue::processQueue, 0, 8500, TimeUnit.MILLISECONDS);
    }

    public static void enqueue(PlayerProfile profile) {
        queue.add(profile);
    }

    private static void processQueue() {
        ArrayList<PlayerProfile> toProcess = new ArrayList<>();
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