package edu.cmu.cs.cs214.hw4.model;

/**
 * One special tile. Once triggered, the score of the word
 * is calculated as if all letters are worth x points, x being 
 * the minimum points of all letters that make up the word.
 * @author ziw
 *
 */
public class MinTile extends SpecialTile {
	
	public MinTile(){};
	
	@Override
	public String getName() {
		return MIN_TILE;
	}

	public SpecialTile getInstance() {
		return new MinTile();
	}

	@Override
	public String getDescription() {
		return "Once triggered, the score of the word is calculated as if all letters are worth x points," +
				" x being the minimum points of all letters that make up the word";
	}

	@Override
	public int getPrice() {
		return 15;
	}

}
