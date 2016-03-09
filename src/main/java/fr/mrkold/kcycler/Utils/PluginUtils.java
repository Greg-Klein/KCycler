package fr.mrkold.kcycler.Utils;

import java.io.IOException;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import fr.mrkold.kcycler.KCyclerPlugin;

public class PluginUtils {

	/**
	 * 3rd party plugins
	 */
	public final static String WORLDGUARD_PLUGIN_NAME = "WorldGuard";
	public final static String PLOTSQUARED_PLUGIN_NAME = "PlotSquared";

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

	/**
	 * Save data to file
	 */
	public static void saveData(KCyclerPlugin plugin) {
		try {
			plugin.getPluginConfig().save(plugin.getDataFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Refresh the given block
	 * 
	 * @param Block
	 */
	public static void refreshBlock(Block block) {
		block.getState().update(true);
	}

	/**
	 * Refresh the chunk where stands the given block
	 * 
	 * @param Block
	 */
	public static void refreshChunk(Block block) {
		World world = block.getWorld();
		Chunk chunk = block.getChunk();
		world.refreshChunk(chunk.getX(), chunk.getZ());
	}

}
