package edu.cmu.cs.cs214.hw4.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import edu.cmu.cs.cs214.hw4.core.Game;
import edu.cmu.cs.cs214.hw4.interfaces.*;
import edu.cmu.cs.cs214.hw4.model.*;

/**
 * This class creates the main game window for players. Each player has his/her
 * own GameFrame. This is created and called by the {@link #GameFrame()}
 * 
 * @author ziw
 * 
 */
public class GameFrame extends JFrame {

	//to prevent warning from eclipse
	private static final long serialVersionUID = -2597175993820066663L;
	//owner of this Frame
	private String owner;
	private MainFrame parent;
	private Game game;
	
	
	//a list of locations with letter tiles that the player placed on the game board
	private List<Location> toBePlayed;	
	//a location with a special tile that the player placed on the game board
	private Location toBePlayedSpeical;
	private JButton boardButtonBackup;
	
	private int chosenIndex = -1;//the index of letter tile chosen
	private int specialChosenIndex = -1;//the index of special tile chosen
	private static final int TOTAL_LETTER_TILES = StandardPlayer.MAX_LETTER_TILES;
	
	//A bunch of colors
	private static final Color CHOSEN_TILE_COLOR = new Color(170,250,150);
	private static final Color EMPTY_SQUARE_COLOR = new Color(240,220,180);
	private static final Color DL_COLOR = new Color(140,230,250);
	private static final Color DW_COLOR = new Color(255,150,150);
	private static final Color TL_COLOR = new Color(100,190,250);
	private static final Color TW_COLOR = new Color(255,150,110);
	private static final Color TILE_COLOR = Color.WHITE;
	
	//All special tiles.
	private static final List<SpecialTile> SPECIAL_TILES = Collections.unmodifiableList(
				Arrays.asList(new SpecialTile[]{
						new CleansingTile(), new MaxTile(),new MinTile(),new NegativePointsTile()
				})
	);
	
	//3 main panels
	private JPanel statusPanel;
	private JPanel gamePanel;
	private JPanel controlPanel;
	
	//status panel components
	private JLabel playerStatus;
	private JLabel gameStatus;
	
	//game panel components
	private JButton[][] gameBoard;
	
	//control panel components
	private JButton[] letterButtons;
	private JButton[] controlButtons;
	private JButton[] specialButtons;
	private JButton shuffleButton;
	private JButton playButton;
	private JButton passButton;
	private JButton recallButton;
	private JButton swapButton;
	
	/**
	 * Create a new frame for the given owner.
	 * @param playerName The name of the owner.
	 * @param g The game being played.
	 * @param main The parent MainFrame that creates this frame.
	 */ 
	public GameFrame(String playerName, Game g, MainFrame main) {
		owner = playerName;
		parent = main;
		game = g;
		toBePlayed = new ArrayList<Location>();
		initUI();
	}

	// suppress default constructor.
	@SuppressWarnings("unused")
	private GameFrame() {
	}

