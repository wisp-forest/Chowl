package com.chyzman.chowl.industries.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.List;

public class ServerTickHelper {
    private static final List<Runnable> SCHEDULE = new ArrayList<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (Runnable task : SCHEDULE) {
                task.run();
            }

            SCHEDULE.clear();
        });
    }

    public static void schedule(Runnable task) {
        SCHEDULE.add(task);
    }
}
