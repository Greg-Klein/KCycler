package fr.mrkold.kcycler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class BiomeCycler {
	
	public MainClass plugin;
	public BiomeCycler(MainClass plugin){
		this.plugin = plugin;
	}
	
	private List<Biome> biomes = Arrays.asList(Biome.values());
    private int maxBiome = biomes.size() - 1;
    
    public Biome getNextBiome(Biome b)
    {
        int i = biomes.indexOf(b) + 1;
        if (i >= maxBiome)
        {
            i = 0;
        }
        return biomes.get(i);
    }

    public Biome getPreviousBiome(Biome b)
    {
        int i = biomes.indexOf(b) - 1;
        if (i <= 0)
        {
            i = maxBiome;
        }
        return biomes.get(i);
    }
    
    public Biome getBiome(String bi){
    	int i=1;
		while(bi != biomes.get(i).toString()){
    		i++;
    	}
		return biomes.get(i);
    }
    
    public void setBiome(Block b, Biome bi)
    {
        World w = b.getWorld();
        Chunk c = b.getChunk();
        w.setBiome(b.getX(), b.getZ(), bi);
        w.refreshChunk(c.getX(), c.getZ());
    }
    
    public void leftClickBlock(Player p, Block b)
    {
        ItemStack wand = p.getItemInHand();
        ItemMeta im = wand.getItemMeta();
        im.setDisplayName(ChatColor.GREEN.toString() + getPreviousBiome(b.getBiome()).name());
        wand.setItemMeta(im);
        setBiome(b, getPreviousBiome(b.getBiome()));
    }

    public void rightClickBlock(Player p, Block b)
    {
    	ItemStack wand = p.getItemInHand();
        ItemMeta im = wand.getItemMeta();
        im.setDisplayName(ChatColor.GREEN.toString() + getNextBiome(b.getBiome()).name());
        wand.setItemMeta(im);
        setBiome(b, getNextBiome(b.getBiome()));
    }
    
    public void copyBiome(Player p, Block b){
    	String player = p.getName();
    	String bi = b.getBiome().toString();
    	plugin.data.set(player +".biome", bi);
    	saveData();
    	ItemStack wand = p.getItemInHand();
		ItemMeta im = wand.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + b.getBiome().toString());
		wand.setItemMeta(im);
    }
    
    public void pasteBiome(Player p, Block b){
    	String player = p.getName();
    	String bi = plugin.data.getString(player +".biome");
    	if(bi == ""){
    		p.sendMessage(ChatColor.RED + "Copiez un biome d'abord");
    	}
    	else{
    		Biome bio = getBiome(bi);
        	setBiome(b, bio);
    	}
    }
    
    public void saveData() {
		try {
			plugin.data.save(plugin.myFile);
		} catch (IOException e) {
			// catch block
			e.printStackTrace();
		}
	}

}