	private void initUI() {
		setLayout(new BorderLayout());
		statusPanel = createStatusPanel();
		gamePanel = createGamePanel();
		controlPanel = createControlPanel(game.getPlayerCopy(owner).getLetterTiles());
		
		getContentPane().add(statusPanel, BorderLayout.NORTH);
		getContentPane().add(gamePanel, BorderLayout.CENTER);
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		setTitle(owner+"'s Scrabble Board");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	private JPanel createGamePanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(game.getWidth(),game.getHeight()));
		gameBoard = new JButton[game.getWidth()][game.getHeight()];
		Board b = game.getGameBoard();
		Set<Map.Entry<Location, Integer>> entrySet = b.getBonusSquares().entrySet();
		for(int i=0;i<game.getWidth();i++){
			for(int j=0;j<game.getHeight();j++){
				
				JButton button = null;
				Tile t = game.getGameBoard().getTile(new Location(i,j));
				if(t == null){
					button = createGameBoardButton(t, entrySet, i, j);
					SpecialTile st = b.getSpecialTile(new Location(i,j));
					if(st != null){
						button.setText(st.getName());
					}
					final int row = i;
					final int col = j;
					//only add action listener if this location is still empty
					button.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							if(chosenIndex == -1 && specialChosenIndex == -1) return;
							//add a letter tile
							if(chosenIndex != -1){
								//already a pending tile on this block
								for(Location loc : toBePlayed){
									if(loc.getX() == row && 
											loc.getY() == col){
										return;
									}
									if(toBePlayedSpeical != null &&
											toBePlayedSpeical.getX()==row &&
											toBePlayedSpeical.getY()==col){
										return;
									}
									
								}
								JButton button = letterButtons[chosenIndex];
								resetButtonLook(button);
								button.setEnabled(false);//already played. no longer usable
								String text = button.getText();
								String letter = text.substring(0,1);//get the letter
								String last = text.substring(text.length()-1);//get the points
								((JButton)e.getSource()).setText(letter);
								toBePlayed.add(new Location(row,col,new LetterTile(letter, Integer.parseInt(last))));
								
								setLookAndFeel((JButton)e.getSource(), CHOSEN_TILE_COLOR);
								chosenIndex = -1;
								specialChosenIndex = -1;
							}
							else if(specialChosenIndex != -1){
								JButton button = specialButtons[specialChosenIndex];
								for(Location loc : toBePlayed){
									if(loc.getX() == row && 
											loc.getY() == col){
										return;
									}
									
								}
								if(toBePlayedSpeical != null){
									gameBoard[toBePlayedSpeical.getX()][toBePlayedSpeical.getY()].setText(boardButtonBackup.getText());
									gameBoard[toBePlayedSpeical.getX()][toBePlayedSpeical.getY()].setName(boardButtonBackup.getName());
									setLookAndFeel(gameBoard[toBePlayedSpeical.getX()][toBePlayedSpeical.getY()], boardButtonBackup.getBackground());
								}
								JButton backup = new JButton(((JButton)e.getSource()).getText());
								backup.setName(((JButton)e.getSource()).getName());
								setLookAndFeel(backup,((JButton)e.getSource()).getBackground());
								boardButtonBackup = backup;
								((JButton)e.getSource()).setText(button.getText());
								chosenIndex = -1;
								specialChosenIndex = -1;
								toBePlayedSpeical = new Location(row, col, SpecialTile.create(button.getText()));
								resetButtonLook(button);
								setLookAndFeel(((JButton)e.getSource()), CHOSEN_TILE_COLOR);
								SwingUtilities.getRoot( (JButton)e.getSource() ).repaint();
							}
							

						}
					});
				}
				else{
					button = new JButton(t.getName());
					button.setName(t.getName());
					button.setForeground(Color.BLACK);
					setLookAndFeel(button, TILE_COLOR);
				}
				button.setToolTipText(i+","+j +" : " + button.getName());
				panel.add(button);
				gameBoard[i][j] = button;
			}
		}
		return panel;
	}
	

	private JPanel createStatusPanel() {
		JPanel panel = new JPanel();
		playerStatus = new JLabel("Player: " + owner+" Score: " + game.getPlayerCopy(owner).getPoints());
		gameStatus = new JLabel("Current player: " + game.getCurrentPlayer().getName());
		panel.add(playerStatus);
		panel.add(gameStatus);
		return panel;
	}

	private JPanel createControlPanel(List<Tile> letterTiles) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,8));
		panel.add(new JLabel("Your letter tiles: "));

		letterButtons = new JButton[TOTAL_LETTER_TILES];
		chosenIndex = -1;
		for(int i=0;i<TOTAL_LETTER_TILES;i++){
			JButton newButton;
			if(i >= letterTiles.size()){
				newButton = getButtonPlaceHolder();
			}
			else{
				final LetterTile tile = (LetterTile)(letterTiles.get(i));
				if(tile == null){
					newButton= new JButton("");
					newButton.setEnabled(false);
				}
				else{
					newButton = new JButton(tile.getName() + " - " + tile.getPoints());
					newButton.setEnabled(isCurrentPlayer());
					letterButtons[i] = newButton;
					final int index=  i;
					if(isCurrentPlayer()){
						newButton.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								if(specialChosenIndex!= -1){
									resetButtonLook(specialButtons[specialChosenIndex]);
									specialChosenIndex = -1;
								}
								if(chosenIndex != -1){
									JButton prevChosen = letterButtons[chosenIndex];
									if(prevChosen != null){
										resetButtonLook(prevChosen);
									}
								}
								chosenIndex = index;
								setLookAndFeel((JButton)e.getSource(),CHOSEN_TILE_COLOR);
							}
						});
					}

					
				}
				
			}
			panel.add(newButton);
		}
		
		
		
		shuffleButton = new JButton("Shuffle Tiles");
		playButton = new JButton("Play Tiles");
		passButton = new JButton("Pass");
		recallButton = new JButton("Recall");
		swapButton = new JButton("Swap");
		
		
		/*-----Add action listeners for buttons------*/
		playButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isCurrentPlayer()){
					boolean valid = game.playMove(owner, toBePlayed, toBePlayedSpeical);
					if(valid){
						game.nextTurn(true);
						notifyUpdate();
					}
					else{
						JOptionPane.showMessageDialog( 
								SwingUtilities.getRoot( (JButton)e.getSource() ) ,
								"Invalid word.", "Invalid", JOptionPane.INFORMATION_MESSAGE);
						
					}
				}
				
			}
		});
		
		passButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isCurrentPlayer()){
					recallPlayedTiles();
					game.nextTurn(false);
					notifyUpdate();
				}
			
			}
		});
		
		recallButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				recallPlayedTiles();
			}
		});
		
		shuffleButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				recallPlayedTiles();
				List<Tile> copy = new ArrayList<Tile>(game.getPlayerCopy(owner).getLetterTiles());
				Collections.shuffle(copy);
				updateControlPanel(copy);
				pack();
				
			}
		});
		
		swapButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				recallPlayedTiles();
				game.swapTiles(owner);
				game.nextTurn(false);
				notifyUpdate();
			}
		});
		
		playButton.setToolTipText("Play the word.");
		shuffleButton.setToolTipText("Shuffle your tiles.");
		passButton.setToolTipText("Pass this round.");
		recallButton.setToolTipText("Recall all tiles placed on board.");
		swapButton.setToolTipText("Pass this round and get a new set of tiles.");
		
		controlButtons = new JButton[]{
				shuffleButton, playButton, passButton, recallButton, swapButton
		};

		
		
		panel.add(new JLabel("Operations: "));
		panel.add(playButton);
		panel.add(shuffleButton);
		panel.add(passButton);
		panel.add(recallButton);
		panel.add(swapButton);
		panel.add(getLabelPlaceHolder());
		panel.add(getLabelPlaceHolder());

		panel.add(new JLabel("Special Tiles: "));
		specialButtons = new JButton[SPECIAL_TILES.size()];
		for(int i=0; i<SPECIAL_TILES.size();i++){
			final SpecialTile tile = SPECIAL_TILES.get(i);
			final int index = i;
			JButton button = new JButton(tile.getName());
			button.setToolTipText(getSpecialButtonTooltip(tile));
			button.setEnabled( game.getPlayerCopy(owner).getPoints() >= tile.getPrice()
					&& isCurrentPlayer());
			
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					specialTileClicked(index);
				}
			});
			
			specialButtons[i] = button;
			panel.add(button);
		}
		for(JButton button : controlButtons){
			button.setEnabled(isCurrentPlayer());
		}

		return panel;
	}
	
	private String getSpecialButtonTooltip(SpecialTile t){
		StringBuffer sb = new StringBuffer("<html>");
		sb.append(t.getDescription());
		sb.append("<br>");
		sb.append("Cost: " + t.getPrice()+" points.<br>");
		sb.append( game.getPlayerCopy(owner).getPoints()>= t.getPrice()? 
				"":"You don't have enough points to play this tile.");
		sb.append("</html>");
		return sb.toString();
	}
	
	private JButton getButtonPlaceHolder(){
		JButton b = new JButton("");
		b.setEnabled(false);
		return b;
	}
	
	private JLabel getLabelPlaceHolder(){
		return new JLabel ("");
	}
	
	private void specialTileClicked(int index) {
		if(specialChosenIndex != -1){
			resetButtonLook(specialButtons[specialChosenIndex]);
		}
		if(chosenIndex != -1){
			resetButtonLook(letterButtons[chosenIndex]);
			chosenIndex = -1;
		}
		JButton button = specialButtons[index];
		specialChosenIndex = index;
		setLookAndFeel(button, CHOSEN_TILE_COLOR);
	}

	private boolean isCurrentPlayer(){
		return owner.equals(game.getCurrentPlayer().getName());
	}
	
	private void recallPlayedTiles(){
		for(JButton button : letterButtons){
			button.setBackground(null);
			button.setEnabled(isCurrentPlayer());
		}

		toBePlayed.clear();
		toBePlayedSpeical = null;
		chosenIndex = -1;
		specialChosenIndex = -1;
		
		updateGamePanel();
		pack();
	}

	
	private JButton createGameBoardButton(Tile t, Set<Map.Entry<Location, Integer>> bonusSquares, int i, int j){
		
		JButton button;
		String label = "";
		Color c =EMPTY_SQUARE_COLOR;
		for(Map.Entry<Location, Integer> e : bonusSquares){
			Location loc = e.getKey();
			if(loc.getX()== i && loc.getY() == j){
				int bonus = e.getValue();
				switch(bonus){
				case StandardGameBoard.DL:
					label = "DL";
					c = DL_COLOR;
					break;
				case StandardGameBoard.DW:
					label = "DW";
					c = DW_COLOR;
					break;
				case StandardGameBoard.TL:
					label = "TL";
					c = TL_COLOR;
					break;
				case StandardGameBoard.TW:
					label = "TW";
					c = TW_COLOR;
					break;
				default:
					label = "";
					c = EMPTY_SQUARE_COLOR;
					break;
				}
			}
		}
		button = new JButton(label);
		button.setName(label);
		setLookAndFeel(button, c);
		return button;
	}
	
	private void notifyUpdate(){
		parent.updateAllFrames();
	}
	
	private void updateStatusPanel(){
		gameStatus.setText("Current player: " + game.getCurrentPlayer().getName());
		playerStatus.setText("Player: " + owner+" Score: " + game.getPlayerCopy(owner).getPoints());
	}
	
	private void updateGamePanel(){
		getContentPane().remove(gamePanel);
		gamePanel = createGamePanel();
		getContentPane().add(gamePanel,BorderLayout.CENTER);
	}
	
	private void updateControlPanel(List<Tile> letterTiles){
		getContentPane().remove(controlPanel);
		controlPanel = createControlPanel(letterTiles);
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
	}
	
	private void setLookAndFeel(JButton b, Color c){
		b.setBackground(c);
		b.setOpaque(true);
		b.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
	}
	
	private void resetButtonLook(JButton button){
		setLookAndFeel(button, null);
		button.setBorder(UIManager.getBorder("Button.border"));
	}


	/**
	 * The only public method of the game frame. Called by its parent 
	 * MainFrame to update the UI to reflect the most current game/player status.
	 */
	public void update(Game g){
		this.game = g;
		chosenIndex = -1;
		specialChosenIndex= -1;
		toBePlayed.clear();
		toBePlayedSpeical = null;
		boardButtonBackup = null;
		updateStatusPanel();
		updateGamePanel();
		updateControlPanel(game.getPlayerCopy(owner).getLetterTiles());
		pack();
	}
	
}

