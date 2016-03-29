package fr.mrkold.kcycler.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import fr.mrkold.kcycler.KCyclerPlugin;
import fr.mrkold.kcycler.utils.GravityUtils;

public class WorldEventListener implements Listener {

	KCyclerPlugin plugin;

	public WorldEventListener(KCyclerPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Cancel leaves decay
	 * 
	 * @param LeavesDecayEvent
	 */
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		boolean leavesDecay = plugin.getConfig().getBoolean("prevent-leaves-decay");
		event.setCancelled(leavesDecay);
	}

	/**
	 * Cancel gravity
	 * 
	 * @param BlockPhysicsEvent
	 */
	@EventHandler
	public void onCheckGravity(BlockPhysicsEvent event) {
		GravityUtils.cancelGravity(plugin, event);
	}

	/**
	 * Prevent Enderdragon egg to teleport when clicked and torch/web to break
	 * under water
	 * 
	 * @param BlockFromToEvent
	 */
	@EventHandler
	public void onBlockChange(BlockFromToEvent event) {
		List<Material> blockList = Arrays.asList(Material.TORCH, Material.WEB, Material.DRAGON_EGG);
		if (blockList.contains(event.getBlock().getType())) {
			event.setCancelled(true);
		}
	}

}
