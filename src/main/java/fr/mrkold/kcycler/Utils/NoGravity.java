package fr.mrkold.kcycler.Utils;

import java.util.List;

import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.Plugin;

public class NoGravity {

	/**
	 * Cancel gravity for a list of blocks defined in configuration file
	 * 
	 * @param Plugin
	 * @param BlockPhysicsEvent
	 */
	public static void cancelGravity(Plugin plugin, BlockPhysicsEvent event) {
		String bloc = event.getBlock().getType().toString();
		List<String> liste = plugin.getConfig().getStringList("antigravity");

		if (liste.contains(bloc)) {
			event.setCancelled(true);
		}
	}

}
