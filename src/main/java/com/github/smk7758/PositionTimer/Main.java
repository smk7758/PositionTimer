package com.github.smk7758.PositionTimer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import com.github.smk7758.PositionTimer.Position.PositionType;
import com.github.smk7758.PositionTimer.Util.SendLog;

public class Main extends JavaPlugin {
	public static final String plugin_name = "PositionTimer";
	public static boolean debug_mode = true;
	private CommandExecuter command_executer = new CommandExecuter(this);
	private PositionListner position_listner = new PositionListner(this);
	private ConfigManager config_manager = new ConfigManager(this);
	public Set<Position> positions = new HashSet<>();
	public Map<Player, Boolean> player_except = new HashMap<>();
	// public Map<String, Location> start_positions = new HashMap<>(), end_positions = new HashMap<>();
	// public Set<String> positions_enable = null;
	// public Map<Player, LocalDateTime> in_timer_player = new HashMap<>();
	// public Map<Player, Duration> player_time = new HashMap<>();// player_time.entrySet().stream().sorted();
	private Scoreboard scoreboard = null;

	@Override
	public void onEnable() {
		if (!Main.plugin_name.equals(getDescription().getName())) getPluginLoader().disablePlugin(this);
		getCommand(plugin_name).setExecutor(command_executer);
		scoreboard = getServer().getScoreboardManager().getNewScoreboard();
		saveDefaultConfig();
		position_listner.startLoop();
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void saveConfig() {
		super.saveConfig();
		save();
	}

	@Override
	public void saveDefaultConfig() {
		super.saveDefaultConfig();
		reloadConfig();
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		load();
		debug_mode = getConfig().getBoolean("DebugMode");
	}

	public void setPositionEnable(String name) {
		Position position = getPositionWithCreate(name);
		position.enable = !position.enable;
	}

	public void setPositionEnable(String name, boolean enable) {
		getPositionWithCreate(name).setEnable(enable);
		// for (Position position : positions) {
		// if (position.equalName(name)) {
		// position.setEnable(enable);
		// return;
		// }
		// }
		// positions.add(new Position(name).setEnable(enable));
	}

	// public void setPositionEnable(String name, boolean enable) {
	// if (enable) {
	// if (!positions_enable.contains(name)) positions_enable.add(name);
	// } else {
	// positions_enable.remove(name);
	// }
	// }

	// TODO
	public void setPositionLocation(String name, PositionType type, Player player) {
		Location loc = player.getLocation().getBlock().getLocation();
		getPositionWithCreate(name).setLocation(loc, type);
		// for (Position position : positions) {
		// if (position.equalName(name)) {
		// position.setLocation(loc, type);
		// return;
		// }
		// }
		// positions.add(new Position(name).setLocation(loc, type));
	}

	public void removePositionLocation(String name, PositionType type) {
		getPositionWithCreate(name).setLocation(null, type);
		// for (Position position : positions) {
		// if (position.equalName(name)) {
		// position.setLocation(null, type);
		// return;
		// }
		// }
	}

	public Position getPositionWithCreate(String name) {
		Position position = getPosition(name);
		if (position != null) {
			return position;
		} else {
			position = new Position(name);
			positions.add(position);
			return position;
		}
	}

	public Position getPosition(String name) {
		for (Position position : positions) {
			if (position.equalName(name)) {
				return position;
			}
		}
		return null;
	}

	public void removePosition(String name) {
		getConfigManager().removePosition(name);
		Position position = getPosition(name);
		if (position != null) positions.remove(position);
	}

	// load
	public void load() {
		SendLog.debug("loadPositions");
		loadPositionLocations(PositionType.Start);
		loadPositionLocations(PositionType.End);
		loadEnablePositions();
	}

	public void loadPositionLocations(PositionType type) {
		for (String name : config_manager.getConfigPositionNames()) {
			getPositionWithCreate(name).setLocation(config_manager.getConfigLocation(name, type), type);
		}
	}

	public void loadEnablePositions() {
		for (String name : config_manager.getConfigPositionNames()) {
			getPositionWithCreate(name).setEnable(config_manager.getConfigEnable(name));
		}
	}

	// save
	public void save() {
		positions.forEach(position -> config_manager.setPosition(position));
	}

	// public void savePositions(PositionType type) {
	// positions.forEach(position -> config_manager
	// .setConfigLocation(type, position.name, position.getLocation(type)));
	// }
	//
	// public void saveEnablePositions() {
	// positions.forEach(position -> config_manager.setConfigEnable(position.name, position.enable));
	// }

	public CommandExecuter getCommandExecuter() {
		return command_executer;
	}

	public PositionListner getPositionListner() {
		return position_listner;
	}

	public ConfigManager getConfigManager() {
		return config_manager;
	}

	public Scoreboard getScoreBoard() {
		return scoreboard;
	}

	// player config 保存
	// remove B
	// Timeを表示するのは、Title, Sidebar, Chatか。
}