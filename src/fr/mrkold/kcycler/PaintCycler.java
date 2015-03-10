package fr.mrkold.kcycler;

import java.util.Arrays;

import org.bukkit.Art;

public class PaintCycler {
	
	public MainClass plugin;
	public PaintCycler(MainClass plugin){
		this.plugin = plugin;
	}
	
	public Art getNextArt(Art art) {
		if (Art.values().length-1 == Arrays.asList(Art.values()).indexOf(art)) {
			return Art.values()[0];
		}
		return Art.values()[Arrays.asList(Art.values()).indexOf(art)+1];
	}
}
