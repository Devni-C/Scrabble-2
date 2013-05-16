package edu.cmu.cs.cs214.hw4.model;

import edu.cmu.cs.cs214.hw4.interfaces.Tile;

/**
 * An abstract class representing all special tiles
 * @author ziw
 *
 */
public abstract class SpecialTile implements Tile {

	protected static final String MIN_TILE = "Min Tile";
	protected static final String MAX_TILE = "Max Tile";
	protected static final String CLEANSING_TILE = "Cleansing Tile";
	protected static final String NEGATIVE_TILE = "Negative Tile";
	
	/**
	 * Get the name of this special tile.
	 * @return
	 */
	public abstract String getName();

	/**
	 * Get the description of this special tile.
	 * @return
	 */
	public abstract String getDescription();
	
	/**
	 * Get the price of this special tile.
	 * @return
	 */
	public abstract int getPrice();
	
	@Override
	public String toString(){
		return getName() == null? "null" : getName();
	}

	public static Tile create(String text) {
		if(text == null) return null;
		if(text.equalsIgnoreCase(MAX_TILE)){
			return new MaxTile();
		}
		if(text.equalsIgnoreCase(MIN_TILE)){
			return new MinTile();
		}
		if(text.equalsIgnoreCase(CLEANSING_TILE)){
			return new CleansingTile();
		}
		if(text.equalsIgnoreCase(NEGATIVE_TILE)){
			return new NegativePointsTile();
		}
		return null;
	}
	
	

}
