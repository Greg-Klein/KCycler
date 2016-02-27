package fr.mrkold.kcycler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KCCommands implements CommandExecutor, PluginConstants {

	private MainClass plugin;

	public KCCommands(MainClass plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (label.equalsIgnoreCase(METATOOL_COMMAND)) {
				plugin.giveMetaTool(p);
			}
			if (label.equalsIgnoreCase(BIOMETOOL_COMMAND)) {
				plugin.giveBiomeTool(p);
			}
			if (label.equalsIgnoreCase(PLAYERHEAD_COMMAND)) {
				if (args.length == 0) {
					p.sendMessage(ChatColor.RED + "Utilisation: /" + PLAYERHEAD_COMMAND + " <nomdujoueur>");
					return true;
				} else {
					String a0 = args[0];
					plugin.givePlayerHead(p, a0);
				}
			}
		}
		return false;
	}

}
