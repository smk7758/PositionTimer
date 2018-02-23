package com.github.smk7758.PositionTimer;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.smk7758.PositionTimer.Main.PositionType;

public class CommandExecuter implements CommandExecutor {
	public Main main = null;

	public CommandExecuter(Main main) {
		this.main = main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("PositionTimer")) {
			if (args.length <= 0) {
				return false;
			}
			if (args[0].equalsIgnoreCase("setstart")) {
				if (!(sender instanceof Player)) {
					return false;
				}
				if (args.length <= 1) {

					return false;
				}
				Player player = (Player) sender;
				main.setPosition(args[1], PositionType.Start, player);
				SendLog.send("Compleate adding start position.", sender);
			} else if (args[0].equalsIgnoreCase("removestart")) {
				if (args.length <= 1) {
					return false;
				}
				main.removeStartPosition(args[1]);
				SendLog.send("Compleate removing start position.", sender);
			} else if (args[0].equalsIgnoreCase("setend")) {
				if (!(sender instanceof Player)) {
					return false;
				}
				if (args.length <= 1) {
					return false;
				}
				Player player = (Player) sender;
				main.setPosition(args[1], PositionType.End, player);
				SendLog.send("Compleate adding end position.", sender);
			} else if (args[0].equalsIgnoreCase("removeend")) {
				if (args.length <= 1) {
					return false;
				}
				main.removeEndPosition(args[1]);
				SendLog.send("Compleate removing end position.", sender);
			} else if (args[0].equalsIgnoreCase("show")) {
				showStartPosition(sender);
				showEndPosition(sender);
			} else if (args[0].equalsIgnoreCase("save")) {
				main.saveConfig();
				SendLog.send("Config has been saved.", sender);
			} else if (args[0].equalsIgnoreCase("debug")) {
				Main.debug_mode = !Main.debug_mode;
				SendLog.debug("DebugMode: " + Main.debug_mode);
			} else if (args[0].equalsIgnoreCase("reload")) {
				main.reloadConfig();
				SendLog.send("Config has been reloaded.", sender);
			} else if (args[0].equalsIgnoreCase("help")) {
				SendLog.debug("DEBUG", sender);
			} else if (args[0].equalsIgnoreCase("startloop")) {
				main.startLoop();
			} else if (args[0].equalsIgnoreCase("stoploop")) {
				main.stopLoop();
			}
			return true;
		}
		return false;
	}

	public void showStartPosition(CommandSender sender) {
		SendLog.send("-- Start Position --", sender);
		main.start_positions.entrySet().stream().filter(entry -> entry.getValue() != null)
				.forEach(entry -> showLocation(entry.getKey(), entry.getValue(), sender));
	}

	public void showEndPosition(CommandSender sender) {
		SendLog.send("-- End Position --", sender);
		main.end_positions.entrySet().forEach(entry -> showLocation(entry.getKey(), entry.getValue(), sender));
	}

	public void showLocation(String name, Location loc, CommandSender sender) {
		SendLog.debug("Name: " + name);
		if (name == null || loc == null) return;
		SendLog.send("Name: " + name
				+ " , X: " + loc.getX()
				+ " , Y: " + loc.getY()
				+ " , Z: " + loc.getZ(), sender);
	}
}
