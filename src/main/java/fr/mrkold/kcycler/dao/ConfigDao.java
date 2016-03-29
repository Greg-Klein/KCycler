package fr.mrkold.kcycler.dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigDao {

	public final static String DATA_FILE_NAME = "data.yml";

	private Plugin plugin;
	private FileConfiguration pluginConfig;
	private File dataFile;

	/**
	 * Getters & Setters
	 */

	public FileConfiguration getPluginConfig() {
		return pluginConfig;
	}

	public File getDataFile() {
		return dataFile;
	}

	/**
	 * END Getters & Setters
	 */

	/**
	 * Constructor
	 */
	public ConfigDao(Plugin plugin) {
		this.plugin = plugin;
		/**
		 * Create and load data file
		 */
		dataFile = new File(plugin.getDataFolder(), DATA_FILE_NAME);
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().warning("Failed to write data file");
				e.printStackTrace();
			}
		}
		pluginConfig = YamlConfiguration.loadConfiguration(dataFile);
	}

	/**
	 * Copy default config
	 */
	public void copyDefault() {
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}

	/**
	 * Create blocks list file
	 */
	@SuppressWarnings("deprecation")
	public void createMaterialList() {
		File materialList = new File(plugin.getDataFolder(), "Material_List.txt");
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
				plugin.getLogger().warning("Failed to write material list file");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Save data to file
	 */
	public void saveData() {
		try {
			pluginConfig.save(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
