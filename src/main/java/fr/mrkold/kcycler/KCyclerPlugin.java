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
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import fr.mrkold.kcycler.Utils.NoGravity;
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
	private String updateMessage = "";

	public File getDataFile() {
		return dataFile;
	}

	public FileConfiguration getPluginConfig() {
		return pluginConfig;
	}

	// Détection de WorldGuard
	private WorldGuardPlugin getWorldGuard() throws Exception {
		Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
		if (wg == null || !(wg instanceof WorldGuardPlugin)) {
			throw new Exception("Worldguard is not running on the server");
		}
		return (WorldGuardPlugin) wg;
	}

	@Override
	public void onEnable() {

		// Enregistrement des evenements
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		// Statistiques
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			getLogger().info("Failed to submit the stats");
		}

		// Initialisation
		biomeCycler = new BiomeCycler(this, Material.BLAZE_ROD);
		metaCycler = new MetaCycler(this, Material.STICK);
		paintCycler = new PaintCycler();
		commandHandler = new CommandManager(this);
		pluginDescription = this.getDescription();

		getCommand(BIOMETOOL_COMMAND).setExecutor(commandHandler);
		getCommand(METATOOL_COMMAND).setExecutor(commandHandler);
		getCommand(PLAYERHEAD_COMMAND).setExecutor(commandHandler);

		// Création de la config
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		// Création du fichier data.yml
		dataFile = new File(getDataFolder(), DATA_FILE_NAME);
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				getLogger().warning("Failed to write data file");
				e.printStackTrace();
			}
		}

		// Création de la liste des blocs
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

		// Chargement du fichier de config
		pluginConfig = YamlConfiguration.loadConfiguration(dataFile);

		// Vérification de la version
		updateMessage = UpdateChecker.checkVersion(pluginDescription);
		if (!updateMessage.equals("")) {
			Bukkit.getServer().getConsoleSender().sendMessage(updateMessage);
		}

		// Affichage du message de succès pour le chargement
		getLogger().info(pluginDescription.getName() + " v" + pluginDescription.getVersion() + " enabled");
	}

	@Override
	public void onDisable() {
		getLogger().info(pluginDescription.getName() + " v" + pluginDescription.getVersion() + " disabled");
	}

	// A la connection
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().isOp()) {
			if (!updateMessage.equalsIgnoreCase("")) {
				e.getPlayer().sendMessage(updateMessage);
			}
		}
	}

	// -------------------------------------------------------
	// Antigravite
	@EventHandler
	public void onCheckGravity(BlockPhysicsEvent event) {
		NoGravity.cancelGravity(this, event);
	}

	// -------------------------------------------------------
	// Empeche l'oeuf de dragon de se teleporter et les torches/webs de depop
	// sous l'eau
	@EventHandler
	public void onBlockChange(BlockFromToEvent event) {
		List<String> blockList = Arrays.asList("TORCH", "WEB");
		if (blockList.contains(event.getToBlock().getType().toString())
				|| event.getBlock().getType() == Material.DRAGON_EGG) {
			event.setCancelled(true);
		}
	}

	// -------------------------------------------------------
	// Cycler
	@EventHandler
	public void Cycler(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Block b = event.getClickedBlock();
		if (b != null) {
			List<Integer> blacklist = getConfig().getIntegerList("cycler-blacklist");
			// Ajout de la double plante à la blacklist
			blacklist.add(175);
			if (p.hasPermission(USE_PERMISSION) && !blacklist.contains(b.getTypeId())) {
				try {
					if (getWorldGuard().canBuild(p, b) || p.hasPermission(ADMIN_PERMISSION)) {
						Byte md = event.getClickedBlock().getData();
						if (p.isSneaking()) {
							// Copier
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
							// Coller
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
							// En avant
							if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
								if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
									biomeCycler.rightClickBlock(p, b);
									event.setCancelled(true);
								}
								if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
									metaCycler.rightClickBlock(p, b, md);
									event.setCancelled(true);
								}
							}
							// En arrière
							if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
								if (event.getPlayer().getItemInHand().getType() == biomeCycler.getMaterial()) {
									biomeCycler.leftClickBlock(p, b);
									event.setCancelled(true);
								}
								if (event.getPlayer().getItemInHand().getType() == metaCycler.getMaterial()) {
									metaCycler.leftClickBlock(p, b, md);
									event.setCancelled(true);
								}
							}
						}
					} else {
						p.sendMessage(ChatColor.RED + "You don't have permission to do this here");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Affichage de l'id du bloc lorsqu'on le regarde
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

	// Paint Cycler
	@EventHandler
	public void rClickPainting(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (p.hasPermission(USE_PERMISSION)) {
			Entity ent = e.getRightClicked();
			Location loc = ent.getLocation();
			try {
				if (getWorldGuard().canBuild(p, loc) || p.hasPermission(ADMIN_PERMISSION)) {
					if (ent.getType() == EntityType.PAINTING) {
						if (p.getItemInHand().getType() == metaCycler.getMaterial()) {
							paintCycler.rClick(ent);
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@EventHandler
	public void lClickPainting(PaintingBreakByEntityEvent e) {
		Player p = (Player) e.getRemover();
		if (p.hasPermission(USE_PERMISSION)) {
			Painting painting = e.getPainting();
			Location loc = painting.getLocation();
			try {
				if (getWorldGuard().canBuild(p, loc) || p.hasPermission(ADMIN_PERMISSION)) {
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

	// Si l'on marche sur une plaque de pression
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

	// Block pick avec metadata
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

	public void giveBiomeTool(Player player) {
		player.setItemInHand(new ItemStack(biomeCycler.getMaterial(), 1));
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
	}

	public void giveMetaTool(Player player) {
		player.setItemInHand(new ItemStack(metaCycler.getMaterial(), 1));
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
	}

	public void givePlayerHead(Player player, String headOwner) {
		ItemStack skull = new ItemStack(397, 1, (short) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(headOwner);
		skull.setItemMeta(meta);
		player.setItemInHand(skull);
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
	}

}
