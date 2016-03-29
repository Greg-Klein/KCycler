package fr.mrkold.kcycler.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import fr.mrkold.kcycler.KCyclerPlugin;
import fr.mrkold.kcycler.tools.BiomeCycler;
import fr.mrkold.kcycler.tools.MetaCycler;
import fr.mrkold.kcycler.tools.PaintCycler;
import fr.mrkold.kcycler.utils.PermissionsUtils;

@SuppressWarnings("deprecation")
public class PlayerEventListener implements Listener {

	private KCyclerPlugin plugin;
	private PermissionsUtils permsUtils;
	private BiomeCycler biomeCycler;
	private MetaCycler metaCycler;
	private PaintCycler paintCycler;
	private List<Integer> cyclerBlacklist;

	public PlayerEventListener(KCyclerPlugin plugin) {
		this.plugin = plugin;
		permsUtils = new PermissionsUtils(plugin);
		biomeCycler = plugin.getBiomeCycler();
		metaCycler = plugin.getMetaCycler();
		paintCycler = plugin.getPaintCycler();

		/**
		 * Initialize blacklist and add double plant to it
		 */
		cyclerBlacklist = plugin.getConfig().getIntegerList("cycler-blacklist");
		cyclerBlacklist.add(175);

	}

	/**
	 * When player join game
	 * 
	 * @param PlayerJoinEvent
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().isOp()) {
			if (!plugin.getUpdateMessage().equalsIgnoreCase("")) {
				event.getPlayer().sendMessage(plugin.getUpdateMessage());
			}
		}
	}

	/**
	 * Cycle through biomes and metadatas
	 * 
	 * @param PlayerInteractEvent
	 */
	@EventHandler
	public void Cycler(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		/**
		 * Check if the clicked block is null
		 */
		if (block != null) {
			/**
			 * Check 3rd party plugins permissions
			 */
			if (permsUtils.canUseAt(player, block.getLocation())) {
				if (player.isSneaking()) {
					/**
					 * Copy
					 */
					if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
						if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
							biomeCycler.copyBiome(player, block);
							event.setCancelled(true);
						}
						if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
							if (!cyclerBlacklist.contains(block.getTypeId())) {
								metaCycler.copyMeta(player, block);
							}
							event.setCancelled(true);
						}
					}
					/**
					 * Paste
					 */
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
							biomeCycler.pasteBiome(player, block);
							event.setCancelled(true);
						}
						if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
							metaCycler.pasteMeta(player, block);
							event.setCancelled(true);
						}
					}
				} else {
					/**
					 * Cycling
					 */
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
							biomeCycler.rightClickBlock(player, block);
							event.setCancelled(true);
						}
						if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
							if (!cyclerBlacklist.contains(block.getTypeId())) {
								metaCycler.rightClickBlock(player, block);
							}
							event.setCancelled(true);
						}
					}
					/**
					 * Cycling (reverse)
					 */
					if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
						if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
							biomeCycler.leftClickBlock(player, block);
							event.setCancelled(true);
						}
						if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
							if (!cyclerBlacklist.contains(block.getTypeId())) {
								metaCycler.leftClickBlock(player, block);
							}
							event.setCancelled(true);
						}
					}
				}
			} else if (player.hasPermission(PermissionsUtils.USE_PERMISSION)) {
				// If player has use permission but no permission to use KCycler
				// here
				player.sendMessage(ChatColor.RED + "You don't have permission to do this here");
			}
		}
	}

	/**
	 * Shows block id and metadata when player target it
	 * 
	 * @param event
	 */
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission(PermissionsUtils.USE_PERMISSION)) {
			ItemStack itemInHand = event.getPlayer().getItemInHand();
			if (itemInHand.getType() == metaCycler.getMaterial()) {
				Block block = event.getPlayer().getTargetBlock(null, 10);
				int blockId = block.getTypeId();
				if (blockId != 0) {
					int blockMetadata = block.getData();
					String name = blockId + ":" + blockMetadata;
					plugin.getPluginUtils().setItemInHandName(ChatColor.GREEN, name, player);
				}
			}
		}
	}

	/**
	 * Right click on a painting
	 * 
	 * @param PlayerInteractEntityEvent
	 */
	@EventHandler
	public void rClickPainting(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Painting clickedPainting = (Painting) event.getRightClicked();
		Location paintingLocation = clickedPainting.getLocation();
		if (permsUtils.canUseAt(player, paintingLocation)) {
			if (clickedPainting.getType() == EntityType.PAINTING) {
				if (player.getItemInHand().getType() == metaCycler.getMaterial()) {
					paintCycler.rightClick(clickedPainting);
				}
			}
		}
	}

	/**
	 * Left click on a painting
	 * 
	 * @param PaintingBreakByEntityEvent
	 */
	@EventHandler
	public void lClickPainting(PaintingBreakByEntityEvent event) {
		Player player = (Player) event.getRemover();
		Painting clickedPainting = event.getPainting();
		Location paintingLocation = clickedPainting.getLocation();
		if (permsUtils.canUseAt(player, paintingLocation)) {
			if (player.getItemInHand().getType() == metaCycler.getMaterial()) {
				paintCycler.leftClick(clickedPainting);
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Cancel default event when player walk on an action plate with metadata
	 * 
	 * @param PlayerInteractEvent
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.PHYSICAL) {
			if ((event.getClickedBlock().getType().toString().contains("PLATE"))) {
				byte metadata = event.getClickedBlock().getData();
				// Si la metadata est differente de 0 on annule l'action
				if (metadata != 0) {
					event.setCancelled(true);
				}
			}
		}
	}

	/**
	 * Pick block with metadata
	 * 
	 * @param InventoryCreativeEvent
	 */
	@EventHandler
	public void onPick(InventoryCreativeEvent event) {
		Player player = (Player) event.getInventory().getHolder();
		Block block = player.getTargetBlock(null, 10);
		List<Integer> pickupBlacklist = plugin.getConfig().getIntegerList("pick-blacklist");
		if (player.isSneaking() && !pickupBlacklist.contains(block.getTypeId())) {
			byte metadata = player.getTargetBlock(null, 10).getData();
			Material material = event.getCursor().getType();
			ItemStack item = new ItemStack(material, 1);
			String name = material.getId() + ":" + metadata;
			plugin.getPluginUtils().setItemInHandName(ChatColor.GREEN, name, player);
			event.setCursor(item);
		}
	}

	/**
	 * Block place with metadata
	 * 
	 * @param BlockPlaceEvent
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		String itemName = player.getItemInHand().getItemMeta().getDisplayName();
		try {
			String[] itemNameArray = itemName.split(":");
			if (itemNameArray[1] != null) {
				byte metadata = Byte.parseByte(itemNameArray[1]);
				event.getBlockPlaced().setData(metadata);
			}
		} catch (Exception exception) {
			// Ne rien faire
		}
	}
}
