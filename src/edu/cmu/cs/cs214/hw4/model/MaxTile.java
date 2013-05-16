package edu.cmu.cs.cs214.hw4.model;

/**
 * One special tile. Once triggered, the score of the word
 * is calculated as if all letters are worth x points, x being 
 * the maximum points of all letters that make up the word.
 * @author ziw
 *
 */
public class MaxTile extends SpecialTile {

	public MaxTile(){};
	
	@Override
	public String getName() {
		return MAX_TILE;
	}

	public SpecialTile getInstance() {
		return new MaxTile();
	}

	@Override
	public String getDescription() {
		return "Once triggered, the score of the word is calculated as if all letters are worth x points, " +
				"x being the maximum points of all letters that make up the word.";
	}

	@Override
	public int getPrice() {
		return 15;
	}

}
