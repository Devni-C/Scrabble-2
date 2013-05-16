package edu.cmu.cs.cs214.hw4.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cmu.cs.cs214.hw4.interfaces.*;
import edu.cmu.cs.cs214.hw4.model.LetterTile;
import edu.cmu.cs.cs214.hw4.model.Location;
import edu.cmu.cs.cs214.hw4.model.SpecialTile;
import edu.cmu.cs.cs214.hw4.model.StandardGameBoard;
import edu.cmu.cs.cs214.hw4.model.StandardPlayer;
import edu.cmu.cs.cs214.hw4.util.Dictionary;

/**
 * Represents one instance of a Scrabble game.
 * @author ziw
 *
 */
public class Game {
	private Board gameBoard;
	private List<Player> allPlayers;
	private List<Tile> allLetterTiles;
	
	
	//the index of the player who is making the current move.
	private int currTurn;
	//the dictionary used in this game
	private Dictionary d;
	//number of rounds in this game
	private int passedTurn;

	
	/**
	 * Create a new game with the given players.
	 * @param players
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Game(List<Player> players) throws FileNotFoundException, IOException{
		if(players == null){
			throw new NullPointerException("Players can't be null");
		}
		if(players.size()<2){
			throw new IllegalArgumentException("The game needs at least 2 players.");
		}
		allLetterTiles = new ArrayList<Tile>();
		allPlayers = new ArrayList<Player>(players);
		gameBoard = new StandardGameBoard();
		currTurn = 0;
		passedTurn = 0;
		d= new Dictionary();
		//init letter tiles
		for(String letter : d.getLetterPool()){
			allLetterTiles.add(new LetterTile(letter.toUpperCase(), d.getPoints(letter)));
		}
		Collections.shuffle(allLetterTiles);
		for(Player p : allPlayers){
			fillUpLetterTile(p);
		}
	}
	
	public Board getGameBoard(){
		return gameBoard;
	}

	/**
	 * Get all players.
	 * @return
	 */
	public List<Player> getPlayers(){
		return new ArrayList<Player>(allPlayers);
	}
	
	/**
	 * Get the player who is making the current turn.
	 * @return
	 */
	public Player getCurrentPlayer(){
		return allPlayers.get(currTurn);
	}
	
	/**
	 * Advance to the next turn.
	 */
	public  void nextTurn(boolean playedNewWord){
		currTurn++;
		if(currTurn>=allPlayers.size()){
			currTurn=0;
		}
		if(!playedNewWord){
			passedTurn++;
		}
	}
	
	
	/**
	 * Try to play a word. Return true if the player is the current
	 * player and the word is valid.
	 * @param p the player making to move 
	 * @param wordPlayed the word played
	 * @return
	 */
	public boolean playMove(String name, List<Location> wordPlayed, Location specialLoc){
		Player p = getPlayer(name);
		if(p== null ||!p.equals(getCurrentPlayer())){
			return false;//not your turn!
		}
		
		if(!gameBoard.validMove(wordPlayed, d)){
			return false;//invalid location and/or vocabulary
		}
		if(!p.removeLetterTiles(wordPlayed)){
			return false;//player doesn't have these tiles
		}
		
		if(specialLoc!=null &&( !gameBoard.isValidLocation(specialLoc) ||
				gameBoard.hasTile(specialLoc) ||
				specialLoc.getTile() == null ||
				!(specialLoc.getTile() instanceof SpecialTile) ||
				p.getPoints() < ((SpecialTile)specialLoc.getTile()).getPrice())){
			return false;
		}

		//calculate score
		Set<List<Location>> allWords = new HashSet<List<Location>>();
		allWords.add(gameBoard.completeWord(wordPlayed));
		allWords.addAll(gameBoard.getSideWords(wordPlayed));
		int score = 0;
		for(List<Location> word : allWords){
			score += gameBoard.evaluate(word, d);
		}
		p.setPoints(p.getPoints() + score);
		
		fillUpLetterTile(p);
		//deduct special tile cost
		p.setPoints(p.getPoints() - (specialLoc==null? 0 : ((SpecialTile)specialLoc.getTile()).getPrice()));
		//play the word on the game board
		gameBoard.setTile(wordPlayed);
		gameBoard.setSpecialTile(specialLoc);
		passedTurn = 0;
		return true;
		
	}
	
