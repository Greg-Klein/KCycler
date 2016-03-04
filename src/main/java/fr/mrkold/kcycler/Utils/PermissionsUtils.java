package fr.mrkold.kcycler.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import fr.mrkold.kcycler.KCyclerPlugin;
import fr.mrkold.kcycler.PluginConstants;

public class PermissionsUtils implements PluginConstants {

	KCyclerPlugin plugin;
	PluginUtils pluginUtils;
	WorldGuardPlugin worldguard;

	public PermissionsUtils(KCyclerPlugin plugin) {
		this.plugin = plugin;
		pluginUtils = plugin.getPluginUtils();
		worldguard = pluginUtils.getWorldGuard();
	}

	public boolean canBuildAt(Player player, Location location) {
		/**
		 * Return true if player has admin permission
		 */
		if (player.hasPermission(ADMIN_PERMISSION)) {
			return true;
		}

		/**
		 * Return false if player can't build in this worldguard region
		 */
		if (worldguard != null) {
			if (!worldguard.canBuild(player, location)) {
				return false;
			}
		}

		return true;

	}

}
