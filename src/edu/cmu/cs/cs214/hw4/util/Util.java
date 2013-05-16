package edu.cmu.cs.cs214.hw4.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.cmu.cs.cs214.hw4.interfaces.Tile;
import edu.cmu.cs.cs214.hw4.model.LetterTile;
import edu.cmu.cs.cs214.hw4.model.Location;

/**
 * Utility class that provides some useful functions for the game/board
 * to check validity of moves.
 * @author ziw
 *
 */
public class Util {
	/**
	 * Horizontal direction.
	 */
	public static final int HORIZONTAL = 1;
	
	/**
	 * Vertical direction.
	 */
	public static final int VERTICAL = 0;
	
	/**
	 * Invalid direction. Anything that is not horizontal or vertical. 
	 */
	public static final int INVALID_DIR = -1;

	/**
	 * Get the direction of the given list of locations. 
	 * @param wordPlayed
	 * @return {@link #HORIZONTAL}, {@link #VERTICAL}, or {@link #INVALID_DIR}
	 */
	public static int getDirection(List<Location> wordPlayed) {
		//treat all one letter word as horizontal
		if(wordPlayed.size() == 1) return HORIZONTAL;
		
		Location prevLoc = null;
		int direction = INVALID_DIR;
		for (Location loc : wordPlayed) {
			if (prevLoc != null) {
				if (direction == INVALID_DIR) {
					if (prevLoc.getX() == loc.getX()) {
						direction = VERTICAL;
					} else if (prevLoc.getY() == loc.getY()) {
						direction = HORIZONTAL;
					} else {
						// error
						return INVALID_DIR;
					}
				} else {
					if (direction == VERTICAL && prevLoc.getX() != loc.getX())
						return INVALID_DIR;
					if (direction == HORIZONTAL && prevLoc.getY() != loc.getY())
						return INVALID_DIR;
				}
			}
			prevLoc = loc;
		}
		return direction;
	}

	private static int compareLocation(Location l1, Location l2, int dir) {
		if (dir == HORIZONTAL) {
			return l1.getX() - l2.getX();
		} else if (dir == VERTICAL) {
			return l1.getY() - l2.getY();
		}
		throw new IllegalArgumentException("Invalid direction");
	}
	
	/**
	 * Sort the given location list according to its direction.
	 * If the location is not a straight line, nothing happens. 
	 * Otherwise, sort the location from left->right or top->bottom.
	 * @param wordPlayed
	 */
	public static void sortLocation(List<Location> wordPlayed){
		final int dir = getDirection(wordPlayed);
		if(dir == INVALID_DIR) return;
		Collections.sort(wordPlayed, new Comparator<Location>(){

			@Override
			public int compare(Location o1, Location o2) {
				return compareLocation(o1,o2, dir);
			}
			
		});
	}

	/**
	 * Check if the given list contains a continuous set of locations.
	 * Return true if the given locations form a straight line with no wholes.
	 * @param l
	 * @return
	 */
	public static boolean isContinuous(List<Location> l){
		List<Location> copy = new ArrayList<Location>(l);
		sortLocation(copy);
		int dir = getDirection(copy);
		if(dir == INVALID_DIR)
			return false;
		Location prevLoc = null;
		for(int i=0;i<copy.size();i++){
			Location curr = copy.get(i);
			if(prevLoc!=null){
				if(dir == HORIZONTAL && ((curr.getX()-prevLoc.getX()) != 1) ){
					return false;
				}
				else if(dir == VERTICAL && ((curr.getY() - prevLoc.getY())!=1)){
					return false;
				}
			}
			prevLoc = curr;
		}
		return true;
	}
	
	/**
	 * Get a list of Locations that is the minimum set of locations needed
	 * to make the given list continuous. i.e. return a list of locations that 
	 * would fill in the holes of the given list.
	 * @param wordPlayed
	 * @return
	 */
	public static List<Location> getListComplement(List<Location> wordPlayed){
		List<Location> l = new ArrayList<Location>();
		if(wordPlayed == null || isContinuous(wordPlayed)){
			return l;
		}
		List<Location> copy = new ArrayList<Location>(wordPlayed);
		sortLocation(copy);
		int dir = getDirection(copy);
		if(dir == INVALID_DIR)
			return l;
		Location prev=  null;
		for(int i=0;i<copy.size();i++){
			Location curr = copy.get(i);
			if(prev != null){
				if(dir == HORIZONTAL && (curr.getX() - prev.getX()) != 1 ){
					for(int j=prev.getX()+1; j<curr.getX();j++){
						l.add(new Location(j,curr.getY()));
					}
				}
				else if(dir == VERTICAL && (curr.getY() - prev.getY()) != 1){
					for(int k=prev.getY()+1;k<curr.getY();k++){
						l.add(new Location(curr.getX(),k));
					}
				}
				
			}
			prev = curr;
		}
		
		return l;
	}
	
	/**
	 * Return a string represented by the given locations
	 * and their associated tiles. The order runs from either left to right
	 * or top to bottom. return null for invalid (non-straight, non-continuous) locations. 
	 * @param wordPlayed
	 * @return
	 */
	public static String extractWord(List<Location> wordPlayed){
		if(wordPlayed == null ||
				getDirection(wordPlayed)==INVALID_DIR ||
				!isContinuous(wordPlayed)){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		sortLocation(wordPlayed);
		for(int i=0;i<wordPlayed.size();i++){
			Location loc = wordPlayed.get(i);
			Tile t = loc.getTile();
			if(t== null)
				throw new NullPointerException("Tile is null" + loc);
			if(t instanceof LetterTile){
				sb.append(t.getName());
			}
			else{
				throw new IllegalArgumentException("Tile is not a letter tile.");
			}
		}
		
		return sb.toString();
		
	}
	
	/**
	 * Get the maximum score of all the letters used in the word.
	 * @param wordPlayed
	 * @param d
	 * @return
	 */
	public static int getMaxLetterScore(Collection<Location> wordPlayed, Dictionary d){
		int max = -1;
		for(Location loc : wordPlayed){
			int score = d.getPoints(loc.getTile().getName());
			if(score>max){
				max = score;
			}
		}
		return max;
	}
}
