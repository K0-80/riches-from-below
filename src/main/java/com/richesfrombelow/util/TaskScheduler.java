package com.richesfrombelow.util;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskScheduler {
    private static final List<Task> PENDING_TASKS = new ArrayList<>();

    public static void schedule(int delayTicks, Runnable action) {
        PENDING_TASKS.add(new Task(delayTicks, action));
    }

    public static void tick(MinecraftServer server) {
        for (Iterator<Task> iterator = PENDING_TASKS.iterator(); iterator.hasNext(); ) {
            Task task = iterator.next();
            task.tick();
            if (task.isFinished()) {
                // Using server.execute makes sure the task runs on the main server thread
                server.execute(task::run);
                iterator.remove();
            }
        }
    }

    private static class Task {
        private int delayTicks;
        private final Runnable action;

        Task(int delayTicks, Runnable action) {
            this.delayTicks = delayTicks;
            this.action = action;
        }

        public void tick() {
            this.delayTicks--;
        }

        public boolean isFinished() {
            return this.delayTicks <= 0;
        }

        public void run() {
            this.action.run();
        }
    }
}