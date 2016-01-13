package fr.mrkold.kcycler;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.Plugin;

public class NoGravity {
	
	public static void cancelGravity(Plugin plugin, BlockPhysicsEvent event){
		Material materiaux = event.getBlock().getType();
		String bloc = materiaux.toString();
		List<String> liste = plugin.getConfig().getStringList("antigravity");
			
		if(liste.contains(bloc)){
			event.setCancelled(true);
		}
	}

}
