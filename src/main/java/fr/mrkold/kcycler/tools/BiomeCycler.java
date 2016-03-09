package fr.mrkold.kcycler.tools;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.mrkold.kcycler.KCyclerPlugin;
import fr.mrkold.kcycler.Utils.PluginUtils;

@SuppressWarnings("deprecation")
public class BiomeCycler {

	public final static Material DEFAULT_BIOMECYCLER_MATERIAL = Material.BLAZE_ROD;

	private KCyclerPlugin plugin;
	private Material material;
	private List<Biome> biomes = Arrays.asList(Biome.values());
	private int maxBiome = biomes.size() - 1;

	/**
	 * Getters & Setters
	 */

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * END Getters & Setters
	 */

	/**
	 * Constructor
	 */
	public BiomeCycler(KCyclerPlugin plugin) {
		this.plugin = plugin;
		this.material = DEFAULT_BIOMECYCLER_MATERIAL;
		Integer biomeTool = plugin.getConfig().getInt("biome-tool");
		if (biomeTool != 0) {
			material = Material.getMaterial(biomeTool);
		}
	}

	/**
	 * 
	 * @param Biome
	 * @return Return the next biome
	 */
	private Biome getNextBiome(Biome biome) {
		int i = biomes.indexOf(biome) + 1;
		if (i >= maxBiome) {
			i = 0;
		}
		return biomes.get(i);
	}

	/**
	 * 
	 * @param Biome
	 * @return Return the previous biome
	 */
	private Biome getPreviousBiome(Biome biome) {
		int i = biomes.indexOf(biome) - 1;
		if (i <= 0) {
			i = maxBiome;
		}
		return biomes.get(i);
	}

	/**
	 * 
	 * @param BiomeName
	 * @return Return biome corresponding to given name
	 */
	private Biome getBiome(String biome) {
		for (Biome currentBiome : biomes) {
			if (biome.equals(currentBiome.toString())) {
				return currentBiome;
			}
		}
		return null;
	}

	/**
	 * Change a block biome to given one
	 * 
	 * @param Block
	 * @param Biome
	 */
	private void setBiome(Block block, Biome biome) {
		if (biome != null) {
			World world = block.getWorld();
			world.setBiome(block.getX(), block.getZ(), biome);
			PluginUtils.refreshChunk(block);
		}
	}

	/**
	 * When player left click on a block it set the biome to previous one
	 * 
	 * @param Player
	 * @param Block
	 */
	public void leftClickBlock(Player player, Block block) {
		plugin.setItemInHandName(ChatColor.GREEN, getPreviousBiome(block.getBiome()).name(), player);
		setBiome(block, getPreviousBiome(block.getBiome()));
	}

	/**
	 * When player right click on a block it set the biome to next one
	 * 
	 * @param Player
	 * @param Block
	 */
	public void rightClickBlock(Player player, Block block) {
		plugin.setItemInHandName(ChatColor.GREEN, getNextBiome(block.getBiome()).name(), player);
		setBiome(block, getNextBiome(block.getBiome()));
	}

	/**
	 * Copy the block's biome and store it in data file
	 * 
	 * @param Player
	 * @param Block
	 */
	public void copyBiome(Player player, Block block) {
		String playerName = player.getName();
		String biomeName = block.getBiome().name();
		plugin.getPluginConfig().set(playerName + ".biome", biomeName);
		PluginUtils.saveData(plugin);
		plugin.setItemInHandName(ChatColor.GOLD, block.getBiome().name(), player);
	}

	/**
	 * Paste the biome stored in data file to targeted block
	 * 
	 * @param Player
	 * @param Block
	 */
	public void pasteBiome(Player player, Block block) {
		String playerName = player.getName();
		String storedBiome = plugin.getPluginConfig().getString(playerName + ".biome");
		if (storedBiome == null || storedBiome.isEmpty()) {
			player.sendMessage(ChatColor.RED + "Copy a biome first");
		} else {
			Biome biome = getBiome(storedBiome);
			setBiome(block, biome);
		}
	}
}
