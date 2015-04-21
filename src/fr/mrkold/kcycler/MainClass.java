package fr.mrkold.kcycler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


@SuppressWarnings("deprecation")
public class MainClass extends JavaPlugin implements Listener {
	
	private PluginDescriptionFile pdf = this.getDescription();
	private String version = pdf.getVersion();
	private String nomplugin = pdf.getName();
	private Integer bt = getConfig().getInt("biome-tool");
	private Integer mt = getConfig().getInt("meta-tool");
	public Material biomeTool = Material.BLAZE_ROD;
	public Material metaTool = Material.STICK;
	public File dataFile = new File(getDataFolder(), "data.yml");
	public File myFile;
    public FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
    
    private BiomeCycler bcycler;
    private MetaCycler mcycler;
    private PaintCycler pcycler;
	
	private WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) plugin;
	}

	@Override
	public void onEnable(){
		
		getCommand("bt").setExecutor(new KCCommands(this));
		getCommand("mt").setExecutor(new KCCommands(this));
		getCommand("ph").setExecutor(new KCCommands(this));
		
		bcycler = new BiomeCycler(this);
		mcycler = new MetaCycler(this);
		pcycler = new PaintCycler(this);
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		myFile = new File(getDataFolder(), "data.yml");
        if (!myFile.exists()) {
            try {
                myFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
		if(bt != 0 && mt != 0){
			biomeTool = Material.getMaterial(bt);
			metaTool = Material.getMaterial(mt);
		}
		
	    getLogger().info(nomplugin + " v"+ version + " enabled");
	}
	
	@Override
	public void onDisable(){
		getLogger().info(pdf.getName() + " v"+ pdf.getVersion() + " disabled");
	}
	
	
	//-------------------------------------------------------
	// Empêche l'oeuf de dragon de se téléporter
	@EventHandler(priority = EventPriority.HIGH)
	public void onEggTP(BlockFromToEvent event) {
		if((event.getBlock().getType()) == Material.DRAGON_EGG){
			event.setCancelled(true);
		}
	}
	
	//-------------------------------------------------------	
	// Antigravité
	@EventHandler
	public void onCheckGravity(BlockPhysicsEvent event) {
		NoGravity.cancelGravity(this, event);
	}
		
	@EventHandler
	public void onCobwebDepop(BlockFromToEvent event){
		if(event.getToBlock().getType() == Material.WEB){
			event.setCancelled(true);
		}
		else if (event.getToBlock().getType() == Material.TORCH){
			event.setCancelled(true);
		}
	}
	
	//-------------------------------------------------------
	// Cycler
	@EventHandler
	public void Cycler(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Block b = event.getClickedBlock();
		if (b != null){
			if(p.hasPermission("kcycler.use")){
				if(getWorldGuard().canBuild(p, b)||p.hasPermission("kcycler.admin")){
					Byte md = event.getClickedBlock().getData();
					List<Integer> blacklist = getConfig().getIntegerList("cycler-blacklist");
					if(p.isSneaking()){
						if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
							if(event.getPlayer().getItemInHand().getType() == biomeTool){
								bcycler.copyBiome(p, b);
								event.setCancelled(true);
							}
							if(event.getPlayer().getItemInHand().getType() == metaTool){
								mcycler.copyMeta(p, b);
					        	event.setCancelled(true);
							}
						}
						if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if(event.getPlayer().getItemInHand().getType() == biomeTool){
								bcycler.pasteBiome(p, b);
								event.setCancelled(true);
							}
							if(event.getPlayer().getItemInHand().getType() == metaTool){
								mcycler.pasteMeta(p, b);
								event.setCancelled(true);
							}
						}
					}
					else{
						if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if(event.getPlayer().getItemInHand().getType() == biomeTool){
								bcycler.rightClickBlock(p, b);
								event.setCancelled(true);
							}
							if(event.getPlayer().getItemInHand().getType() == metaTool){
								if(blacklist.contains(b.getTypeId())||b.getTypeId() == 175){
									p.sendMessage(ChatColor.RED + "Action impossible");
									event.setCancelled(true);
								}
								else{
									MetaCycler.rightClickBlock(p, b, md);
									event.setCancelled(true);
								}
							}
						}
						if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
							if(event.getPlayer().getItemInHand().getType() == biomeTool){
								bcycler.leftClickBlock(p, b);
								event.setCancelled(true);
							}
							if(event.getPlayer().getItemInHand().getType() == metaTool){
								if(blacklist.contains(b.getTypeId())||b.getTypeId() == 175){
									p.sendMessage(ChatColor.RED + "Action impossible");
									event.setCancelled(true);
								}
								else{
									MetaCycler.leftClickBlock(p, b, md);
									event.setCancelled(true);
								}
							}
						}
					}
				}
				else{
					p.sendMessage(ChatColor.RED +  "Vous n'avez pas l'autorisation d'effectuer cette action ici");
				}
			}
		}
	}
	
	// Affichage de l'id du bloc lorsqu'on le regarde
	@EventHandler
	public void onMove(PlayerMoveEvent event){
		Player p = event.getPlayer();
		if(event.getPlayer().getItemInHand().getType() == metaTool){
			Block b = event.getPlayer().getTargetBlock(null, 10);
			int id = b.getTypeId();
			if(id != 0){
				byte mdb = b.getData();
				int md = (int)mdb;
				ItemStack wand = p.getItemInHand();
		        ItemMeta im = wand.getItemMeta();
		        im.setDisplayName(ChatColor.GREEN.toString() + id + ":" + md);
		        wand.setItemMeta(im);
			}
		}
	}
	
	//Paint Cycler
	@EventHandler
	public void paintingChange(PaintingBreakByEntityEvent event) {
		Painting painting = event.getPainting();
		Player p = (Player) event.getRemover();
		if(p.getItemInHand().getType() == metaTool){
			painting.setArt(pcycler.getNextArt(painting.getArt()));
			event.setCancelled(true);
		}
	}
	
	// Si l'on marche sur une plaque de pression en stone ou wood
		@EventHandler(priority = EventPriority.HIGH)
		public void onInteract(PlayerInteractEvent event) {
			
		    if(event.getAction() == Action.PHYSICAL) {
		        if((event.getClickedBlock().getType() == Material.STONE_PLATE)||(event.getClickedBlock().getType() == Material.WOOD_PLATE)) {
		        	byte metadata = event.getClickedBlock().getData();
		        	// Si la metadata est différente de 0 on annule l'action
		        	if(metadata != 0){
		        		event.setCancelled(true);
		        	}
		        }
		    }
		}
}
