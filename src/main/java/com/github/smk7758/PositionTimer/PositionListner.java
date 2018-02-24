package com.github.smk7758.PositionTimer;

import java.time.Duration;
import java.time.LocalDateTime;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.github.smk7758.PositionTimer.Position.PositionType;
import com.github.smk7758.PositionTimer.Util.SendLog;
import com.github.smk7758.PositionTimer.Util.Util;

public class PositionListner {
	Main main = null;
	private BukkitTask loop = null;

	public PositionListner(Main main) {
		this.main = main;
	}

	public void startLoop() {
		if (loop != null && !loop.isCancelled()) stopLoop();
		loop();
	}

	public void stopLoop() {
		loop.cancel();
	}

	private void loop() {
		loop = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : main.getServer().getOnlinePlayers()) {
					for (Position position : main.positions) {
						if (!canUsePosition(position)) continue;
						// SendLog.debug(position.name);
						if (!isPlayerInTimer(player, position)) {
							if (isPlayerOnPosition(player, position, PositionType.Start)) {
								onStartPosition(position, player);
							}
						} else {
							if (isPlayerOnPosition(player, position, PositionType.End)) {
								onEndPosition(position, player);
							}
						}
					}
				}
			}
		}.runTaskTimer(main, 0, 1);
	}

	public void onStartPosition(Position position, Player player) {
		SendLog.debug("start position: " + position.name + " by Player: " + player.getName());
		position.players_started.put(player, LocalDateTime.now());
		player.sendTitle("", "Start", 5, 50, 5);
	}

	public void onEndPosition(Position position, Player player) {
		SendLog.debug("end position: " + position.name + " by Player: " + player.getName());
		LocalDateTime now_time = LocalDateTime.now(), start_time = position.players_started.get(player);
		Duration duration = Util.getTime(start_time, now_time);

		SendLog.debug("End: " + player.getName() + ", from: " + start_time + ", to: " + now_time);
		SendLog.send("Time: " + Util.getTimeText(duration), player);

		player.sendTitle("", "End", 5, 50, 5);
		position.players_started.remove(player);
		position.player_times.put(player, duration);
	}

	public boolean canUsePosition(Position position) {
		return position.enable
				&& position.getLocation(PositionType.Start) != null
				&& position.getLocation(PositionType.End) != null;
	}

	public boolean isPlayerInTimer(Player player, Position position) {
		return position.players_started.containsKey(player);
	}

	public boolean isPlayerOnPosition(Player player, Position position, PositionType type) {
		return player.getLocation().equals(position.getLocation(type));
	}
}
