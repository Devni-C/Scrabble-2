package edu.cmu.cs.cs214.hw4.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cmu.cs.cs214.hw4.interfaces.Board;
import edu.cmu.cs.cs214.hw4.interfaces.Tile;
import edu.cmu.cs.cs214.hw4.util.Dictionary;
import edu.cmu.cs.cs214.hw4.util.Util;

/**
 * A standard 15*15 Scrabble game board.
 * @author ziw
 *
 */
public class StandardGameBoard implements Board {

	private static final int WIDTH = 15;
	private static final int HEIGHT = 15;
	public static final int DW = 1;//double word
	public static final int TW = 2;//triple word
	public static final int DL = 3;//double letter
	public static final int TL = 4;//triple letter
	
	
	
	//storing letter tiles, special tiles and bonus squares.
	private Location[][] grids;
	private Map<Location,Integer> bonusSquares;
	private List<Location> specialTiles;
	private int numTiles;
	private static final Location CENTER = new Location(7,7);
	
	//Hard-coded bonus squares. This is in accordance to the
	//original/official scrabble board.
	private Location[] TWLocs = new Location[]{
			new Location(0,0),
			new Location(7,0),
			new Location(14,0),
			new Location(0,7),
			new Location(14,7),
			new Location(0,14),
			new Location(7,14),
			new Location(14,14)
	};	
	private Location[] TLLocs = new Location[]{
			new Location(5,1),
			new Location(9,1),
			new Location(1,5),
			new Location(5,5),
			new Location(9,5),
			new Location(13,5),
			new Location(1,9),
			new Location(5,9),
			new Location(9,9),
			new Location(13,9),
			new Location(5,13),
			new Location(9,13)
	};
	private Location[] DLLocs = new Location[]{
			new Location(3,0),
			new Location(11,0),
			new Location(6,2),
			new Location(8,2),
			new Location(0,3),
			new Location(7,3),
			new Location(14,3),
			new Location(2,6),
			new Location(6,6),
			new Location(8,6),
			new Location(12,6),
			new Location(3,7),
			new Location(11,7),
			new Location(2,8),
			new Location(6,8),
			new Location(8,8),
			new Location(12,8),
			new Location(0,11),
			new Location(7,11),
			new Location(14,11),
			new Location(6,12),
			new Location(8,12),
			new Location(3,14),
			new Location(11,14),
	};

	/**
	 * Construct a new, empty game board. 
	 */
	public StandardGameBoard(){
		//initialize bonus squares
		bonusSquares = new HashMap<Location,Integer>();
		for(Location loc : Arrays.asList(TWLocs)){
			bonusSquares.put(loc, TW);
		}
		for(Location loc : Arrays.asList(TLLocs)){
			bonusSquares.put(loc, TL);
		}
		for(Location loc : Arrays.asList(DLLocs)){
			bonusSquares.put(loc,DL);
		}
		for(int i=0;i<WIDTH;i++){
			for(int j=0;j<HEIGHT;j++){
				if( (i==j || i+j==14 ) && i!=0 && i!=7 && i!=14
						&& (i!= 5) && (i!=9) && i!=6 && i!=8){
					bonusSquares.put(new Location(i,j), DW);
				}
			}
		}
		
		//initialize special tiles
		specialTiles = new ArrayList<Location>();
		
		//initialize grids
		grids = new Location[WIDTH][HEIGHT];
		for(int i=0;i<WIDTH;i++){
			for(int j=0;j<HEIGHT;j++){
				grids[i][j] = new Location(i,j);
			}
		}
		numTiles = 0;
	}
	
	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public Tile getTile(Location loc) {
		return isValidLocation(loc)? getBoardLocation(loc).getTile() : null;
	}
	
	private boolean isValidLocation(int x, int y){
		return x>=0 && y>=0 && x<WIDTH && y< HEIGHT;
	}


	@Override
	public boolean isValidLocation(Location loc) {
		return loc!=null && isValidLocation(loc.getX(), loc.getY());
	}
	
	private boolean isValidLocation(Collection<Location> allLocs){
		if(allLocs == null) return false;
		for(Location loc : allLocs){
			if(!isValidLocation(loc)) return false;
		}
		return true;
	}

	@Override
	public void setSpecialTile(Location loc) {
		if(!isValidLocation(loc)) return;
		Tile t = loc.getTile();
		if(t==null || !(t instanceof SpecialTile)){
			return;
		}
		specialTiles.add(loc);
	}
	
