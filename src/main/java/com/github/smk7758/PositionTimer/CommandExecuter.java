package com.github.smk7758.PositionTimer;

import java.time.Duration;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.smk7758.PositionTimer.ConfigManager.PositionType;
import com.github.smk7758.PositionTimer.Util.SendLog;
import com.github.smk7758.PositionTimer.Util.Util;

public class CommandExecuter implements CommandExecutor {
	public Main main = null;

	public CommandExecuter(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("PositionTimer")) {
			if (args.length <= 0) {
				SendLog.error("Too short argument.", sender);
				return false;
			} else if (args[0].equalsIgnoreCase("set")) {
				if (!(sender instanceof Player)) {
					SendLog.error("Must use this command from Player.", sender);
					return false;
				}
				if (args.length <= 2) {
					SendLog.error("Too short argument.", sender);
					return false;
				}
				Player player = (Player) sender;
				if (args[1].equalsIgnoreCase("start")) {
					main.setPositionLocation(args[2], PositionType.Start, player);
					SendLog.send("Compleate adding start position: " + args[2] + ".", sender);
				} else if (args[1].equalsIgnoreCase("end")) {
					main.setPositionLocation(args[2], PositionType.End, player);
					SendLog.send("Compleate adding end position: " + args[2] + ".", sender);
				} else if (args[1].equalsIgnoreCase("enable")) {
					if (args.length <= 3) {
						main.setPositionEnable(args[2]);
						SendLog.send("Compleate swtiching enable of Position: " + args[2] + ".", sender);
					} else {
						if (args[2].equalsIgnoreCase("true")) {
							main.setPositionEnable(args[3], true);
						} else if (args[2].equalsIgnoreCase("false")) {
							main.setPositionEnable(args[3], false);
						} else {
							SendLog.error("Illegal argument."
									+ " Please wirite true of false for the third argument.", sender);
							return false;
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length <= 1) {
					SendLog.error("Too short argument.", sender);
					return false;
				} else {
					main.removePosition(args[1]);
					SendLog.send("Compleate removing position.", sender);
				}
			} else if (args[0].equalsIgnoreCase("show")) {
				if (args.length <= 1) {
					SendLog.error("Too short argument.", sender);
					return false;
				} else if (args.length <= 2) {
					if (args[1].equalsIgnoreCase("positions")) {
						showPositions(sender);
					} else if (args[1].equalsIgnoreCase("ranks")) {
						showRanks(sender);
					} else {
						return false;
					}
				} else if (args[1].equalsIgnoreCase("position")) {
					showPosition(args[2], sender);
				} else if (args[1].equalsIgnoreCase("rank")) {
					showRank(args[2], sender);
				} else {
					return false;
				}
			} else if (args[0].equalsIgnoreCase("save")) {
				main.saveConfig();
				SendLog.send("Config has been saved.", sender);
			} else if (args[0].equalsIgnoreCase("reload")) {
				main.reloadConfig();
				SendLog.send("Config has been reloaded.", sender);
			} else if (args[0].equalsIgnoreCase("debug")) {
				Main.debug_mode = !Main.debug_mode;
				SendLog.debug("DebugMode: " + Main.debug_mode, sender);
			} else if (args[0].equalsIgnoreCase("help")) {
				SendLog.debug("DEBUG", sender);
			} else if (args[0].equalsIgnoreCase("startloop")) {
				main.getPositionListner().startLoop();
				SendLog.send("Loop has been started.", sender);
			} else if (args[0].equalsIgnoreCase("stoploop")) {
				main.getPositionListner().stopLoop();
				SendLog.send("Loop has been stopped.", sender);
			} else {
				SendLog.error("Illegal argument.", sender);
			}
			return true;
		}
		return false;
	}

	public void showCommandList(CommandSender sender) {
		SendLog.send("PositionTimer", sender);
	}

	public void showPositions(CommandSender sender) {
		SendLog.send("-- Position --", sender);
		main.positions.forEach(position -> showPosition(position, sender));
	}

	public void showPosition(String name, CommandSender sender) {
		SendLog.send("Name: " + name, sender);
		showPosition(name, PositionType.Start, sender);
		showPosition(name, PositionType.End, sender);
	}

	public void showPosition(Position position, CommandSender sender) {
		SendLog.send("Name: " + position.name, sender);
		showPosition(position, PositionType.Start, sender);
		showPosition(position, PositionType.End, sender);
	}

	public void showPosition(String name, PositionType type, CommandSender sender) {
		Position position = main.getPosition(name);
		if (position != null) showPosition(position, type, sender);
		else SendLog.error("Illegal position name.", sender);
	}

	public void showPosition(Position position, PositionType type, CommandSender sender) {
		if (position == null) throw new IllegalArgumentException("Position is null.");
		Location loc = position.getLocation(type);
		SendLog.send("  " + type.toString() + ":", sender);
		SendLog.send("    Enable: " + position.enable, sender);
		if (loc != null) SendLog.send("    X: " + loc.getX() + " , Y: " + loc.getY() + " , Z: " + loc.getZ(), sender);
		else SendLog.send("  Location is none.", sender);
	}

	public void showRanks(CommandSender sender) {
		SendLog.send("-- Rank --", sender);
		main.positions.forEach(position -> showRank(position, sender));
	}

	public void showRank(String name, CommandSender sender) {
		SendLog.send("-- Rank --", sender);
		Position position = main.getPosition(name);
		if (position != null) showRank(position, sender);
		else SendLog.error("Illegal position name.", sender);
	}

	public void showRank(Position position, CommandSender sender) {
		if (position == null) throw new IllegalArgumentException("Position is null.");
		Iterator<Entry<OfflinePlayer, Duration>> player_times = position.getPlayerTimesSorted().iterator();
		Entry<OfflinePlayer, Duration> entry = null;
		SendLog.send("Position: " + position.name, sender);
		if (player_times.hasNext()) {
			for (int i = 1; i <= 5; i++) {
				StringBuilder sb = new StringBuilder();
				if (!player_times.hasNext()) break;
				entry = player_times.next();
				sb.append(' ');
				sb.append(i);
				sb.append(" : ");
				sb.append(entry.getKey().getName());
				sb.append(' ');
				sb.append(Util.getTimeText(entry.getValue()));
				SendLog.send(sb.toString(), sender);
			}
		} else {
			SendLog.send("Rank is none.", sender);
		}
	}
}