package fr.mrkold.kcycler;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MetaCycler {
	
	public MainClass plugin;
	public MetaCycler(MainClass plugin){
		this.plugin = plugin;
	}
	
	
	@SuppressWarnings("deprecation")
	public static void leftClickBlock(Player player, Block b, Byte md){
    	b.setData((byte) (md - 1));
    	refreshChunk(b);
    	Block block = player.getTargetBlock(null, 5);
		ItemMeta im = player.getItemInHand().getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "" + block.getTypeId() + ":" + block.getData());
		player.getItemInHand().setItemMeta(im);
	}
	
	@SuppressWarnings("deprecation")
	public static void rightClickBlock(Player player, Block b, Byte md){
    	b.setData((byte) (md + 1));
    	refreshChunk(b);
    	Block block = player.getTargetBlock(null, 5);
		ItemMeta im = player.getItemInHand().getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "" + block.getTypeId() + ":" + block.getData());
		player.getItemInHand().setItemMeta(im);
	}
	
	@SuppressWarnings("deprecation")
	public void copyMeta(Player p, Block b){
		String player = p.getName();
    	byte md = b.getData();
    	Integer mat = b.getTypeId();
    	plugin.data.set(player +".block", mat);
    	plugin.data.set(player +".meta", md);
    	saveData();
    	ItemStack wand = p.getItemInHand();
		ItemMeta im = wand.getItemMeta();
    	im.setDisplayName(ChatColor.GOLD + "" + mat.toString() + ":" + md);
    	wand.setItemMeta(im);
    }
    
    @SuppressWarnings("deprecation")
	public void pasteMeta(Player p, Block b){
    	String player = p.getName();
    	Integer matint = plugin.data.getInt(player +".block");
    	Integer mdint = plugin.data.getInt(player +".meta");
    	if(matint == 0){
    		p.sendMessage(ChatColor.RED + "Copiez un bloc d'abord");
    	}
    	else{
    		byte md = mdint.byteValue();
        	Material mat = Material.getMaterial(matint);
        	b.setType(mat);
        	b.setData(md);
        	refreshChunk(b);
    	}
    }
    
    public void saveData() {
		try {
			plugin.data.save(plugin.myFile);
		} catch (IOException e) {
			// catch block
			plugin.getLogger().info("DEBUG");
			e.printStackTrace();
		}
	}
    
    static void refreshChunk(Block b){
    	World w = b.getWorld();
        Chunk c = b.getChunk();
        w.refreshChunk(c.getX(), c.getZ());
    }
}
