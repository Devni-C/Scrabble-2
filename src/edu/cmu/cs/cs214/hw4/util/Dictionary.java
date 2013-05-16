package edu.cmu.cs.cs214.hw4.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Collection;

import edu.cmu.cs.cs214.hw4.model.Location;

/**
 * A multi-purpose dictionary class. This provides the game some useful 
 * information regarding letters and words. Dictionary is able to 
 * check the validity of a word, provide letter tile distribution and 
 * letter score distribution.
 * @author ziw
 *
 */
public class Dictionary {

	private HashSet<String> dictionary = new HashSet<String>();
	private HashMap<String, Integer> points = new HashMap<String, Integer>();;
	private List<String> letterPool = new ArrayList<String>();
	
	
	private static final String WORDS_FILE = "./assets/words.txt";
	private static final String POINTS_FILE = "./assets/points.txt";
	private static final String LETTER_POOL = "./assets/letterPool.txt";

	/**
	 * Create a new Dictionary.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public Dictionary() throws IOException, FileNotFoundException {
		loadDictionary();
		loadLetterPoints();
		loadLetterPool();
	}

	private void loadDictionary() throws IOException, FileNotFoundException{
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(WORDS_FILE));
			String word;
			while ((word = in.readLine()) != null) {
				dictionary.add(word.trim().toLowerCase());
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}

		}
	}

	private void loadLetterPoints() throws IOException, FileNotFoundException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(POINTS_FILE));
			String line;
			while((line = in.readLine())!=null){
				line = line.trim();
				int letterPoint = Integer.parseInt(line.substring(0, 2));
				line = line.substring(2);
				for(char c : line.toCharArray()){
					points.put(new String(new char[]{c}), letterPoint);
				}
			}
			
			in.close();
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException();
		} catch (IOException e) {
			throw new IOException();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}

		}
	}
	
	
	private void loadLetterPool() throws IOException, FileNotFoundException{
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(LETTER_POOL));
			String line;
			while((line = in.readLine())!=null){
				line = line.trim();
				int numLetters = Integer.parseInt(line.substring(0, 2));
				line = line.substring(2);
				for(char c : line.toCharArray()){
					for(int i=0;i<numLetters;i++){
						letterPool.add(new String(new char[]{c}));
					}
				}
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}

		}
	}
	
	/**
	 * Get the point of the given letter
	 * @param letter
	 * @return
	 */
	public int getPoints(String letter){
		return points.get(letter.toLowerCase());
	}
	
	/**
	 * Get the point of the given letter
	 * @param c
	 * @return
	 */
	public int getPoints(char c){
		return points.get(new String(new char[]{c}));
	}

	/**
	 * Check if the given word is a word in the dictionary. This is a case
	 * insensitive check.
	 * 
	 * @param word the word to check.
	 * @return <tt>true</tt> if the word is not null and is in the dictionary.
	 */
	public boolean isWord(String word) {
		return word != null && dictionary.contains(word.toLowerCase());
	}
	
	/**
	 * Check if every word in the collection is a valid word. 
	 * @param allWords
	 * @return
	 */
	public boolean isWord(Collection<String> allWords){
		if(allWords == null) return false;
		for(String s : allWords){
			if(!isWord(s)) return false;
		}
		return true;
	}
	
	/**
	 * Return a list of letters, each letter with one or multiple occurrences representing
	 * the number of such tile in a Scrabble game.
	 * @return
	 */
	public List<String> getLetterPool(){
		//return a hard copy
		return new ArrayList<String>(letterPool);
	}

	/**
	 * Add a new word to the dictionary.
	 * @param word
	 */
	public void addWord(String word) {
		if (word == null)
			return;
		dictionary.add(word);
	}


	

}
