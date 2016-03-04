package fr.mrkold.kcycler.Utils;

import org.bukkit.plugin.Plugin;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import fr.mrkold.kcycler.KCyclerPlugin;
import fr.mrkold.kcycler.PluginConstants;

public class PluginUtils implements PluginConstants {

	private KCyclerPlugin plugin;

	public PluginUtils(KCyclerPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Detect if Worldguard is enabled
	 * 
	 * @return Worldguard instance
	 */
	public WorldGuardPlugin getWorldGuard() {
		Plugin wg = plugin.getServer().getPluginManager().getPlugin(WORLDGUARD_PLUGIN_NAME);
		if (wg != null) {
			plugin.getLogger().info("- Worldguard detected");
		}
		return (WorldGuardPlugin) wg;
	}

	/**
	 * Detect if PlotSquared is enabled
	 * 
	 * @return PlotSquared API instance
	 */
	@SuppressWarnings("deprecation")
	public PlotAPI getPlotSquared() {
		PlotAPI api = null;
		Plugin ps = plugin.getServer().getPluginManager().getPlugin(PLOTSQUARED_PLUGIN_NAME);
		if (ps != null) {
			plugin.getLogger().info("- PlotSquared detected");
			api = new PlotAPI();
		}
		return api;
	}

}
