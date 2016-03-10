package fr.mrkold.kcycler.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import fr.mrkold.kcycler.KCyclerPlugin;

public class PermissionsUtils {

	/**
	 * Permissions
	 */
	public final static String USE_PERMISSION = "kcycler.use";
	public final static String ADMIN_PERMISSION = "kcycler.admin";

	KCyclerPlugin plugin;
	PluginUtils pluginUtils;
	WorldGuardPlugin worldguard;
	PlotAPI plotsquared;

	public PermissionsUtils(KCyclerPlugin plugin) {
		this.plugin = plugin;
		pluginUtils = plugin.getPluginUtils();
		worldguard = pluginUtils.getWorldGuard();
		plotsquared = pluginUtils.getPlotSquared();
	}

	public boolean canUseAt(Player player, Location location) {
		/**
		 * Return true if player has admin permission
		 */
		if (player.hasPermission(ADMIN_PERMISSION)) {
			return true;
		}

		/**
		 * Return false if player do not have use permission
		 */
		if (!player.hasPermission(USE_PERMISSION)) {
			return false;
		}

		/**
		 * Return false if player can't build in this worldguard region
		 */
		if (worldguard != null) {
			if (!worldguard.canBuild(player, location)) {
				return false;
			}
		}

		/**
		 * Return false if player can't build in this PlotSquared plot
		 */
		if (plotsquared != null) {
			/**
			 * Is this location in a plotworld
			 */
			if (plotsquared.isPlotWorld(location.getWorld())) {
				/**
				 * If location is on a plot
				 */
				Plot plot = plotsquared.getPlot(location);
				if (plot != null) {
					/**
					 * Return false if player isn't added to the plot
					 */
					if (!plot.isAdded(player.getUniqueId())) {
						return false;
					}
				} else {
					/**
					 * Return false if player is in plotworld but not in plot
					 */
					return false;
				}
			}
		}

		return true;

	}

}
