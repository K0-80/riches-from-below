package com.richesfrombelow.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    private static final List<ScheduledTask> tasks = new CopyOnWriteArrayList<>();

    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(server -> tick());
    }

    public static void schedule(int delayTicks, Runnable task) {
        tasks.add(new ScheduledTask(delayTicks, task));
    }

    private static void tick() {
        if (tasks.isEmpty()) {
            return;
        }

        List<ScheduledTask> tasksToRemove = new ArrayList<>();
        List<ScheduledTask> tasksToRun = new ArrayList<>();

        for (ScheduledTask task : tasks) {
            task.decrementDelay();
            if (task.getDelay() <= 0) {
                tasksToRun.add(task);
                tasksToRemove.add(task);
            }
        }

        // Remove completed tasks
        if (!tasksToRemove.isEmpty()) {
            tasks.removeAll(tasksToRemove);
        }

        // Execute the tasks that are due.
        // This is done *after* iteration and removal to prevent ConcurrentModificationException
        // if a task schedules another task.
        for (ScheduledTask task : tasksToRun) {
            task.getRunnable().run();
        }
    }

    private static class ScheduledTask {
        private int delay;
        private final Runnable runnable;

        public ScheduledTask(int delay, Runnable runnable) {
            this.delay = delay;
            this.runnable = runnable;
        }

        public int getDelay() {
            return delay;
        }

        public Runnable getRunnable() {
            return runnable;
        }

        public void decrementDelay() {
            this.delay--;
        }
    }
}