package fr.mrkold.kcycler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor, PluginConstants {

	private KCyclerPlugin plugin;

	public CommandManager(KCyclerPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (p.hasPermission(USE_PERMISSION)) {
				if (label.equalsIgnoreCase(METATOOL_COMMAND)) {
					plugin.giveMetaTool(p);
				}
				if (label.equalsIgnoreCase(BIOMETOOL_COMMAND)) {
					plugin.giveBiomeTool(p);
				}
				if (label.equalsIgnoreCase(PLAYERHEAD_COMMAND)) {
					if (args.length != 1) {
						p.sendMessage(ChatColor.RED + "Usage: /" + PLAYERHEAD_COMMAND + " <PlayerName>");
					} else {
						String a0 = args[0];
						plugin.givePlayerHead(p, a0);
					}
				}
			} else {
				p.sendMessage(
						ChatColor.AQUA + "You don't have permission to use this command. (" + USE_PERMISSION + ")");
			}
		}
		return false;
	}

}
