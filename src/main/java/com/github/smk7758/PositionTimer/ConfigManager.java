package com.github.smk7758.PositionTimer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import com.github.smk7758.PositionTimer.Position.PositionType;
import com.github.smk7758.PositionTimer.Util.SendLog;
import com.github.smk7758.PositionTimer.Util.Util;

public class ConfigManager {
	private Main main = null;
	private final String pos = "Positions", enable_s = "enable", player_time_s = "PlayerTime";

	public ConfigManager(Main main) {
		this.main = main;
	}

	public void setPosition(Position position) {
		if (position == null) throw new IllegalArgumentException("Position is null.");
		setConfigEnable(position.name, position.enable);
		setConfigLocation(position.name, position.getLocation(PositionType.Start));
		setConfigLocation(position.name, position.getLocation(PositionType.End));
	}

	public void setConfigEnable(String name, boolean enable) {
		main.getConfig().set(Util.getPath(pos, name, this.enable_s), enable);
	}

	public void setConfigLocation(PositionType type, String name, Location loc) {
		setConfigLocation(getPositionPath(name, type), loc);
	}

	public void setConfigLocation(String path, Location loc) {
		if (loc != null) {
			SendLog.debug("Path@setConfigLoc: " + path);
			main.getConfig().set(Util.getPath(path, "World"), loc.getWorld().getName());
			main.getConfig().set(Util.getPath(path, "X"), loc.getX());
			main.getConfig().set(Util.getPath(path, "Y"), loc.getY());
			main.getConfig().set(Util.getPath(path, "Z"), loc.getZ());
		} else {
			main.getConfig().set(path, null);
		}
	}

	public void setConfigPlayerTime(String name, Map<OfflinePlayer, Duration> player_time) {
		player_time.forEach((player, duration) -> setConfigPlayerTime(name, player, duration));
	}

	public void setConfigPlayerTime(String name, OfflinePlayer player, Duration duration) {
		main.getConfig().set(Util.getPath(pos, player_time_s, name, player.getUniqueId().toString()),
				duration.toMillis());
	}

	public void removePosition(String name) {
		main.getConfig().set(Util.getPath(pos, name), null);
	}

	public Set<String> getConfigPositionNames() {
		return main.getConfig().getConfigurationSection(pos).getKeys(false);
	}

	public Location getConfigLocation(String name, PositionType type) {
		return getConfigLocation(getPositionPath(name, type));
	}

	public Location getConfigLocation(String path) {
		SendLog.debug("config loc path: " + path);
		World world = null;
		int x = 0, y = 0, z = 0;
		// TODO
		// if (main.getConfig().contains(path + ".World") || main.getConfig().contains(path + ".X")
		// || main.getConfig().contains(path + ".Y") || main.getConfig().contains(path + ".Z")) {
		// SendLog.error("Cannot find location type.");
		// return null;
		// }
		x = main.getConfig().getInt(path + ".X");
		y = main.getConfig().getInt(path + ".Y");
		z = main.getConfig().getInt(path + ".Z");
		SendLog.debug("test");
		String world_name = main.getConfig().getString(path + ".World");
		SendLog.debug("World: " + world_name);
		if (world_name != null) world = main.getServer().getWorld(world_name);
		if (world == null) {
			SendLog.error("Cannot load world in Path: " + path + " in config.");
			return null;
		} else {
			SendLog.debug("Location has been created.");
			return new Location(world, x, y, z);
		}
	}

	public boolean getConfigEnable(String name) {
		return main.getConfig().getBoolean(Util.getPath(pos, name, "enable"), true);
	}

	public Map<OfflinePlayer, Duration> getConfigPlayerTime() {
		Map<OfflinePlayer, Duration> player_time = new HashMap<>();
		for (String uuid : main.getConfig().getConfigurationSection(Util.getPath(pos, player_time_s)).getKeys(false)) {
			player_time.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)),
					Duration.ofMillis(main.getConfig().getLong(Util.getPath(pos, player_time_s, uuid))));
		}
		return player_time;
	}

	private String getPositionPath(String name, PositionType type) {
		return Util.getPath(pos, name, type.toString());
	}
}