package fr.mrkold.kcycler.utils;

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
		String block = event.getBlock().getType().toString();
		List<String> affectedBlocksList = plugin.getConfig().getStringList("antigravity");
		plugin.getLogger().info(block + " : " + affectedBlocksList);

		if (affectedBlocksList.contains(block)) {
			event.setCancelled(true);
		}
	}

}
