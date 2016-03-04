package fr.mrkold.kcycler.Utils;

import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import fr.mrkold.kcycler.KCyclerPlugin;

public class PluginUtils {

	private KCyclerPlugin plugin;

	public PluginUtils(KCyclerPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Detect if Worldguard is enabled
	 * 
	 * @return Worldguard instance
	 * @throws Exception
	 */
	public WorldGuardPlugin getWorldGuard() {
		Plugin wg = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		return (WorldGuardPlugin) wg;
	}

}
