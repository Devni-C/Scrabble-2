package edu.cmu.cs.cs214.hw4.model;

/**
 * A special tile. Once triggered, the word's total score becomes
 * negative. 
 * @author ziw
 *
 */
public class NegativePointsTile extends SpecialTile {

	public NegativePointsTile (){};
	
	@Override
	public String getName() {
		return NEGATIVE_TILE;
	}

	public SpecialTile getInstance() {
		return new NegativePointsTile();
	}

	@Override
	public String getDescription() {
		return "Once triggered, the word's total score becomes negative.";
	}

	@Override
	public int getPrice() {
		return 20;
	}

}
