package edu.cmu.cs.cs214.hw4.interfaces;

import java.util.List;
import edu.cmu.cs.cs214.hw4.model.Location;

/**
 * Represents a player of the scrabble game.
 * @author ziw
 *
 */
public interface Player {
	
	/**
	 * Return the name of the player.
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the points of the player currently has.
	 * @return
	 */
	public int getPoints();
	
	/**
	 * Set the points of the player.
	 * @param points
	 */
	public void setPoints(int points);
	
	/**
	 * Get a list of letter tiles the player owns.
	 * @return
	 */
	public List<Tile> getLetterTiles();
	
	
	/**
	 * Set the player's letter tiles
	 * @param tiles
	 */
	public void setLetterTiles(List<Tile> tiles);
	
	/**
	 * Get a list of special tiles the player owns.
	 * @return
	 */
	public List<Tile> getSpecialTiles();
	
	/**
	 * Set the player's special tiles
	 * @param tiles
	 */
	public void setSpecialTiles(List<Tile> tiles);
	
	/**
	 * Add a letter tile to the player's letter tile list.
	 * @param lt
	 */
	public void addLetterTile(Tile lt);
		
	/**
	 * Add a special tile to the player's special tile list.
	 * @param t
	 */
	public void addSpecialTile(Tile t);
	
	/**
	 * Get the number of letter tiles the player owns.
	 * @return
	 */
	public int getNumLetterTiles();
	
	/**
	 * Try to find and remove all the tiles of the wordPlayed from
	 * the player's letter tile list. This simulates the player playing 
	 * a move. If the player does not have all the tiles required to make the 
	 * move, the method returns <tt>false</tt> and nothing happens to the tile collection.
	 * Otherwise, return <tt>true</tt> and remove all the tiles just used to play the word.
	 * @param wordPlayed
	 * @return
	 */
	public boolean removeLetterTiles(List<Location> wordPlayed);

	/**
	 * Try to find and remove the tile given from the player's special tile list.
	 * This simulates the player playing a special tile. The method returns true 
	 * if the player has the given tile and the tile is removed, false otherwise. 
	 * @param tile
	 * @return
	 */
	public boolean removeSpecialTile(Tile tile);


	
}