	@Override
	public void setTile(Location loc) {
		if(!isValidLocation(loc)){
			return;
		}
		getBoardLocation(loc).setTile(loc.getTile());
		numTiles++;
	}
	

	@Override
	public void setTile(Collection<Location> locs) {
		for(Location loc : locs)
			setTile(loc);
	}
	
	//given a location, return the Location stored in the grids[][] on this board
	private Location getBoardLocation(Location loc){
		if(!isValidLocation(loc))
			return null;
		return grids[loc.getX()][loc.getY()];
	}
	
	@Override
	public boolean hasTile(Location loc){
		return isValidLocation(loc) && getBoardLocation(loc).isOccupied();
	}

	@Override
	public boolean validMove(Collection<Location> wordPlayed, Dictionary d) {
		if(wordPlayed == null) return false;
		//first check size and valid location
		if(wordPlayed.size()==0 || !isValidLocation(wordPlayed))
			return false;
		
		
		//check to make sure there is no existing tiles on the any 
		//location given in the collection
		for(Location loc : wordPlayed){
			if(hasTile(loc)){
				return false;
			}
		}
		//create a hard copy and sort the list
		List<Location> sortedWord = new ArrayList<Location>(wordPlayed);
		Util.sortLocation(sortedWord);
		
		int direction = Util.getDirection(sortedWord);
		if(direction == Util.INVALID_DIR) return false;
		
		//first word must go through the center of the board.
		if(numTiles ==0){
			return Util.isContinuous(sortedWord) && validFirstMove(sortedWord)
					&& d.isWord(Util.extractWord(sortedWord));
		}
		
		//word sorted. look for adjacent/connecting tiles that are already on the board
		Location first = sortedWord.get(0);
		Location last = sortedWord.get(sortedWord.size()-1);
		
		//flag marking if the word connects to existing tiles from left/top
		boolean left = hasTile(direction == Util.HORIZONTAL? 
				new Location(first.getX()-1, first.getY()) : new Location(first.getX(), first.getY()-1));
		
		//flag marking if the word connects to existing tiles from right/bottom
		boolean right = hasTile(direction == Util.HORIZONTAL? 
				new Location(last.getX()+1, last.getY()) : new Location(last.getX(), last.getY()+1));
		//flag to mark if the locations of this word are continuous(no holes)
		boolean continuous = Util.isContinuous(sortedWord);
		//a set of words formed on the side of this way, if any
		Set<List<Location>> sideWords = getSideWords(sortedWord);

		//The word has no holes in it, and does not connect to any existing tile from 
		//either left/top or right/bottom. Invalid.
		if(continuous && !left && !right && sideWords.size()==0) return false;

		//get a new list of locations with both the word played
		//and all the connecting tiles that are already on board
		List<Location> completed = completeWord(sortedWord);
		
		Set<String> allWords = new HashSet<String>();
		
		//add both the main word and all possible side words
		if(completed.size()>1){
			allWords.add(Util.extractWord(completed));
		}
		for(List<Location> sideWord : sideWords){
			allWords.add(Util.extractWord(sideWord));
		}
		
		//the completed word and all side words should be continuous and in the dictionary.
		return Util.isContinuous(completed) && d.isWord(allWords);
		
	}
	
	private boolean validFirstMove(List<Location> wordPlayed){
		for(Location loc : wordPlayed){
			if(loc.getX() == CENTER.getX() &&
					loc.getY() == CENTER.getY()){
				return true;
			}
		}
		System.out.println("First word must go through the center of the board.");
		return false;
	}

