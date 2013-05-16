package edu.cmu.cs.cs214.hw4.model;

/**
 * One special tile. Once triggered, the word played no longer
 * benefits from bonus squares on the game board.
 * @author ziw
 *
 */
public class CleansingTile extends SpecialTile {

	public CleansingTile(){};
	
	@Override
	public String getName() {
		return CLEANSING_TILE;
	}

	public SpecialTile getInstance() {
		return new CleansingTile();
	}

	@Override
	public String getDescription() {
		return "Once triggered, the word played no longer benefits from bonus squares on the game board.";
	}

	@Override
	public int getPrice() {
		return 10;
	}

}
