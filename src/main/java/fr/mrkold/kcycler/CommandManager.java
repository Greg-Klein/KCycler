package fr.mrkold.kcycler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.mrkold.kcycler.Utils.PermissionsUtils;

public class CommandManager implements CommandExecutor {

	/**
	 * Commands
	 */
	public final static String BIOMETOOL_COMMAND = "bt";
	public final static String METATOOL_COMMAND = "mt";
	public final static String PLAYERHEAD_COMMAND = "ph";

	private KCyclerPlugin plugin;

	public CommandManager(KCyclerPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission(PermissionsUtils.USE_PERMISSION)) {
				if (label.equalsIgnoreCase(METATOOL_COMMAND)) {
					plugin.giveMetaTool(player);
				}
				if (label.equalsIgnoreCase(BIOMETOOL_COMMAND)) {
					plugin.giveBiomeTool(player);
				}
				if (label.equalsIgnoreCase(PLAYERHEAD_COMMAND)) {
					if (args.length != 1) {
						player.sendMessage(ChatColor.RED + "Usage: /" + PLAYERHEAD_COMMAND + " <PlayerName>");
					} else {
						String a0 = args[0];
						plugin.givePlayerHead(player, a0);
					}
				}
			} else {
				player.sendMessage(ChatColor.AQUA + "You don't have permission to use this command. ("
						+ PermissionsUtils.USE_PERMISSION + ")");
			}
		} else {
			plugin.getLogger().warning("Only players can perform this command");
		}
		return false;
	}

}
