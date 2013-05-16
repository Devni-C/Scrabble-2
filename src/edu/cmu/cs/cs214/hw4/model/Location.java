package edu.cmu.cs.cs214.hw4.model;

import edu.cmu.cs.cs214.hw4.interfaces.Tile;

/**
 * A class representing a location with <tt>(x,y)</tt> coordinates
 * and possibly a tile that occupies the location. 
 * 
 * @author ziw
 *
 */
public class Location {
	
	private int x,y;
	private Tile tile;
	
	/**
	 * Construct a location at x,y with no tile.
	 * @param x
	 * @param y
	 */
	public Location(int x, int y) {
		this(x,y,null);
	}
	
	/**
	 * Construct a location at x,y with the given tile.
	 * @param x
	 * @param y
	 * @param t
	 */
	public Location(int x, int y, Tile t){
		this.x = x;
		this.y = y;
		this.tile = t;
	}

	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public Tile getTile(){
		return tile;
	}
	
	public boolean isOccupied(){
		return tile!=null;
	}
	
	public void setTile(Tile t){
		tile = t;
	}
	
	/**
	 * Compare to another location. This only 
	 * checks the coordinates and ignore the tile.
	 * @param other
	 * @return
	 */
	public boolean sameCoord(Location other){
		return other!=null && other.x == x && other.y == y;
	}
	
	@Override
	public boolean equals(Object other){
		return other != null && (other instanceof Location)
				&& ((Location)other).x == x && ((Location)other).y == y;
	}
	
	@Override
	public String toString(){
		String s = "<"+x+","+y+">";
		if(tile == null) return s+" : null";
		return s + " : " + tile;
	}
	
	@Override
	public int hashCode(){
		return (31*x)+y;
	}
	
}
