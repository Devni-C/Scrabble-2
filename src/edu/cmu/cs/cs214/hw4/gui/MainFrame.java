package edu.cmu.cs.cs214.hw4.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


import edu.cmu.cs.cs214.hw4.interfaces.*;
import edu.cmu.cs.cs214.hw4.core.*;


/**
 * The main frame that is responsible for populating menu frame and game frames.
 * @author ziw
 *
 */
public class MainFrame extends JFrame{

	
	private static final long serialVersionUID = 7566439078042611902L;
	private Game game;
	private Map<Player, GameFrame> allGameFrames;
	
	private MainFrame(){};
	private MainFrame(MenuPanel panel){
	       setTitle("Scrabble Game");
	       setSize(panel.getWidth(),panel.getHeight());
	       setLocationRelativeTo(null);
	       setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args){
		showMenu();//start the game by showing the menu
	}
	
	/**
	 * Show the menu frame. This is the entry point of this game. 
	 */
	public static void showMenu(){
		 SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	MenuPanel menu = new MenuPanel();
	            	MainFrame gameFrame = new MainFrame(menu);
	            	gameFrame.getContentPane().add(menu);
	            	gameFrame.pack();
	            	gameFrame.setVisible(true);
	            }
        });
	}
	
	/**
	 * Start the game with the given list of players. Create new 
	 * game frames for each player in the game.
	 * @param players
	 * @param menu
	 */
	public void startGame(List<Player> players, MenuPanel menu){
		this.allGameFrames = new HashMap<Player,GameFrame>();
		getContentPane().remove(menu);
		setVisible(false); 
		dispose(); 
		
		if(players == null || players.size() <2){
			throw new IllegalArgumentException("Invalid players number. Can't start the game.");
		}
		try {
			game = new Game(players);
			for(int i=0; i< players.size();i++){
				Player p = players.get(i);
				GameFrame frame = new GameFrame(p.getName(),game,this);
				frame.setLocation(i*200, 200);
				allGameFrames.put(p, frame);
			}
			//bring the first player's window to the front
			allGameFrames.get(players.get(0)).toFront();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update all game frames to reflect the most current 
	 * game/player/board status.
	 */
	public void updateAllFrames() {
		if(game.isGameOver()){
			notifyGameOver();
			return;
		}
		GameFrame toFront = null;
		 for(Map.Entry<Player, GameFrame> e : allGameFrames.entrySet()){
			 GameFrame frame = e.getValue();
			 if(e.getKey().getName().equals(game.getCurrentPlayer().getName())){
				 toFront = frame;
			 }
			 frame.update(game);
		 }
		 toFront.toFront();
	}

	//notify all game frames that game is over.
	private void notifyGameOver(){
		List<Player> winners = game.getWinner();
		StringBuffer sb = new StringBuffer();
		sb.append("Game over! ");
		sb.append(winners.size()>1? "The winners are " : "The winner is ");
		for(Player p : winners){
			sb.append(p.getName()+" ");
		}
        JOptionPane.showMessageDialog(null, sb.toString(), "Game Over!", JOptionPane.INFORMATION_MESSAGE);

		for(Map.Entry<Player, GameFrame> e : allGameFrames.entrySet()){
			 GameFrame frame = e.getValue();
			 frame.setVisible(false);
			 frame.dispose();
		}
		showMenu();
	}

}
