
package fi.tuni.prog3.streams2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MovieAnalytics2 {
    private List<Movie> moviesList;

    public MovieAnalytics2() {
        moviesList = new ArrayList<>();
    }
    
    public void populateWithData(String fileName) 
            throws FileNotFoundException, IOException {
        try {
            moviesList = new BufferedReader(new FileReader(fileName))
                    .lines()
                    .map(line -> line.split(";"))
                    .filter(parts -> parts.length == 6)
                    .map(parts -> new Movie(parts[0], Integer.parseInt(parts[1]), 
                            Integer.parseInt(parts[2]), parts[3], Double.parseDouble(parts[4]), parts[5]))
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            
        }
    }
    
    public void printCountByDirector(int n) {
        moviesList.stream()
                .collect(Collectors.groupingBy(Movie::getDirector, Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> {
                    int countComparison = e2.getValue().compareTo(e1.getValue());
                    return countComparison != 0 ? countComparison : e1.getKey().compareTo(e2.getKey());
                })
                .limit(n)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " movies"));
        }
  
    public void printAverageDurationByGenre() {
        moviesList.stream()
                .collect(Collectors.groupingBy(Movie::getGenre, Collectors.averagingInt(Movie::getDuration)))
                .entrySet().stream()
                .sorted(Comparator.comparingDouble(entry -> entry.getValue()))
                .forEach(entry -> System.out.println(entry.getKey() + ": " + String.format("%.2f", entry.getValue())));
    }
    
    public void printAverageScoreByGenre() {
        moviesList.stream()
                .collect(Collectors.groupingBy(Movie::getGenre, Collectors.averagingDouble(Movie::getScore)))
                .entrySet().stream()
                .sorted((entry1, entry2) -> {
                    int compareByAverage = Double.compare(entry2.getValue(), entry1.getValue());
                    if (compareByAverage != 0) {
                        return compareByAverage;
                    }
                    return entry1.getKey().compareTo(entry2.getKey());
                })
                
                 .forEach(entry -> System.out.println(entry.getKey() + ": " + String.format("%.2f", entry.getValue())));
    }
}