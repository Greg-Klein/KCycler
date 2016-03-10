package fr.mrkold.kcycler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import fr.mrkold.kcycler.listeners.EventListener;
import fr.mrkold.kcycler.tools.BiomeCycler;
import fr.mrkold.kcycler.tools.MetaCycler;
import fr.mrkold.kcycler.tools.PaintCycler;
import fr.mrkold.kcycler.utils.PluginUtils;
import update.checker.UpdateChecker;

@SuppressWarnings("deprecation")
public class KCyclerPlugin extends JavaPlugin implements Listener {

	public final static String DATA_FILE_NAME = "data.yml";

	private File dataFile;
	private FileConfiguration pluginConfig;
	private PluginUtils pluginUtils;
	private String updateMessage;
	private BiomeCycler biomeCycler;
	private MetaCycler metaCycler;
	private PaintCycler paintCycler;

	/**
	 * Getters & Setters
	 */

	public File getDataFile() {
		return dataFile;
	}

	public FileConfiguration getPluginConfig() {
		return pluginConfig;
	}

	public PluginUtils getPluginUtils() {
		return pluginUtils;
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
		 * Create config
		 */
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();

		/**
		 * Create and load data file
		 */
		dataFile = new File(getDataFolder(), DATA_FILE_NAME);
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				getLogger().warning("Failed to write data file");
				e.printStackTrace();
			}
		}
		pluginConfig = YamlConfiguration.loadConfiguration(dataFile);

		/**
		 * Create blocks list file
		 */
		File materialList = new File(getDataFolder(), "Material_List.txt");
		if (!materialList.exists()) {
			Material[] materials = Material.class.getEnumConstants();
			try {
				FileWriter fileWriter = new FileWriter(materialList);
				fileWriter.write("NAME - ID\n----------\n");
				for (Material material : materials) {
					fileWriter.write(material.toString() + " - " + material.getId() + "\n");
				}
				fileWriter.close();
				materialList.createNewFile();
			} catch (IOException e) {
				getLogger().warning("Failed to write material list file");
				e.printStackTrace();
			}
		}

		/**
		 * Register event listener
		 */
		EventListener eventListener = new EventListener(this);
		Bukkit.getServer().getPluginManager().registerEvents(eventListener, this);

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
