package edu.cmu.cs.cs214.hw4.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.cmu.cs.cs214.hw4.interfaces.*;
import edu.cmu.cs.cs214.hw4.model.*;

/**
 * A JPanel for the game's starting screen.
 * @author ziw
 *
 */
public class MenuPanel extends JPanel{

	
	private static final long serialVersionUID = -4113991534447202981L;
	private JLabel nameLabel;
	private JTextField nameField;
	private JButton addButton;
	private JButton startButton;
	
	private List<Player> players;
	
	/**
	 * Create a new menu panel that prompts the user 
	 * to enter player's name to start a new Scrabble game.
	 */
	public MenuPanel(){
		players = new ArrayList<Player>();

		nameLabel = new JLabel("Name: ");
		nameField = new JTextField("",20);
		addButton = new JButton("Add Player");
		startButton = new JButton("Start Game");
		
		
		nameLabel.setLabelFor(nameField);
		
		updateAddButton();
		updateStartButton();
		
		nameField.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				update();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				update();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				update();
			}
			
			private void update(){
				updateAddButton();
				updateStartButton();
			}
		});
		
		addButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String name = nameField.getText();
				if(name == null || name.trim().length()==0 
						|| players.size() >= 4){
					return;
				}
				players.add(new StandardPlayer(name.trim()));
				nameField.setText("");
				updateAddButton();
				updateStartButton();
			}
			
		});
		
		
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});

		add(nameLabel);
		add(nameField);
		add(addButton);
		add(startButton);
		
	}
	
	private boolean isGameReady() {
		return players!=null && players.size()>=2 && players.size() <= 4;
	}
	
	private String getStartButtonTooltips(){
		StringBuffer sb = new StringBuffer();
		if(isGameReady()){
			sb.append( "Start the Scrabble Game with: ");
			for(Player p : players){
				sb.append(p.getName()+" ");
			}
		}
		else{
			sb.append("Invalid player number. Must have 2 - 4 players.");
			if(players == null || players.size()==0){
				sb.append("Currently there is no player in the game.");
			}
			else{
				sb.append("Currently there are: ");
				for(Player p : players){
					sb.append(p.getName());
				}
			}
		}

		return sb.toString();
		
	}
	
	private String getAddButtonTooltips(){
		if(players.size() >= 4){
			return "There are already 4 players. Can't add more players.";
		}
		if(nameField.getText().trim().length() ==0 ){
			return "Can't add player with empty name.";
		}
		if(players.contains(new StandardPlayer(nameField.getText().trim()))){
			return "A player with that name already exists.";
		}
		return "Add the player (" +nameField.getText().trim() + ") to the game.";
	}
	
	private void updateAddButton(){
		addButton.setEnabled(players.size() < 4 && nameField.getText().trim().length()>0
								&& !players.contains(new StandardPlayer(nameField.getText().trim())));
		addButton.setToolTipText(getAddButtonTooltips());
	}
	
	private void updateStartButton(){
		startButton.setEnabled(isGameReady());
		startButton.setToolTipText(getStartButtonTooltips());
	}

	private void startGame(){
		MainFrame gameFrame = (MainFrame) SwingUtilities.getRoot(this);
		gameFrame.startGame(players,this);
	}

	
}
