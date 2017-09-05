
package jdz.farmKing.utils;

import org.bukkit.scheduler.BukkitRunnable;

import jdz.farmKing.main.Main;

public class TimedTask {
	private boolean isRunning = false;
	private final BukkitRunnable runnable;
	private final int time;

	public TimedTask(int time, Task t){
		this.time = time;
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				t.execute();
			}
		};
	}
	
	public void run(){
		runnable.run();
	}
	
	public void start() {
		if (!isRunning) {
			runnable.runTaskTimer(Main.plugin, time, time);
			isRunning = true;
		}
	}

	public void stop() {
		if (!isRunning) {
			runnable.cancel();
			isRunning = false;
		}
	}
	
	public boolean isRunning(){
		return isRunning;
	}

	public interface Task{
		public void execute();
	}
}
