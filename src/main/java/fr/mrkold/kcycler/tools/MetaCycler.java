package fr.mrkold.kcycler.tools;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.mrkold.kcycler.KCyclerPlugin;

@SuppressWarnings("deprecation")
public class MetaCycler {

	private KCyclerPlugin plugin;
	private Material material;

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
	public MetaCycler(KCyclerPlugin plugin, Material material) {
		this.plugin = plugin;
		this.material = material;
		Integer mt = plugin.getConfig().getInt("meta-tool");
		if (mt != 0) {
			material = Material.getMaterial(mt);
		}
	}

	/**
	 * When player left click on a block it set the metadata to previous one
	 * 
	 * @param Player
	 * @param Block
	 */
	public void leftClickBlock(Player player, Block b) {
		b.setData((byte) (b.getData() - 1));
		refreshBlock(b);
		String name = b.getTypeId() + ":" + b.getData();
		plugin.setItemInHandName(ChatColor.GREEN, name, player);
	}

	/**
	 * When player right click on a block it set the metadata to next one
	 * 
	 * @param Player
	 * @param Block
	 */
	public void rightClickBlock(Player player, Block b) {
		b.setData((byte) (b.getData() + 1));
		refreshBlock(b);
		String name = b.getTypeId() + ":" + b.getData();
		plugin.setItemInHandName(ChatColor.GREEN, name, player);
	}

	/**
	 * Copy given block metadata and store it in data file
	 * 
	 * @param Player
	 * @param Block
	 */
	public void copyMeta(Player p, Block b) {
		String player = p.getName();
		byte md = b.getData();
		Integer mat = b.getTypeId();
		plugin.getPluginConfig().set(player + ".block", mat);
		plugin.getPluginConfig().set(player + ".meta", md);
		saveData();
		String name = mat.toString() + ":" + md;
		plugin.setItemInHandName(ChatColor.GOLD, name, p);
	}

	/**
	 * Paste the metadata stored in data file to targeted block
	 * 
	 * @param Player
	 * @param Block
	 */
	public void pasteMeta(Player p, Block b) {
		String player = p.getName();
		Integer matint = plugin.getPluginConfig().getInt(player + ".block");
		Integer mdint = plugin.getPluginConfig().getInt(player + ".meta");
		if (matint == 0) {
			p.sendMessage(ChatColor.RED + "Copy a block first");
		} else {
			byte md = mdint.byteValue();
			Material mat = Material.getMaterial(matint);
			b.setType(mat);
			b.setData(md);
			refreshBlock(b);
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
	 * Refresh teh given block
	 * 
	 * @param Block
	 */
	private void refreshBlock(Block b) {
		b.getState().update(true);
	}

}
