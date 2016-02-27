package fr.mrkold.kcycler.tools;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.mrkold.kcycler.MainClass;

public class MetaCycler {

	private MainClass plugin;
	private Material material;

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	@SuppressWarnings("deprecation")
	public MetaCycler(MainClass plugin, Material material) {
		this.plugin = plugin;
		this.material = material;
		Integer mt = plugin.getConfig().getInt("meta-tool");
		if (mt != 0) {
			material = Material.getMaterial(mt);
		}
	}

	@SuppressWarnings("deprecation")
	public void leftClickBlock(Player player, Block b, Byte md) {
		b.setData((byte) (md - 1));
		refreshChunk(b);
		Block block = player.getTargetBlock(null, 5);
		ItemMeta im = player.getItemInHand().getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "" + block.getTypeId() + ":" + block.getData());
		player.getItemInHand().setItemMeta(im);
	}

	@SuppressWarnings("deprecation")
	public void rightClickBlock(Player player, Block b, Byte md) {
		b.setData((byte) (md + 1));
		refreshChunk(b);
		Block block = player.getTargetBlock(null, 5);
		ItemMeta im = player.getItemInHand().getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "" + block.getTypeId() + ":" + block.getData());
		player.getItemInHand().setItemMeta(im);
	}

	@SuppressWarnings("deprecation")
	public void copyMeta(Player p, Block b) {
		String player = p.getName();
		byte md = b.getData();
		Integer mat = b.getTypeId();
		plugin.getPluginConfig().set(player + ".block", mat);
		plugin.getPluginConfig().set(player + ".meta", md);
		saveData();
		ItemStack wand = p.getItemInHand();
		ItemMeta im = wand.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + "" + mat.toString() + ":" + md);
		wand.setItemMeta(im);
	}

	@SuppressWarnings("deprecation")
	public void pasteMeta(Player p, Block b) {
		String player = p.getName();
		Integer matint = plugin.getPluginConfig().getInt(player + ".block");
		Integer mdint = plugin.getPluginConfig().getInt(player + ".meta");
		if (matint == 0) {
			p.sendMessage(ChatColor.RED + "Copiez un bloc d'abord");
		} else {
			byte md = mdint.byteValue();
			Material mat = Material.getMaterial(matint);
			b.setType(mat);
			b.setData(md);
			refreshChunk(b);
		}
	}

	private void saveData() {
		try {
			plugin.getPluginConfig().save(plugin.getDataFile());
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