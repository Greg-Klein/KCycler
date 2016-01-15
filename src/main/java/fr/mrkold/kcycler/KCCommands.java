package fr.mrkold.kcycler;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class KCCommands implements CommandExecutor {
	
	private MainClass plugin;
	
	public KCCommands(MainClass plugin){
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			if(label.equalsIgnoreCase("mt")) {
				p.setItemInHand(new ItemStack(plugin.metaTool, 1));
			    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
			}
			if(label.equalsIgnoreCase("bt")) {
				p.setItemInHand(new ItemStack(plugin.biomeTool, 1));
			    p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
			}
			if(label.equalsIgnoreCase("ph")) {
			    if (args.length == 0) {
					p.sendMessage(ChatColor.RED + "Utilisation: /ph <nomdujoueur>");
					return true;
			    }
			    else {
			    	String a0 = args[0];		            
			    	ItemStack skull = new ItemStack(397, 1, (short) 3);
			    	SkullMeta meta = (SkullMeta) skull.getItemMeta();
			    	meta.setOwner(a0);
			    	skull.setItemMeta(meta);;
			    	p.setItemInHand(skull);
			    	
				   	p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
			    }
			}
		}	
		return false;
	}

}
