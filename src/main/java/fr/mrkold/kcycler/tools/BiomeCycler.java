package fr.mrkold.kcycler.tools;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.mrkold.kcycler.KCyclerPlugin;

@SuppressWarnings("deprecation")
public class BiomeCycler {

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
	public BiomeCycler(KCyclerPlugin plugin, Material material) {
		this.plugin = plugin;
		this.material = material;
		Integer bt = plugin.getConfig().getInt("biome-tool");
		if (bt != 0) {
			material = Material.getMaterial(bt);
		}
	}

	/**
	 * 
	 * @param Biome
	 * @return Return the next biome
	 */
	private Biome getNextBiome(Biome b) {
		int i = biomes.indexOf(b) + 1;
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
	private Biome getPreviousBiome(Biome b) {
		int i = biomes.indexOf(b) - 1;
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
		for (Biome b : biomes) {
			if (b.equals(b.toString())) {
				return b;
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
	private void setBiome(Block b, Biome bi) {
		if (bi != null) {
			World world = b.getWorld();
			world.setBiome(b.getX(), b.getZ(), bi);
			refreshChunk(b);
		}
	}

	/**
	 * When player left click on a block it set the biome to previous one
	 * 
	 * @param Player
	 * @param Block
	 */
	public void leftClickBlock(Player p, Block b) {
		plugin.setItemInHandName(ChatColor.GREEN, getPreviousBiome(b.getBiome()).name(), p);
		setBiome(b, getPreviousBiome(b.getBiome()));
	}

	/**
	 * When player right click on a block it set the biome to next one
	 * 
	 * @param Player
	 * @param Block
	 */
	public void rightClickBlock(Player p, Block b) {
		plugin.setItemInHandName(ChatColor.GREEN, getNextBiome(b.getBiome()).name(), p);
		setBiome(b, getNextBiome(b.getBiome()));
	}

	/**
	 * Copy the block's biome and store it in data file
	 * 
	 * @param Player
	 * @param Block
	 */
	public void copyBiome(Player p, Block b) {
		String player = p.getName();
		String bi = b.getBiome().name();
		plugin.getPluginConfig().set(player + ".biome", bi);
		saveData();
		plugin.setItemInHandName(ChatColor.GOLD, b.getBiome().name(), p);
	}

	/**
	 * Paste the biome stored in data file to targeted block
	 * 
	 * @param Player
	 * @param Block
	 */
	public void pasteBiome(Player p, Block b) {
		String player = p.getName();
		String storedBiome = plugin.getPluginConfig().getString(player + ".biome");
		if (storedBiome == null || storedBiome.isEmpty()) {
			p.sendMessage(ChatColor.RED + "Copy a biome first");
		} else {
			Biome biome = getBiome(storedBiome);
			setBiome(b, biome);
		}
	}

	/**
	 * Save data to file
	 */
	private void saveData() {
		try {
			plugin.getPluginConfig().save(plugin.getDataFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Refresh the chunk where stands the given block
	 * 
	 * @param Block
	 */
	private void refreshChunk(Block b) {
		World w = b.getWorld();
		Chunk c = b.getChunk();
		w.refreshChunk(c.getX(), c.getZ());
	}
}
