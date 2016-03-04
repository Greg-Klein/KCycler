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
import org.bukkit.entity.Entity;
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
public class KCyclerPlugin extends JavaPlugin implements Listener, PluginConstants {

	private PluginDescriptionFile pluginDescription;
	private File dataFile;
	private FileConfiguration pluginConfig;
	private BiomeCycler biomeCycler;
	private MetaCycler metaCycler;
	private PaintCycler paintCycler;
	private CommandManager commandHandler;
	private PluginUtils pluginUtils;
	private PermissionsUtils permsUtils;
	private String updateMessage = "";
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
		 * Initialisation
		 */
		biomeCycler = new BiomeCycler(this, Material.BLAZE_ROD);
		metaCycler = new MetaCycler(this, Material.STICK);
		paintCycler = new PaintCycler();
		commandHandler = new CommandManager(this);
		pluginUtils = new PluginUtils(this);
		permsUtils = new PermissionsUtils(this);
		pluginDescription = this.getDescription();

		getCommand(BIOMETOOL_COMMAND).setExecutor(commandHandler);
		getCommand(METATOOL_COMMAND).setExecutor(commandHandler);
		getCommand(PLAYERHEAD_COMMAND).setExecutor(commandHandler);

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
		File matList = new File(getDataFolder(), "Material_List.txt");
		if (!matList.exists()) {
			Material[] materials = Material.class.getEnumConstants();
			try {
				FileWriter fw = new FileWriter(matList);
				fw.write("NAME - ID\n----------\n");
				for (Material m : materials) {
					fw.write(m.toString() + " - " + m.getId() + "\n");
				}
				fw.close();
				matList.createNewFile();
			} catch (IOException e) {
				getLogger().warning("Failed to write material list file");
				e.printStackTrace();
			}
		}

		/**
		 * Check for new version
		 */
		updateMessage = UpdateChecker.checkVersion(pluginDescription);
		if (!updateMessage.equals("")) {
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
		getLogger().info(pluginDescription.getName() + " v" + pluginDescription.getVersion() + " disabled");
	}

	/**
	 * When player join game
	 * 
	 * @param PlayerJoinEvent
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().isOp()) {
			if (!updateMessage.equalsIgnoreCase("")) {
				e.getPlayer().sendMessage(updateMessage);
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
	 * Prevent Enderdragon egg to teleport when clicked
	 * 
	 * @param BlockFromToEvent
	 */
	@EventHandler
	public void onBlockChange(BlockFromToEvent event) {
		List<String> blockList = Arrays.asList("TORCH", "WEB");
		if (blockList.contains(event.getToBlock().getType().toString())
				|| event.getBlock().getType() == Material.DRAGON_EGG) {
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
		Player p = event.getPlayer();
		/**
		 * Check if player has permission to use the plugin
		 */
		if (p.hasPermission(USE_PERMISSION)) {
			Block b = event.getClickedBlock();
			/**
			 * Check if the clicked block is null or on blacklist
			 */
			if (b != null && !cyclerBlacklist.contains(b.getTypeId())) {
				/**
				 * Check 3rd party plugins permissions
				 */
				if (permsUtils.canBuildAt(p, b.getLocation())) {
					if (p.isSneaking()) {
						/**
						 * Copy
						 */
						if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
							if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
								biomeCycler.copyBiome(p, b);
								event.setCancelled(true);
							}
							if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
								metaCycler.copyMeta(p, b);
								event.setCancelled(true);
							}
						}
						/**
						 * Paste
						 */
						if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
								biomeCycler.pasteBiome(p, b);
								event.setCancelled(true);
							}
							if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
								metaCycler.pasteMeta(p, b);
								event.setCancelled(true);
							}
						}
					} else {
						/**
						 * Cycling
						 */
						if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
								biomeCycler.rightClickBlock(p, b);
								event.setCancelled(true);
							}
							if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
								metaCycler.rightClickBlock(p, b);
								event.setCancelled(true);
							}
						}
						/**
						 * Cycling (reverse)
						 */
						if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
							if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
								biomeCycler.leftClickBlock(p, b);
								event.setCancelled(true);
							}
							if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
								metaCycler.leftClickBlock(p, b);
								event.setCancelled(true);
							}
						}
					}
				} else {
					p.sendMessage(ChatColor.RED + "You don't have permission to do this here");
				}
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
		ItemStack wand = event.getPlayer().getItemInHand();
		if (wand.getType() == metaCycler.getMaterial()) {
			Block b = event.getPlayer().getTargetBlock(null, 10);
			int id = b.getTypeId();
			if (id != 0) {
				byte mdb = b.getData();
				int md = mdb;
				ItemMeta im = wand.getItemMeta();
				im.setDisplayName(ChatColor.GREEN.toString() + id + ":" + md);
				wand.setItemMeta(im);
			}
		}
	}

	/**
	 * Right click on a painting
	 * 
	 * @param PlayerInteractEntityEvent
	 */
	@EventHandler
	public void rClickPainting(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission(USE_PERMISSION)) {
			Entity ent = e.getRightClicked();
			Location loc = ent.getLocation();
			try {
				if (pluginUtils.getWorldGuard().canBuild(p, loc) || p.hasPermission(ADMIN_PERMISSION)) {
					if (ent.getType() == EntityType.PAINTING) {
						if (p.getItemInHand().getType() == metaCycler.getMaterial()) {
							paintCycler.rClick((Painting) ent);
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Left click on a painting
	 * 
	 * @param PaintingBreakByEntityEvent
	 */
	@EventHandler
	public void lClickPainting(PaintingBreakByEntityEvent e) {
		Player p = (Player) e.getRemover();
		if (p.hasPermission(USE_PERMISSION)) {
			Painting painting = e.getPainting();
			Location loc = painting.getLocation();
			try {
				if (pluginUtils.getWorldGuard().canBuild(p, loc) || p.hasPermission(ADMIN_PERMISSION)) {
					if (p.getItemInHand().getType() == metaCycler.getMaterial()) {
						paintCycler.lClick(painting);
						e.setCancelled(true);
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
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
	public void onPick(InventoryCreativeEvent e) {
		Player p = (Player) e.getInventory().getHolder();
		Block b = p.getTargetBlock(null, 5);
		List<Integer> puBl = getConfig().getIntegerList("pick-blacklist");
		if (p.isSneaking() && !puBl.contains(b.getTypeId())) {
			byte metadata = p.getTargetBlock(null, 5).getData();
			Material mat = e.getCursor().getType();
			ItemStack item = new ItemStack(mat, 1);
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "" + mat.getId() + ":" + metadata);
			item.setItemMeta(im);
			e.setCursor(item);
		}
	}

	/**
	 * Block place with metadata
	 * 
	 * @param BlockPlaceEvent
	 */
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		String name = p.getItemInHand().getItemMeta().getDisplayName();
		try {
			String[] tab = name.split(":");
			if (tab[1] != null) {
				int mdint = Integer.parseInt(tab[1]);
				byte md = (byte) mdint;
				e.getBlockPlaced().setData(md);
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
