package com.github.smk7758.PositionTimer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Main extends JavaPlugin {
	public static final String plugin_name = "PositionTimer";
	public static boolean debug_mode = true;
	private CommandExecuter command_executer = new CommandExecuter(this);
	public Map<String, Location> start_positions = new HashMap<>(), end_positions = new HashMap<>();
	public Map<Player, LocalDateTime> in_timer_player = new HashMap<>();
	private BukkitTask loop = null;

	public enum Path {
		start_positions("StartPositions"), end_positions("EndPositions");

		String path;

		private Path(String path) {
			this.path = path;
		}
	}

	@Override
	public void onEnable() {
		if (!Main.plugin_name.equals(getDescription().getName())) getPluginLoader().disablePlugin(this);
		// getServer().getPluginManager().registerEvents(command_listner, this);
		getCommand(plugin_name).setExecutor(command_executer);
		saveDefaultConfig();
	}

	@Override
	public void onDisable() {
	}

	public CommandExecuter getCommandExecuter() {
		return command_executer;
	}

	public void loop() {
		loop = new BukkitRunnable() {
			@Override
			public void run() {
				@SuppressWarnings("unchecked")
				Stream<Player> players = (Stream<Player>) getServer().getOnlinePlayers().stream();
				start_positions.values().forEach(loc -> players
						.filter(player -> player.getLocation().equals(loc) && !in_timer_player.containsKey(player))
						.forEach(player -> onStartPosition(loc, player)));
				end_positions.values().forEach(loc -> players
						.filter(player -> player.getLocation().equals(loc) && in_timer_player.containsKey(player))
						.forEach(player -> onEndPosition(loc, player)));
			}
		}.runTask(this);
	}

	// TODO
	public void stopLoop() {
		loop.cancel();
	}

	public void startLoop() {
		loop();
	}

	// TODO: 以下
	public void onStartPosition(Location loc, Player player) {
		in_timer_player.put(player, LocalDateTime.now());
	}

	public void onEndPosition(Location loc, Player player) {
		LocalDateTime start_time = in_timer_player.get(player);
		SendLog.send("Time: " + Duration.between(LocalDateTime.now(), start_time), player);
		in_timer_player.remove(player);
	}

	public void setStartPosition(String name, Player player) {
		setStartPosition(name, player.getLocation().getBlock().getLocation());
	}

	public void setStartPosition(String name, Location loc) {
		start_positions.put(name, loc);
	}

	public void removeStartPosition(String name) {
		start_positions.remove(name);
		getConfig().set(Path.start_positions.toString() + "." + name, null);
	}

	public void setEndPosition(String name, Player player) {
		setEndPosition(name, player.getLocation().getBlock().getLocation());
	}

	public void setEndPosition(String name, Location loc) {
		end_positions.put(name, loc);
	}

	public void removeEndPosition(String name) {
		end_positions.remove(name);
		getConfig().set(Path.end_positions.toString() + "." + name, null);
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
	}

	public void savePositions() {
		savePosition(start_positions, Path.start_positions);
		savePosition(end_positions, Path.end_positions);
	}

	public void savePosition(Map<String, Location> positions, Path path) {
		positions.entrySet()
				.forEach(entry -> setConfigLocation(path, entry.getKey(), entry.getValue()));
	}

	public void setConfigLocation(Path path, String name, Location loc) {
		setConfigLocation(path.path + "." + name, loc);
	}

	public void setConfigLocation(String path, Location loc) {
		getConfig().set(path + ".World", loc.getWorld().getName());
		getConfig().set(path + ".X", loc.getX());
		getConfig().set(path + ".Y", loc.getY());
		getConfig().set(path + ".Z", loc.getZ());
	}

	public void loadPositions() {
		SendLog.debug("loadPositions");
		loadPosition(start_positions, Path.start_positions);
		loadPosition(end_positions, Path.end_positions);
	}

	public void loadPosition(Map<String, Location> positions, Path path) {
		for (String name : getConfig().getConfigurationSection(path.path).getKeys(false)) {
			if (name != null) positions.put(name, getConfigLocation(path, name));
		}
	}

	public Location getConfigLocation(Path path, String name) {
		return getConfigLocation(path.path + "." + name);
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
}
