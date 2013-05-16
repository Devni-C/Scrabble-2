package edu.cmu.cs.cs214.hw4.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.cmu.cs.cs214.hw4.interfaces.Player;
import edu.cmu.cs.cs214.hw4.interfaces.Tile;

/**
 * A standard Scrabble player with a maximum of 7 letter tiles.
 * @author ziw
 *
 */
public class StandardPlayer implements Player {
	
	private String name;
	private int points;
	private List<Tile> letters;
	private List<Tile> specials;
	public static final int MAX_LETTER_TILES = 7;
	
	/**
	 * Create a new player with the given name;
	 * @param name
	 */
	public StandardPlayer (String name){
		if(name == null)
			throw new NullPointerException("Player's name can't be null");
		this.name = name.trim();
		this.points =0;
		letters = new ArrayList<Tile>();
		specials = new ArrayList<Tile>();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPoints() {
		return points;
	}

	@Override
	public List<Tile> getLetterTiles() {
		return letters;
	}
	
	@Override
	public void setLetterTiles(List<Tile> tiles) {
		letters = tiles;
	}

	@Override
	public List<Tile> getSpecialTiles() {
		return specials;
	}
	
	@Override
	public void setSpecialTiles(List<Tile> tiles) {
		specials = tiles;
	}


	@Override
	public void addLetterTile(Tile lt) {
		if(lt == null)
			throw new NullPointerException("Can't add null tile.");
		if(letters.size()>=MAX_LETTER_TILES)
			return;
		if(!(lt instanceof LetterTile))
			return;
		letters.add(lt);
	}
	

	@Override
	public void addSpecialTile(Tile t) {
		if(t == null)
			throw new NullPointerException("Can't add null special tile");
		if(!(t instanceof SpecialTile))
			return;
		specials.add(t);
		
	}

	@Override
	public int getNumLetterTiles() {
		return letters.size();
	}

	@Override
	public boolean equals(Object other){
		return other!= null && other instanceof Player
				&& ((Player)other).getName().equals(name);
	}

	@Override
	public void setPoints(int points) {
		this.points = points;
	}

	@Override
	public boolean removeLetterTiles(List<Location> tilesPlayed) {
		if(tilesPlayed == null) return true;
		List<Tile> removedTiles = new ArrayList<Tile>();
		for(Location loc : tilesPlayed){
			Tile t = loc.getTile();
			if(t==null){
				throw new NullPointerException("Can't remove null tile from player");
			}
			Iterator<Tile> it = letters.iterator();
			boolean removed = false;
			while(it.hasNext()){
				Tile next = it.next();
				if(next.getName().equals(t.getName())){
					it.remove();
					removedTiles.add(next);
					removed = true;
					break;
				}
			}
			if(!removed){
				letters.addAll(removedTiles);
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean removeSpecialTile(Tile tile) {
		if(tile == null) return true;
		Iterator<Tile> it = specials.iterator();
		while(it.hasNext()){
			Tile next = it.next();
			if(next.getName().equals(tile.getName())){
				it.remove();
				return true;
			}
		}
		return false;
		
	}

	
	@Override
	public int hashCode(){
		return name.hashCode();
	}
	
}
