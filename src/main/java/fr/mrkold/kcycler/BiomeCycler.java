package fr.mrkold.kcycler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BiomeCycler {

	private MainClass plugin;

	public BiomeCycler(MainClass plugin) {
		this.plugin = plugin;
	}

	private List<Biome> biomes = Arrays.asList(Biome.values());
	private int maxBiome = biomes.size() - 1;

	public Biome getNextBiome(Biome b) {
		int i = biomes.indexOf(b) + 1;
		if (i >= maxBiome) {
			i = 0;
		}
		return biomes.get(i);
	}

	public Biome getPreviousBiome(Biome b) {
		int i = biomes.indexOf(b) - 1;
		if (i <= 0) {
			i = maxBiome;
		}
		return biomes.get(i);
	}

	public Biome getBiome(String bi) {
		for (Biome biome : biomes) {
			if (bi.equals(biome.toString())) {
				return biome;
			}
		}
		return null;
	}

	public void setBiome(Block b, Biome bi) {
		if (bi != null) {
			World world = b.getWorld();
			world.setBiome(b.getX(), b.getZ(), bi);
			refreshChunk(b);
		}
	}

	public void leftClickBlock(Player p, Block b) {
		ItemStack wand = p.getItemInHand();
		ItemMeta im = wand.getItemMeta();
		im.setDisplayName(ChatColor.GREEN.toString() + getPreviousBiome(b.getBiome()).name());
		wand.setItemMeta(im);
		setBiome(b, getPreviousBiome(b.getBiome()));
	}

	public void rightClickBlock(Player p, Block b) {
		ItemStack wand = p.getItemInHand();
		ItemMeta im = wand.getItemMeta();
		im.setDisplayName(ChatColor.GREEN.toString() + getNextBiome(b.getBiome()).name());
		wand.setItemMeta(im);
		setBiome(b, getNextBiome(b.getBiome()));
	}

	public void copyBiome(Player p, Block b) {
		String player = p.getName();
		String bi = b.getBiome().toString();
		plugin.data.set(player + ".biome", bi);
		saveData();
		ItemStack wand = p.getItemInHand();
		ItemMeta im = wand.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + b.getBiome().toString());
		wand.setItemMeta(im);
	}

	public void pasteBiome(Player p, Block b) {
		String player = p.getName();
		String bi = plugin.data.getString(player + ".biome");
		if (bi == null || bi.isEmpty()) {
			p.sendMessage(ChatColor.RED + "Copiez un biome d'abord");
		} else {
			Biome bio = getBiome(bi);
			setBiome(b, bio);
		}
	}

	private void saveData() {
		try {
			plugin.data.save(plugin.dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void refreshChunk(Block b) {
		World w = b.getWorld();
		Chunk c = b.getChunk();
		w.refreshChunk(c.getX(), c.getZ());
	}
}
