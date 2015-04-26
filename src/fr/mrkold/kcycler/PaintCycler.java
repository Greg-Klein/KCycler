package fr.mrkold.kcycler;

import org.bukkit.Art;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;

public class PaintCycler {
	
	public MainClass plugin;
	public PaintCycler(MainClass plugin){
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void rClick(Entity ent){
		Painting painting = (Painting) ent;
        int paintingID = painting.getArt().getId();
        int pID = ++paintingID;
        if(pID == 21){
        	pID = 0;
        }
        painting.setArt(Art.getById(pID));
	}
	
	@SuppressWarnings("deprecation")
	public void lClick(Painting painting){
		int paintingID = painting.getArt().getId();
		 int pID = --paintingID;
	        if(pID < 0){
	        	pID = 20;
	        }
        painting.setArt(Art.getById(pID));
	}
}