	/**
	 * Try to place a special tile on the given location.
	 * @param p the player who plays the specialtile.
	 * @param loc the location and the tile to play.
	 * @return
	 */
	public boolean playSpecialTile(String name, Location loc){
		Player p = getPlayer(name);
		if(!p.equals(getCurrentPlayer())){
			return false;//not your turn!
		}
		if(!gameBoard.isValidLocation(loc) ||
				gameBoard.hasTile(loc) ||
				loc.getTile() == null ||
				!(loc.getTile() instanceof SpecialTile)){
			return false;
		}
		
		if(p.removeSpecialTile(loc.getTile())){
			gameBoard.setSpecialTile(loc);
			return true;
		}
		return false;
		
	}
	
	/**
	 * Try to purchase a special tile for the given player.
	 * Return <tt>true</tt> if the player has enough score and 
	 * the purchase is successful.
	 * @param p the player who purchases the tile.
	 * @param t the tile to be purchased.
	 */
	public boolean purchaseSpecialTile(String name, Tile t){
		Player p = getPlayer(name);
		if(p== null ||!p.equals(getCurrentPlayer())){
			return false;//not your turn;
		}
		if(t==null || !(t instanceof SpecialTile)){
			return false;
		}
		SpecialTile st = (SpecialTile) t;
		if(p.getPoints() < st.getPrice())
			return false;
		p.setPoints(p.getPoints() - st.getPrice());
		p.addSpecialTile(t);
		return true;
	}
	
	private void fillUpLetterTile(Player p){
		Collections.shuffle(allLetterTiles);
		//fill up tiles used in this move
		int tilesToFill =  StandardPlayer.MAX_LETTER_TILES - p.getNumLetterTiles();
		Iterator<Tile> it = allLetterTiles.iterator();
		//remove the tile from the bag and add to player's list.
		for(int i=0;i<tilesToFill;i++){
			if(it.hasNext()){
				Tile t = it.next();
				it.remove();
				p.addLetterTile(t);
			}
		}
	}
	
	
	private Player getPlayer(String name){
		if(name == null) return null;
		for(Player p : allPlayers){
			if(p.getName().equals(name)){
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Return a copy of the player with the given name.
	 * @param name
	 * @return
	 */
	public Player getPlayerCopy(String name){
		if(name == null) return null;
		for(Player p : allPlayers){
			if(p.getName().equals(name)){
				Player copy =  new StandardPlayer(name);
				copy.setPoints(p.getPoints());
				copy.setLetterTiles(p.getLetterTiles());
				copy.setSpecialTiles(p.getSpecialTiles());
				return copy;
			}
		}
		return null;
	}
	
	
	
	public int getWidth(){
		return gameBoard.getWidth();
	}
	
	public int getHeight(){
		return gameBoard.getHeight();
	}

	public void swapTiles(String owner) {
		Player p = getPlayer(owner);
		allLetterTiles.addAll(p.getLetterTiles());
		p.setLetterTiles(new ArrayList<Tile>());
		Collections.shuffle(allLetterTiles);
		fillUpLetterTile(p);
		return;
	}
	

	/**
	 * Check if the game is over.<br>
	 * The game is over if 1) one player played all his/her letter
	 * tiles and there is no more tile in the tile bag, OR 2) for <tt>x</tt> consecutive turns, no player played a word.
	 * x = Math.max(6, number of players * 2)
	 * @return
	 */
	public boolean isGameOver(){
		for(Player p : allPlayers){
			if(p.getLetterTiles().size()==0
					&& allLetterTiles.size()==0){
				return true;
			}
		}
		return passedTurn >= Math.max((allPlayers.size()*2),6) ;
	}
	
	/**
	 * Get a list of players with the highest points.
	 * @return
	 */
	public List<Player> getWinner(){
		List<Player> winners = new ArrayList<Player>();
		winners.add(allPlayers.get(0));
		int max = allPlayers.get(0).getPoints();
		for(int i=1;i<allPlayers.size();i++){
			Player p  = allPlayers.get(i);
			if(p.getPoints()>max){
				winners.clear();
				winners.add(p);
				max = p.getPoints();
			}
			else if(p.getPoints()==max){
				winners.add(p);
			}
		}
		return winners;
	}
	
}
