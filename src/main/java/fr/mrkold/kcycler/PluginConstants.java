package fr.mrkold.kcycler;

import org.bukkit.Material;

public interface PluginConstants {

	/**
	 * Default materials
	 */
	public final static Material DEFAULT_METACYCLER_MATERIAL = Material.STICK;
	public final static Material DEFAULT_BIOMECYCLER_MATERIAL = Material.BLAZE_ROD;

	/**
	 * Files
	 */
	public final static String DATA_FILE_NAME = "data.yml";

	/**
	 * Commands
	 */
	public final static String BIOMETOOL_COMMAND = "bt";
	public final static String METATOOL_COMMAND = "mt";
	public final static String PLAYERHEAD_COMMAND = "ph";

	/**
	 * 3rd party plugins
	 */
	public final static String WORLDGUARD_PLUGIN_NAME = "WorldGuard";
	public final static String PLOTSQUARED_PLUGIN_NAME = "PlotSquared";
}
