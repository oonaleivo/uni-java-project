package fi.tuni.prog3.wordle;

import java.util.Map;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Wordle extends Application {
    private Game game;
    private String targetWord;
    private int gameNumber = 0;
    private boolean gameActive;
    private Label [][] allLabels;
    private int lastColumn;
    private int lastRow;
    private String guessedWord;
    private GridPane grid;
    private Button newGameButton;
    private Label infoLabel;
    private Scene scene;
    
    @Override
    public void start(Stage stage) {
        // init a new game
        newGame(stage);
        
        // set stage
        stage.setTitle("Wordle");
        stage.show();
    }
    
    private void newGame(Stage stage) {
        game = new Game();
        targetWord = game.getWord(gameNumber);
        gameNumber++; // increased so we always get a new word
        gameActive = true;
        allLabels = new Label[6][targetWord.length()];
        lastColumn = 0;
        lastRow = 0;
        guessedWord = "";
        
        // background for letter labels
        BackgroundFill whiteBackgroundFill = new BackgroundFill(
            Color.LIGHTSKYBLUE, null, null);
        Background whiteBackground = new Background(whiteBackgroundFill);
        
        // border for letters
        Border border = new Border(new BorderStroke(
            Color.BLACK, BorderStrokeStyle.SOLID,
            CornerRadii.EMPTY, new BorderWidths(2)));
        
        grid = new GridPane();
        
        // create grid of labels
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < targetWord.length(); c++) {
                Label letterLabel = new Label();
                letterLabel.setBackground(whiteBackground);
                letterLabel.setBorder(border);
                letterLabel.setPrefSize(100, 100);
                letterLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
                letterLabel.setAlignment(Pos.CENTER);
                String id = String.format("%d_%d", r,c);
                letterLabel.setId(id);
                grid.add(letterLabel, c+1, r+1);
                allLabels [r][c] = letterLabel;
            }
        }
        
        // create constant game elements
        newGameButton = new Button();
        newGameButton.setText("Start new game");
        newGameButton.setPrefSize(120, 40);
        newGameButton.setId("newGameBtn");
        newGameButton.setBorder(border);
        grid.add(newGameButton, 0, 0);
        newGameButton.setOnAction((event) -> {
            newGame(stage);
        });
        
        infoLabel = new Label();
        infoLabel.setId("infoBox");
        infoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        grid.add(infoLabel, 2,0,3,1);
        
        // create scene and add grid to it
        int gridWidth = 100 * targetWord.length() + 200;
        int gridHeigth = 700;
        scene = new Scene(grid, gridWidth, gridHeigth);
        
        // set the scene to the stage
        stage.setScene(scene);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::handleKeyPressedEvent);
        scene.addEventHandler(KeyEvent.KEY_TYPED, this::handleKeyTypedEvent);  
        scene.getRoot().requestFocus();
    }
    
    private void handleKeyPressedEvent(KeyEvent event) {
        if (gameActive) {
            if (event.getCode() == KeyCode.ENTER) {
                if (lastColumn != targetWord.length()) {
                    infoLabel.setText("Give a complete word before pressing Enter!");
                }
                else {
                    // get letters from labels and save them to guessedword
                    updateGuessedWord();
                    
                    // change colors
                    changeColor();
                    
                    if(gameOver()) {
                        gameActive = false;
                    }
                    
                    else {
                        // move to the next row
                        lastRow++;
                        lastColumn = 0;
                        guessedWord = "";
                    }
                    scene.getRoot().requestFocus();
                }
            }
            
            else if (event.getCode() == KeyCode.BACK_SPACE) {
                if (lastColumn != 0) {
                    allLabels[lastRow][--lastColumn].setText("");
                }
            }
        }
    }
    
    private void handleKeyTypedEvent(KeyEvent event) {
        if (gameActive) {
            String typedCharacter = event.getCharacter();
            
            if (Character.isLetter(typedCharacter.charAt(0))) {
                infoLabel.setText("");
                typedCharacter = typedCharacter.toUpperCase();
                
                if (lastColumn != targetWord.length()) {
                    // update the label's text with the typed character
                    allLabels[lastRow][lastColumn].setText(typedCharacter);
                    lastColumn++;
                }
            }
        }
    }
    
    private void updateGuessedWord() {
        for (Label currentLabel : allLabels[lastRow]) {
            String character = currentLabel.getText();
            guessedWord += character;
        }
    }
    
    private void changeColor() {
        // check what letters were guessed correctly
        Map<Integer, String> colorMap = game.guess(guessedWord);
        
        for (int i = 0; i < targetWord.length(); i++) {
            Label currentLabel = allLabels[lastRow][i];
            String color = colorMap.get(i);
            if (color.equals("GREEN")) {
                BackgroundFill backgroundFill = new BackgroundFill(
                    Color.GREEN, null, null);
                Background background = new Background(backgroundFill);
                currentLabel.setBackground(background);
            }
            else if (color.equals("ORANGE")) {
                BackgroundFill backgroundFill = new BackgroundFill(
                    Color.ORANGE, null, null);
                Background background = new Background(backgroundFill);
                currentLabel.setBackground(background);
            }
            else if (color.equals("GRAY")) {
                BackgroundFill backgroundFill = new BackgroundFill(
                    Color.GRAY, null, null);
                Background background = new Background(backgroundFill);
                currentLabel.setBackground(background);
            }           
        }      
    }
    
    private boolean gameOver() {
        if (game.correctGuess(guessedWord)) {
            infoLabel.setText("Congratulations, you won!");
            infoLabel.setTextFill(Color.GREEN); // Set the text color to GREEN
            return true;
        }
        else if (lastRow == 5) {
            infoLabel.setText("Game over, you lost!");
            infoLabel.setTextFill(Color.RED); // Set the text color to red
            return true;
        }
        return false;
    }
    
    
    public static void main(String[] args) {
        launch();
    }
}