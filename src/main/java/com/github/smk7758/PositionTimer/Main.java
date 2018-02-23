package com.github.smk7758.PositionTimer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {
	public static final String plugin_name = "PositionTimer";
	public static boolean debug_mode = true;
	private final String pos = "Positions";
	private CommandExecuter command_executer = new CommandExecuter(this);
	public Map<String, Location> start_positions = new HashMap<>(), end_positions = new HashMap<>();
	public Map<Player, LocalDateTime> in_timer_player = new HashMap<>();
	private BukkitTask loop = null;

	public enum PositionType {
		Start, End;
	}

	@Override
	public void onEnable() {
		if (!Main.plugin_name.equals(getDescription().getName())) getPluginLoader().disablePlugin(this);
		getCommand(plugin_name).setExecutor(command_executer);
		saveDefaultConfig();
		startLoop();
	}

	@Override
	public void onDisable() {
	}

	public CommandExecuter getCommandExecuter() {
		return command_executer;
	}

	@Override
	public void saveConfig() {
		super.saveConfig();
		savePositions();
	}

	@Override
	public void saveDefaultConfig() {
		super.saveDefaultConfig();
		reloadConfig();
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		loadPositions();
		debug_mode = getConfig().getBoolean("DebugMode");
	}

	private void loop() {
		loop = new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : getServer().getOnlinePlayers()) {
					if (!in_timer_player.containsKey(player)) {
						for (Location loc : start_positions.values()) {
							if (loc != null && player.getLocation().getBlock().getLocation().equals(loc)) {
								onStartPosition(loc, player);
							}
						}
					} else {
						for (Location loc : end_positions.values()) {
							if (loc != null && player.getLocation().getBlock().getLocation().equals(loc)) {
								onEndPosition(loc, player);
							}
						}
					}
				}
				in_timer_player.forEach((player, time) -> SendLog
						.debug("In: " + player.getName() + ", from: " + time.toString()));
			}
		}.runTaskTimer(this, 0, 1);
	}

	public void stopLoop() {
		loop.cancel();
	}

	public void startLoop() {
		loop();
	}

	public void onStartPosition(Location loc, Player player) {
		SendLog.debug("onStartPosition");
		in_timer_player.put(player, LocalDateTime.now());
	}

	public void onEndPosition(Location loc, Player player) {
		LocalDateTime now_time = LocalDateTime.now();
		SendLog.debug("onEndPosition");
		LocalDateTime start_time = in_timer_player.get(player);
		SendLog.debug("End: " + player.getName() + ", from: " + start_time + ", to: " + now_time);
		SendLog.send("Time: " + getTime(start_time, now_time), player);
		in_timer_player.remove(player);
	}

	private String getTime(Temporal start, Temporal end) {
		Duration duration = Duration.between(start, end);
		StringBuilder sb = new StringBuilder();
		sb.append(duration.getSeconds());
		sb.append('.');
		sb.append(duration.getNano()).delete(sb.length() - 6, sb.length());
		sb.append('s');
		return sb.toString();
	}

	public void setPosition(String name, PositionType type, Player player) {
		Location loc = player.getLocation().getBlock().getLocation();
		switch (type) {
			case Start:
				setStartPosition(name, loc);
				break;
			case End:
				setEndPosition(name, loc);
				break;
			default:
				SendLog.error("Please set Position Type.");
		}
	}

	public void setStartPosition(String name, Location loc) {
		start_positions.put(name, loc);
		setConfigLocation(PositionType.Start, name, loc);
	}

	public void removeStartPosition(String name) {
		start_positions.remove(name);
		setConfigLocation(PositionType.Start, name, null);
	}

	public void setEndPosition(String name, Location loc) {
		end_positions.put(name, loc);
		setConfigLocation(PositionType.End, name, loc);
	}

	public void removeEndPosition(String name) {
		end_positions.remove(name);
		setConfigLocation(PositionType.End, name, null);
	}

	// save
	public void savePositions() {
		savePosition(start_positions, PositionType.Start);
		savePosition(end_positions, PositionType.End);
	}

	public void savePosition(Map<String, Location> positions, PositionType type) {
		positions.entrySet().forEach(entry -> setConfigLocation(type, entry.getKey(), entry.getValue()));
	}

	public void setConfigLocation(PositionType type, String name, Location loc) {
		setConfigLocation(getPositionPath(name, type), loc);
	}

	public void setConfigLocation(String path, Location loc) {
		if (loc != null) {
			getConfig().set(path + ".World", loc.getWorld().getName());
			getConfig().set(path + ".X", loc.getX());
			getConfig().set(path + ".Y", loc.getY());
			getConfig().set(path + ".Z", loc.getZ());
		} else {
			getConfig().set(path, null);
		}
	}

	// load
	public void loadPositions() {
		SendLog.debug("loadPositions");
		loadPosition(start_positions, PositionType.Start);
		loadPosition(end_positions, PositionType.End);
	}

	public void loadPosition(Map<String, Location> positions, PositionType type) {
		for (String name : getConfig().getConfigurationSection(pos).getKeys(false)) {
			if (name != null) positions.put(name, getConfigLocation(name, type));
		}
	}

	public Location getConfigLocation(String name, PositionType type) {
		return getConfigLocation(getPositionPath(name, type));
	}

	public Location getConfigLocation(String path) {
		SendLog.debug("config loc path: " + path);
		World world = null;
		int x = 0, y = 0, z = 0;
		// TODO
		// if (getConfig().contains(path + ".World") || getConfig().contains(path + ".X")
		// || getConfig().contains(path + ".Y") || getConfig().contains(path + ".Z")) {
		// SendLog.error("Cannot find location type.");
		// return null;
		// }
		x = getConfig().getInt(path + ".X");
		y = getConfig().getInt(path + ".Y");
		z = getConfig().getInt(path + ".Z");
		SendLog.debug("test");
		String world_name = getConfig().getString(path + ".World");
		SendLog.debug("World: " + world_name);
		if (world_name != null) world = getServer().getWorld(world_name);
		if (world == null) {
			SendLog.error("Cannot load world in Path: " + path + " in config.");
			return null;
		} else {
			SendLog.debug("Location has been created.");
			return new Location(world, x, y, z);
		}
	}

	private String getPositionPath(String name, PositionType type) {
		return getPath(pos, name, type.toString());
	}

	public String getPath(String... paths) {
		StringBuilder sb = new StringBuilder();
		sb.append(paths[0]);
		for (int i = 1; i < paths.length; i++) {
			sb.append('.');
			sb.append(paths[i]);
		}
		return sb.toString();
	}
}