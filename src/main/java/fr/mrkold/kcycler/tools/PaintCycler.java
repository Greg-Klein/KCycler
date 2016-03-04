package fr.mrkold.kcycler.tools;

import org.bukkit.Art;
import org.bukkit.entity.Painting;

@SuppressWarnings("deprecation")
public class PaintCycler {

	/**
	 * When player right click on a painting it set the metadata to next one
	 * 
	 * @param ent
	 */
	public void rClick(Painting painting) {
		int paintingID = painting.getArt().getId();
		int pID = ++paintingID;
		if (pID > 25) {
			pID = 0;
		}
		painting.setArt(Art.getById(pID));
	}

	/**
	 * When player left click on a painting it set the metadata to previous one
	 * 
	 * @param painting
	 */
	public void lClick(Painting painting) {
		int paintingID = painting.getArt().getId();
		int pID = --paintingID;
		if (pID < 0) {
			pID = 25;
		}
		painting.setArt(Art.getById(pID));
	}
}
