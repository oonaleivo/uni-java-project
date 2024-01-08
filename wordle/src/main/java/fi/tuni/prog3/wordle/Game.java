/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fi.tuni.prog3.wordle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author oonam
 */
public class Game {
    private String targetWord;
    
    public Game() {
        
    }
    
    public void chooseWord(int wordIndex){
        String wordsFile = "words.txt";
        try {
            // read from file
            java.nio.file.Path filePath = Paths.get(wordsFile);
            List<String> words = Files.readAllLines(filePath);
            
            
            // choose next word
            if (!words.isEmpty()) {
                targetWord = words.get(wordIndex).toUpperCase();
            }
            
        } catch (IOException e) {
        }
    }
    
    public String getWord(int wordIndex) {
        chooseWord(wordIndex);
        return targetWord;
    }
    
    public Map<Integer, String> guess(String guessedWord) {
        Map<Integer, String> colorMap = new HashMap<>();
        
        // compare the words
        for (int i = 0; i < targetWord.length(); i++) {
            char wordChar = targetWord.charAt(i);
            char guessedChar = guessedWord.charAt(i);
            
            if (wordChar == guessedChar) {
                colorMap.put(i, "GREEN");
            }
            else if (targetWord.contains(String.valueOf(guessedChar))) {
                colorMap.put(i, "ORANGE");
            }
            else {
                colorMap.put(i, "GRAY");
            }
        }
        
        return colorMap;
    }
    
    public boolean correctGuess(String guessedWord) {
        return guessedWord.equals(targetWord);
    }
}
