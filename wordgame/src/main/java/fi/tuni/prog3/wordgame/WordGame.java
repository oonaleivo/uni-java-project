
package fi.tuni.prog3.wordgame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class WordGame {
    private ArrayList<String> words;
    private WordGameState gameState;
    private boolean gameActive;
    private String selectedWord;
    private ArrayList<Character> joArvatutKirjaimet;

    public WordGame(String wordFilename) throws IOException {
        words = new ArrayList<>();
        try (var fileReader = new BufferedReader(new FileReader(wordFilename))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                words.add(line);
            }
        }
        gameActive = false;
    }

    public void initGame(int wordIndex, int mistakeLimit) throws GameStateException {
        int totalWords = words.size();

        if (totalWords > 0) {
            int selectedWordIndex = wordIndex % totalWords;
            selectedWord = words.get(selectedWordIndex);
            int missingChars = selectedWord.length();
            String hiddenWord = "_".repeat(missingChars);
            gameState = new WordGame.WordGameState(hiddenWord, 0, mistakeLimit, missingChars);
            gameActive = true;
            joArvatutKirjaimet = new ArrayList<>();
        } else {
            throw new GameStateException("No words available to start the game.");
        }
    }

    public boolean isGameActive() {
        return gameActive;
    }

    public WordGameState getGameState() throws GameStateException {
        if (!gameActive) {
            throw new GameStateException("There is currently no active word game!");
        }
        return gameState;
    }

    public WordGameState guess(char c) throws GameStateException {
    if (!gameActive) {
        throw new GameStateException("There is currently no active word game!");
    }
        int matchesFound = 0; 
        char [] charArray = gameState.word.toCharArray();
        char charLower = Character.toLowerCase(c);
        
        for (int i = 0; i < selectedWord.length(); i++) {
            char letter = Character.toLowerCase(selectedWord.charAt(i));
            if (charLower == letter) {
                matchesFound += 1;
                charArray[i] = letter;
            }
        }
        
        boolean alreadyGuessed = joArvatutKirjaimet.contains(charLower);
        if (matchesFound != 0 && !alreadyGuessed) {
            gameState.missingChars -= matchesFound;
            joArvatutKirjaimet.add(Character.toLowerCase(c));
            gameState.word = new String(charArray);
        }
        
        else {
            gameState.mistakes += 1;
        }
        
        if (gameState.missingChars == 0 || gameState.mistakes > gameState.mistakeLimit) {
            gameState.word = selectedWord;
            gameActive = false;
        }

        return gameState;
    }


    public WordGameState guess(String word) throws GameStateException {
        if (!gameActive) {
            throw new GameStateException("There is currently no active word game!");
        }

        String guessedWord = word.toLowerCase();
        String sWord = selectedWord.toLowerCase();

        if (guessedWord.equals(sWord)) {
            gameState.word = selectedWord;
            gameState.missingChars = 0;
            gameActive = false;          
        } 
        
        else {
            gameState.mistakes += 1;
        }
        
        if (gameState.getMissingChars() == 0) {
            gameActive = false;
        }

        if (gameState.missingChars == 0 || gameState.mistakes > gameState.mistakeLimit) {
            gameState.word = selectedWord;
            gameActive = false;
        }

        return gameState;
    }

    public static class WordGameState {
        private String word;
        private int mistakes;
        private int mistakeLimit;
        private int missingChars;

        private WordGameState(String word, int mistakes, int mistakeLimit, int missingChars) {
            this.word = word;
            this.mistakes = mistakes;
            this.mistakeLimit = mistakeLimit;
            this.missingChars = missingChars;
        }

        public String getWord() {
            return word;
        }

        public int getMistakes() {
            return mistakes;
        }

        public int getMistakeLimit() {
            return mistakeLimit;
        }

        public int getMissingChars() {
            return missingChars;
        }
    }
}
