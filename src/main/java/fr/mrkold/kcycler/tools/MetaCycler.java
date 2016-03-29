package fr.mrkold.kcycler.tools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.mrkold.kcycler.KCyclerPlugin;
import fr.mrkold.kcycler.dao.ConfigDao;
import fr.mrkold.kcycler.utils.PluginUtils;

@SuppressWarnings("deprecation")
public class MetaCycler {

	public final static Material DEFAULT_METACYCLER_MATERIAL = Material.STICK;

	private KCyclerPlugin plugin;
	private ConfigDao configDao;
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
	public MetaCycler(KCyclerPlugin plugin) {
		this.plugin = plugin;
		this.material = DEFAULT_METACYCLER_MATERIAL;
		configDao = plugin.getConfigDao();
		Integer metaTool = plugin.getConfig().getInt("meta-tool");
		if (metaTool != 0) {
			material = Material.getMaterial(metaTool);
		}
	}

	/**
	 * When player left click on a block it set the metadata to previous one
	 * 
	 * @param Player
	 * @param Block
	 */
	public void leftClickBlock(Player player, Block block) {
		block.setData((byte) (block.getData() - 1));
		PluginUtils.refreshBlock(block);
		String name = block.getTypeId() + ":" + block.getData();
		plugin.getPluginUtils().setItemInHandName(ChatColor.GREEN, name, player);
	}

	/**
	 * When player right click on a block it set the metadata to next one
	 * 
	 * @param Player
	 * @param Block
	 */
	public void rightClickBlock(Player player, Block block) {
		block.setData((byte) (block.getData() + 1));
		PluginUtils.refreshBlock(block);
		String name = block.getTypeId() + ":" + block.getData();
		plugin.getPluginUtils().setItemInHandName(ChatColor.GREEN, name, player);
	}

	/**
	 * Copy given block metadata and store it in data file
	 * 
	 * @param Player
	 * @param Block
	 */
	public void copyMeta(Player player, Block block) {
		String playerName = player.getName();
		byte metadata = block.getData();
		Integer materialId = block.getTypeId();
		configDao.getPluginConfig().set(playerName + ".block", materialId);
		configDao.getPluginConfig().set(playerName + ".meta", metadata);
		configDao.saveData();
		String name = materialId.toString() + ":" + metadata;
		plugin.getPluginUtils().setItemInHandName(ChatColor.GOLD, name, player);
	}

	/**
	 * Paste the metadata stored in data file to targeted block
	 * 
	 * @param Player
	 * @param Block
	 */
	public void pasteMeta(Player player, Block block) {
		String playerName = player.getName();
		Integer materialId = configDao.getPluginConfig().getInt(playerName + ".block");
		Integer metadataInteger = configDao.getPluginConfig().getInt(playerName + ".meta");
		if (materialId == 0) {
			player.sendMessage(ChatColor.RED + "Copy a block first");
		} else {
			byte metadata = metadataInteger.byteValue();
			Material material = Material.getMaterial(materialId);
			block.setType(material);
			block.setData(metadata);
			PluginUtils.refreshBlock(block);
		}
	}

}
