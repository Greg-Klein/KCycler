package fr.mrkold.kcycler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import fr.mrkold.kcycler.Utils.NoGravity;
import fr.mrkold.kcycler.Utils.PermissionsUtils;
import fr.mrkold.kcycler.Utils.PluginUtils;
import fr.mrkold.kcycler.tools.BiomeCycler;
import fr.mrkold.kcycler.tools.MetaCycler;
import fr.mrkold.kcycler.tools.PaintCycler;
import update.checker.UpdateChecker;

@SuppressWarnings("deprecation")
public class KCyclerPlugin extends JavaPlugin implements Listener {

	public final static String DATA_FILE_NAME = "data.yml";

	private File dataFile;
	private FileConfiguration pluginConfig;
	private BiomeCycler biomeCycler;
	private MetaCycler metaCycler;
	private PaintCycler paintCycler;
	private PluginUtils pluginUtils;
	private PermissionsUtils permsUtils;
	private String updateMessage;
	private List<Integer> cyclerBlacklist;

	/**
	 * Getters & Setters
	 */

	public File getDataFile() {
		return dataFile;
	}

	public FileConfiguration getPluginConfig() {
		return pluginConfig;
	}

	public PluginUtils getPluginUtils() {
		return pluginUtils;
	}

	/**
	 * END Getters & Setters
	 */

	@Override
	public void onEnable() {

		/**
		 * Register events
		 */
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		/**
		 * Stats
		 */
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			getLogger().info("Failed to submit the stats");
		}

		/**
		 * Tools
		 */
		biomeCycler = new BiomeCycler(this);
		metaCycler = new MetaCycler(this);
		paintCycler = new PaintCycler();

		/**
		 * Utils
		 */
		pluginUtils = new PluginUtils(this);
		permsUtils = new PermissionsUtils(this);

		/**
		 * Commands
		 */
		CommandManager commandHandler = new CommandManager(this);
		getCommand(CommandManager.BIOMETOOL_COMMAND).setExecutor(commandHandler);
		getCommand(CommandManager.METATOOL_COMMAND).setExecutor(commandHandler);
		getCommand(CommandManager.PLAYERHEAD_COMMAND).setExecutor(commandHandler);

		/**
		 * Create config
		 */
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		/**
		 * Create and load data file
		 */
		dataFile = new File(getDataFolder(), DATA_FILE_NAME);
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				getLogger().warning("Failed to write data file");
				e.printStackTrace();
			}
		}
		pluginConfig = YamlConfiguration.loadConfiguration(dataFile);

		/**
		 * Create blocks list file
		 */
		File materialList = new File(getDataFolder(), "Material_List.txt");
		if (!materialList.exists()) {
			Material[] materials = Material.class.getEnumConstants();
			try {
				FileWriter fileWriter = new FileWriter(materialList);
				fileWriter.write("NAME - ID\n----------\n");
				for (Material material : materials) {
					fileWriter.write(material.toString() + " - " + material.getId() + "\n");
				}
				fileWriter.close();
				materialList.createNewFile();
			} catch (IOException e) {
				getLogger().warning("Failed to write material list file");
				e.printStackTrace();
			}
		}

		/**
		 * Check for new version
		 */
		PluginDescriptionFile pluginDescription = this.getDescription();
		updateMessage = UpdateChecker.checkVersion(pluginDescription);
		if (!updateMessage.isEmpty()) {
			Bukkit.getServer().getConsoleSender().sendMessage(updateMessage);
		}

		/**
		 * Initialize blacklist and add double plant to it
		 */
		cyclerBlacklist = getConfig().getIntegerList("cycler-blacklist");
		cyclerBlacklist.add(175);

		/**
		 * Show loading success message
		 */
		getLogger().info(pluginDescription.getName() + " v" + pluginDescription.getVersion() + " enabled");
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pluginDescription = this.getDescription();
		getLogger().info(pluginDescription.getName() + " v" + pluginDescription.getVersion() + " disabled");
	}

	/**
	 * When player join game
	 * 
	 * @param PlayerJoinEvent
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().isOp()) {
			if (!updateMessage.equalsIgnoreCase("")) {
				event.getPlayer().sendMessage(updateMessage);
			}
		}
	}

	/**
	 * Cancel leaves decay
	 * 
	 * @param LeavesDecayEvent
	 */
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		boolean leavesDecay = getConfig().getBoolean("prevent-leaves-decay");
		event.setCancelled(leavesDecay);
	}

	/**
	 * Cancel gravity
	 * 
	 * @param BlockPhysicsEvent
	 */
	@EventHandler
	public void onCheckGravity(BlockPhysicsEvent event) {
		NoGravity.cancelGravity(this, event);
	}

	/**
	 * Prevent Enderdragon egg to teleport when clicked and torch/web to break
	 * under water
	 * 
	 * @param BlockFromToEvent
	 */
	@EventHandler
	public void onBlockChange(BlockFromToEvent event) {
		List<Material> blockList = Arrays.asList(Material.TORCH, Material.WEB, Material.DRAGON_EGG);
		if (blockList.contains(event.getBlock().getType())) {
			event.setCancelled(true);
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
					setItemInHandName(ChatColor.GREEN, name, player);
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
		List<Integer> pickupBlacklist = getConfig().getIntegerList("pick-blacklist");
		if (player.isSneaking() && !pickupBlacklist.contains(block.getTypeId())) {
			byte metadata = player.getTargetBlock(null, 10).getData();
			Material material = event.getCursor().getType();
			ItemStack item = new ItemStack(material, 1);
			String name = material.getId() + ":" + metadata;
			setItemInHandName(ChatColor.GREEN, name, player);
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

	/**
	 * Gives the biome tool to the player
	 * 
	 * @param player
	 */
	public void giveBiomeTool(Player player) {
		player.setItemInHand(new ItemStack(biomeCycler.getMaterial(), 1));
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
	}

	/**
	 * Gives the meta tool to the player
	 * 
	 * @param player
	 */
	public void giveMetaTool(Player player) {
		player.setItemInHand(new ItemStack(metaCycler.getMaterial(), 1));
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
