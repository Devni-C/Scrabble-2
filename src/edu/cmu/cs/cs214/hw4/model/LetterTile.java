package edu.cmu.cs.cs214.hw4.model;

import edu.cmu.cs.cs214.hw4.interfaces.Tile;

/**
 * Tile with a letter. The normal tile used to play Scrabble.
 * @author ziw
 *
 */
public class LetterTile implements Tile {

	private String name;
	private int points;
	
	public LetterTile(String letter, int points) {
		this.name = letter;
		this.points = points;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public int getPoints() {
		return points;
	}
	
	@Override
	public String toString(){
		return getName() == null? "null" : getName();
	}

}
