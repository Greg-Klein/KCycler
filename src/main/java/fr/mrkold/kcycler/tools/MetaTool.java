package fr.mrkold.kcycler.tools;

import org.bukkit.Art;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;

import fr.mrkold.kcycler.KCyclerPlugin;
import fr.mrkold.kcycler.dao.ConfigDao;
import fr.mrkold.kcycler.utils.PluginUtils;

@SuppressWarnings("deprecation")
public class MetaTool implements Tool {

	public static Material METATOOL_MATERIAL = Material.STICK;

	private KCyclerPlugin plugin;
	private ConfigDao configDao;

	/**
	 * Constructor
	 */
	public MetaTool(KCyclerPlugin plugin) {
		this.plugin = plugin;
		configDao = plugin.getConfigDao();
		Integer metaTool = plugin.getConfig().getInt("meta-tool");
		if (metaTool != 0) {
			METATOOL_MATERIAL = Material.getMaterial(metaTool);
		}
	}

	/**
	 * Copy given block metadata and store it in data file
	 */
	@Override
	public void copy(Player player, Block block) {
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
	 */
	@Override
	public void paste(Player player, Block block) {
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

	/**
	 * When player left click on a block it set the metadata to previous one
	 */
	@Override
	public void previous(Player player, Block block) {
		block.setData((byte) (block.getData() - 1));
		PluginUtils.refreshBlock(block);
		String name = block.getTypeId() + ":" + block.getData();
		plugin.getPluginUtils().setItemInHandName(ChatColor.GREEN, name, player);
	}

	/**
	 * When player right click on a block it set the metadata to next one
	 */
	@Override
	public void next(Player player, Block block) {
		block.setData((byte) (block.getData() + 1));
		PluginUtils.refreshBlock(block);
		String name = block.getTypeId() + ":" + block.getData();
		plugin.getPluginUtils().setItemInHandName(ChatColor.GREEN, name, player);
	}

	/**
	 * When player left click on a painting it set the metadata to previous one
	 * 
	 * @param painting
	 */
	public void previousPainting(Painting painting) {
		int paintingID = painting.getArt().getId();
		int pID = --paintingID;
		if (pID < 0) {
			pID = 25;
		}
		painting.setArt(Art.getById(pID));
	}

	/**
	 * When player right click on a painting it set the metadata to next one
	 * 
	 * @param painting
	 */
	public void nextPainting(Painting painting) {
		int paintingID = painting.getArt().getId();
		int pID = ++paintingID;
		if (pID > 25) {
			pID = 0;
		}
		painting.setArt(Art.getById(pID));
	}

}
