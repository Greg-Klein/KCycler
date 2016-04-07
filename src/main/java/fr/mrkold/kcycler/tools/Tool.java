package fr.mrkold.kcycler.tools;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface Tool {

	public void copy(Player player, Block block);

	public void paste(Player player, Block block);

	public void previous(Player player, Block block);

	public void next(Player player, Block block);

}
