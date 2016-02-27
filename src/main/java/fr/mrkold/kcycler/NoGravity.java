package fr.mrkold.kcycler;

import java.util.List;

import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.plugin.Plugin;

public class NoGravity {

	public static void cancelGravity(Plugin plugin, BlockPhysicsEvent event) {
		String bloc = event.getBlock().getType().toString();
		List<String> liste = plugin.getConfig().getStringList("antigravity");

		if (liste.contains(bloc)) {
			event.setCancelled(true);
		}
	}

}
