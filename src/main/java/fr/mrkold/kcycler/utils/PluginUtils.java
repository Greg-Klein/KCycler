package fr.mrkold.kcycler.utils;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import fr.mrkold.kcycler.KCyclerPlugin;
import fr.mrkold.kcycler.tools.BiomeTool;
import fr.mrkold.kcycler.tools.MetaTool;

@SuppressWarnings("deprecation")
public class PluginUtils {

	/**
	 * 3rd party plugins
	 */
	public final static String WORLDGUARD_PLUGIN_NAME = "WorldGuard";
	public final static String PLOTSQUARED_PLUGIN_NAME = "PlotSquared";

	private KCyclerPlugin plugin;

	/**
	 * Constructor
	 */
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

	/**
	 * Gives the biome tool to the player
	 * 
	 * @param player
	 */
	public void giveBiomeTool(Player player) {
		player.setItemInHand(new ItemStack(BiomeTool.BIOMETOOL_MATERIAL, 1));
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
	}

	/**
	 * Gives the meta tool to the player
	 * 
	 * @param player
	 */
	public void giveMetaTool(Player player) {
		player.setItemInHand(new ItemStack(MetaTool.METATOOL_MATERIAL, 1));
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
	}

	/**
	 * Give a player head to the player
	 * 
	 * @param player
	 * @param headOwner
	 */
	public void givePlayerHead(Player player, String headOwner) {
		ItemStack skull = new ItemStack(397, 1, (short) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(headOwner);
		skull.setItemMeta(meta);
		player.setItemInHand(skull);
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
	}

	/**
	 * Change the name of the item in the hand of the player
	 * 
	 * @param ChatColor
	 * @param String
	 * @param Player
	 */
	public void setItemInHandName(ChatColor color, String name, Player player) {
		ItemStack wand = player.getItemInHand();
		ItemMeta im = wand.getItemMeta();
		im.setDisplayName(color + name);
		wand.setItemMeta(im);
	}

}
