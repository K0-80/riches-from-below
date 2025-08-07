package com.richesfrombelow;

import com.richesfrombelow.block.ModBlocks;
import com.richesfrombelow.block.entity.ModBlockEntities;
import com.richesfrombelow.component.ModDataComponents;
import com.richesfrombelow.entities.ModEntities;
import com.richesfrombelow.items.ModItemGroups;
import com.richesfrombelow.items.ModItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RichesfromBelow implements ModInitializer {
	public static final String MOD_ID = "richesfrombelow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final List<ScheduledTask> serverTasks = new ArrayList<>();

	@Override
	public void onInitialize() {

		ModEntities.register();

		ModBlocks.register();
		ModBlockEntities.register();

		ModItems.register();
		ModItemGroups.register();

		ModDataComponents.register();


		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (Iterator<ScheduledTask> iterator = serverTasks.iterator(); iterator.hasNext(); ) {
				ScheduledTask task = iterator.next();
				task.tick();
				if (task.isFinished()) {
					task.run();
					iterator.remove();
				}
			}
		});
	}

	public static void scheduleServerTask(int delayTicks, Runnable action) {
		serverTasks.add(new ScheduledTask(delayTicks, action));
	}

	private static class ScheduledTask {
		private int delayTicks;
		private final Runnable action;

		ScheduledTask(int delayTicks, Runnable action) {
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