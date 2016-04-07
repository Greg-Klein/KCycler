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
import fr.mrkold.kcycler.dao.ConfigDao;
import fr.mrkold.kcycler.utils.PluginUtils;

@SuppressWarnings("deprecation")
public class BiomeTool implements Tool {

	public static Material BIOMETOOL_MATERIAL = Material.BLAZE_ROD;

	private KCyclerPlugin plugin;
	private ConfigDao configDao;
	private List<Biome> biomes = Arrays.asList(Biome.values());
	private int maxBiome = biomes.size() - 1;

	/**
	 * Constructor
	 */
	public BiomeTool(KCyclerPlugin plugin) {
		this.plugin = plugin;
		this.configDao = plugin.getConfigDao();
		Integer biomeTool = plugin.getConfig().getInt("biome-tool");
		if (biomeTool != 0) {
			BIOMETOOL_MATERIAL = Material.getMaterial(biomeTool);
		}
	}

	/**
	 * Copy the block's biome and store it in data file
	 */
	@Override
	public void copy(Player player, Block block) {
		String playerName = player.getName();
		String biomeName = block.getBiome().name();
		configDao.getPluginConfig().set(playerName + ".biome", biomeName);
		configDao.saveData();
		plugin.getPluginUtils().setItemInHandName(ChatColor.GOLD, block.getBiome().name(), player);
	}

	/**
	 * Paste the biome stored in data file to targeted block
	 */
	@Override
	public void paste(Player player, Block block) {
		String playerName = player.getName();
		String storedBiome = configDao.getPluginConfig().getString(playerName + ".biome");
		if (storedBiome == null || storedBiome.isEmpty()) {
			player.sendMessage(ChatColor.RED + "Copy a biome first");
		} else {
			Biome biome = getBiome(storedBiome);
			setBiome(block, biome);
		}
	}

	/**
	 * When player left click on a block it set the biome to previous one
	 */
	@Override
	public void previous(Player player, Block block) {
		plugin.getPluginUtils().setItemInHandName(ChatColor.GREEN, getPreviousBiome(block.getBiome()).name(), player);
		setBiome(block, getPreviousBiome(block.getBiome()));
	}

	/**
	 * When player right click on a block it set the biome to next one
	 */
	@Override
	public void next(Player player, Block block) {
		plugin.getPluginUtils().setItemInHandName(ChatColor.GREEN, getNextBiome(block.getBiome()).name(), player);
		setBiome(block, getNextBiome(block.getBiome()));
	}

	// ---------- Utilities ----------

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

}
