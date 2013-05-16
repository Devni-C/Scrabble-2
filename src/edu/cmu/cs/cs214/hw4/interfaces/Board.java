package edu.cmu.cs.cs214.hw4.interfaces;

import java.util.Collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cmu.cs.cs214.hw4.model.*;
import edu.cmu.cs.cs214.hw4.util.Dictionary;

/**
 * The game board of the Scrabble game.
 * @author ziw
 *
 */
public interface Board {
	
	/**
	 * Get the width of the board
	 * @return
	 */
	public int getWidth();
	
	/**
	 * Get the height of the board
	 * @return
	 */
	public int getHeight();
	
	/**
	 * Check if the given location is a valid location
	 * @param loc
	 * @return
	 */
	public boolean isValidLocation(Location loc);

	/**
	 * Get the tile that occupies the given location
	 * @param loc
	 * @return the tile on this location or <tt>null</tt> if no tile exists
	 */
	public Tile getTile(Location loc);
	
	/**
	 * Get the special tile that occupies this location.
	 * @param loc
	 * @return
	 */
	public SpecialTile getSpecialTile(Location loc);
	
	/**
	 * Set the special tile of the given location on board with loc's containing tile.
	 * @param loc
	 */
	public void setSpecialTile(Location loc);
	
	/**
	 * Set the tile of the given location on board with loc's containing tile.
	 * This simulates placing a tile on the board at the given location.
	 * @param loc the Location where the tile goes.
	 */
	public void setTile(Location loc);

	/**
	 * See {@link #setTile(Location)}
	 * @param locs
	 */
	public void setTile(Collection<Location> locs);
	
	/**
	 * Check if the given collection of locations is a valid move.
	 * It  checks if the locations are valid relative to the current state
	 * of this board (if they connect to existing tiles and all new tiles are on 
	 * one straight line). This also checks if all the new words resulted 
	 * from this collection of locations are valid words found in 
	 * the given dictionary.
	 * @param wordPlayed
	 * @return <tt>true</tt> only if all locations are valid 
	 * and all new words are valid words. 
	 */
	public boolean validMove(Collection<Location> wordPlayed, Dictionary d);
	
	/**
	 * Return the total points after the given wordPlayed is placed on the board. 
	 * This calculates any bonus squares and any triggered special tiles.
	 * @param wordPlayed the word just played on this board
	 * @param d the Dictionary containing letter score distribution
	 * @return
	 */
	public int evaluate(Collection<Location> wordPlayed, Dictionary d);
	
	/**
	 * For each tile in the given wordPlayed, return, if any, all other words formed
	 * in the direction orthogonal to its word direction. i.e. if given a 
	 * vertical word, return a set of horizontal words formed by one or many tiles
	 * of the given word.
	 * @param wordPlayed
	 * @return
	 */
	public Set<List<Location>> getSideWords(List<Location> wordPlayed);

	/**
	 * Check if the given location has a tile on it.
	 * @param loc
	 * @return
	 */
	public boolean hasTile(Location loc);

	/**
	 * Return a list of locations that contains the given played word and 
	 * any location on the game board with tile that connects to the given word.
	 * This new list contains any connecting tiles from the given word's left/top and/or
	 * right/bottom direction. 
	 * @param sortedWord
	 * @return
	 */
	public List<Location> completeWord(List<Location> sortedWord);
	
	/**
	 * Return a map of the location of all un-triggered bonus squares on the game board
	 * with the type of that bonus square. 
	 * @return
	 */
	public Map<Location, Integer> getBonusSquares();
}