	@Override
	public Set<List<Location>> getSideWords(List<Location> wordPlayed){
		Set<List<Location>> s = new HashSet<List<Location>>();
		if(wordPlayed == null) return s;
		int direction = Util.getDirection(wordPlayed);
		for(Location loc : wordPlayed){
			List<Location> l = new ArrayList<Location>();
			boolean left = false; 
			boolean right = false;
			for(int i=1;;i++){
				Location next = direction == Util.HORIZONTAL ?
						new Location(loc.getX(),loc.getY()-i) :
						new Location(loc.getX()-i,loc.getY());
				if(hasTile(next)){
					left = true;
					l.add(getBoardLocation(next));
				}
				else break;
			}
			
			for(int i=1;;i++){
				Location next = direction == Util.HORIZONTAL?
						new Location(loc.getX(), loc.getY()+i) :
						new Location(loc.getX()+i, loc.getY());
				if(hasTile(next)){
					right = true;
					l.add(getBoardLocation(next));
				}
				else break;
			}
			if(left || right) 
				l.add(loc);
			if(l.size()>0){
				Util.sortLocation(l);
				s.add(l);
			}
		}
		return s;
	}
	
	
	@Override
	public List<Location> completeWord(List<Location> sortedWord){
		List<Location> c = new ArrayList<Location>(sortedWord);
		
		Util.sortLocation(c);
		int direction = Util.getDirection(c);
		Location first = c.get(0);
		Location last = c.get(c.size()-1);
		
		//now check if the locations skipped by this word(holes) actually have tiles on them from previous moves
		List<Location> complement = Util.getListComplement(sortedWord);
		if(complement != null && complement.size()>0){
			for(Location loc : complement){
				if(hasTile(loc)){
					c.add(getBoardLocation(loc));
				}
			}
		}


		// first append existing tiles from the left/upper end
		for (int i = 1;; i++) {
			Location next = direction == Util.HORIZONTAL ? getBoardLocation(new Location(
					first.getX() - i, first.getY()))
					: getBoardLocation(new Location(first.getX(), first.getY()
							- i));
			if (isValidLocation(next) && hasTile(next)) {
				Tile t = next.getTile();
				c.add(new Location(next.getX(), next.getY(), t));
			} else
				break;

		}
		// append from existing tiles right/lower end
		for (int i = 1;; i++) {
			Location next = direction == Util.HORIZONTAL ? getBoardLocation(new Location(
					last.getX() + i, last.getY()))
					: getBoardLocation(new Location(last.getX(), last.getY()
							+ i));
			if (isValidLocation(next) && hasTile(next)) {
				Tile t = next.getTile();
				c.add(new Location(next.getX(), next.getY(), t));
			} else
				break;
		}
		Util.sortLocation(c);
		return c;
	}
	
	
	@Override
	public int evaluate(Collection<Location> wordPlayed, Dictionary d) {
		int wordMultiplier = 1;
		int total = 0;
		Tile special = null;
		for(Location loc : wordPlayed){
			//find and remove all special tiles this word covers.
			//if multiple sp. tiles are triggered, only the last one is effective.
			Tile temp = findAndRemoveSpecialTile(loc);
			if(temp != null){
				special = temp;
			}
		}
		int max = Util.getMaxLetterScore(wordPlayed, d);
		for(Location loc : wordPlayed){
			int letterScore = d.getPoints(loc.getTile().getName());
			int bonus = getBonusMultiplier(loc);
			if(special!=null){
				if(special instanceof CleansingTile){
					//no bonus squares if cleansing tile triggered
					bonus = -1;
				}
				else if(special instanceof MinTile){
					//min score of any word is 1
					letterScore = 1;
				}
				else if(special instanceof MaxTile){
					letterScore =  max;
				}
			}
			switch(bonus){
				case DW:
					wordMultiplier = 2;
					break;
				case TW:
					wordMultiplier = 3;
					break;
				case DL:
					letterScore*=2;
					break;
				case TL:
					letterScore*=3;
					break;
				default:break;
			}
			total += letterScore;
		}
		if(special !=null && special instanceof NegativePointsTile){
			wordMultiplier*= -1;//negative tile effect
		}
		return total*wordMultiplier;
	}
	

	private int getBonusMultiplier(Location loc){
		Location toRemove = null;
		for(Location bonus : bonusSquares.keySet()){
			if(loc.sameCoord(bonus)){
				toRemove = bonus;
				break;
			}
		}
		if(toRemove != null){
			int multiplier = bonusSquares.get(toRemove);
			bonusSquares.remove(toRemove);
			return multiplier;
		}
		return -1;
	}
	
	public Map<Location, Integer> getBonusSquares(){

		return new HashMap<Location, Integer>(bonusSquares);
	}
	
	private Tile findAndRemoveSpecialTile(Location loc){
		Iterator<Location> it = specialTiles.iterator();
		while(it.hasNext()){
			Location next = it.next();
			if(next.sameCoord(loc)){
				it.remove();
				return next.getTile();
			}
		}
		return null;
	}

	@Override
	public SpecialTile getSpecialTile(Location loc) {
		if(!isValidLocation(loc)) return null;
		for(Location l : specialTiles){
			if(l.sameCoord(loc)){
				return (SpecialTile) l.getTile();
			}
		}
		return null;
	}




	
	

}
