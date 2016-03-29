package fr.mrkold.kcycler;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import fr.mrkold.kcycler.dao.ConfigDao;
import fr.mrkold.kcycler.listeners.PlayerEventListener;
import fr.mrkold.kcycler.listeners.WorldEventListener;
import fr.mrkold.kcycler.tools.BiomeCycler;
import fr.mrkold.kcycler.tools.MetaCycler;
import fr.mrkold.kcycler.tools.PaintCycler;
import fr.mrkold.kcycler.utils.PluginUtils;
import update.checker.UpdateChecker;

public class KCyclerPlugin extends JavaPlugin implements Listener {

	private PluginUtils pluginUtils;
	private ConfigDao configDao;
	private String updateMessage;
	private BiomeCycler biomeCycler;
	private MetaCycler metaCycler;
	private PaintCycler paintCycler;

	/**
	 * Getters & Setters
	 */
	public PluginUtils getPluginUtils() {
		return pluginUtils;
	}

	public ConfigDao getConfigDao() {
		return configDao;
	}

	public String getUpdateMessage() {
		return updateMessage;
	}

	public BiomeCycler getBiomeCycler() {
		return biomeCycler;
	}

	public MetaCycler getMetaCycler() {
		return metaCycler;
	}

	public PaintCycler getPaintCycler() {
		return paintCycler;
	}

	/**
	 * END Getters & Setters
	 */

	@Override
	public void onEnable() {

		/**
		 * Stats
		 */
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {
			getLogger().info("Failed to submit the stats");
		}

		/**
		 * Config
		 */
		configDao = new ConfigDao(this);
		configDao.copyDefault();
		configDao.createMaterialList();

		/**
		 * Tools
		 */
		biomeCycler = new BiomeCycler(this);
		metaCycler = new MetaCycler(this);
		paintCycler = new PaintCycler();

		/**
		 * Utils
		 */
		pluginUtils = new PluginUtils(this);

		/**
		 * Commands
		 */
		CommandManager commandHandler = new CommandManager(this);
		getCommand(CommandManager.BIOMETOOL_COMMAND).setExecutor(commandHandler);
		getCommand(CommandManager.METATOOL_COMMAND).setExecutor(commandHandler);
		getCommand(CommandManager.PLAYERHEAD_COMMAND).setExecutor(commandHandler);

		/**
		 * Register event listeners
		 */
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new WorldEventListener(this), this);

		/**
		 * Check for new version
		 */
		PluginDescriptionFile pluginDescription = this.getDescription();
		updateMessage = UpdateChecker.checkVersion(pluginDescription);
		if (!updateMessage.isEmpty()) {
			Bukkit.getServer().getConsoleSender().sendMessage(updateMessage);
		}

		/**
		 * Show loading success message
		 */
		getLogger().info(pluginDescription.getName() + " v" + pluginDescription.getVersion() + " enabled");
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pluginDescription = this.getDescription();
		getLogger().info(pluginDescription.getName() + " v" + pluginDescription.getVersion() + " disabled");
	}

}
