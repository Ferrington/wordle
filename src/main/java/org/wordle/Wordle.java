package org.wordle;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Wordle {
    final int NUMBER_OF_ATTEMPTS = 6;
    final String Y = "\033[0;33m";
    final String Y_END = "\033[0m";
    final String G = "\033[0;32m";
    final String G_END = "\033[0m";
    final String K = "\033[0;30m";
    final String K_END = "\033[0m";

    int guess;
    Map<Character, Character> availableLetters;
    String guessHistory;
    String errorMessage;
    String targetWord;
    Set<String> wordList;
    Random rand;
    Scanner scan;

    Wordle() {
        guess = 1;
        rand = new Random();
        scan = new Scanner(System.in);
        guessHistory = "";
        errorMessage = "";

        availableLetters = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            availableLetters.put(c, '0');
        }
    }

    public void start() {
        targetWord = chooseTargetWord();
        System.out.println(targetWord);
        wordList = getWordList();

        for (int i = 1; i <= NUMBER_OF_ATTEMPTS; i++) {
            String guess = getPlayerGuess(i);
            modifyGuessHistory(guess);
            animateGuessHistory();

            if (guess.equals(targetWord)) {
                printAvailableLetters();
                System.out.format("You Win! You got it in %s attempt%s.", i, i == 1 ? "" : "s");
                return;
            }
        }
        System.out.format("You Lose :( The answer was %s.\n", targetWord);
    }

    private void animateGuessHistory() {
        clearConsole();
        String[] lines = guessHistory.split("\n");
        String[] firstLines = Arrays.copyOfRange(lines, 0, lines.length - 1);
        for (String line : firstLines) {
            System.out.println(line);
        }

        String lastLine = lines[lines.length - 1];
        for (int i = 0; i < lastLine.length(); i++) {
            char c = lastLine.charAt(i);
            if (c >= 65 && c <= 90) {
                sleep(250);
            }
            System.out.print(lastLine.charAt(i));
        }
        System.out.print("\n");
    }

    private void modifyGuessHistory(String guess) {
        String colorfulString = "";

        for (int i = 0; i < guess.length(); i++) {
            char guessChar = guess.charAt(i);
            if (guessChar == targetWord.charAt(i)) {
                colorfulString += G + guessChar + G_END;
                modifyAvailableLetters(guessChar, 'G');
            } else if (targetWord.contains(Character.toString(guessChar))) {
                if (shouldBeColored(i, guess)) {
                    colorfulString += Y + guessChar + Y_END;
                    modifyAvailableLetters(guessChar, 'Y');
                } else {
                    colorfulString += guessChar;
                    modifyAvailableLetters(guessChar, 'K');
                }
            } else {
                colorfulString += guessChar;
                modifyAvailableLetters(guessChar, 'K');
            }

        }

        guessHistory += colorfulString + "\n";
    }

    private void modifyAvailableLetters(char guessChar, char color) {
        char prevColor = availableLetters.get(guessChar);
        List<Character> priorityList = Arrays.asList('0','K','Y','G');

        int prevPriority = priorityList.indexOf(prevColor);
        int nextPriority = priorityList.indexOf(color);

        if (nextPriority > prevPriority)
            availableLetters.put(guessChar, color);
    }

    private boolean shouldBeColored(int position, String guess) {
        char guessChar = guess.charAt(position);
        long countInTarget = targetWord.chars().filter(ch -> ch == guessChar).count();
        long countInGuess = guess.chars().filter(ch -> ch == guessChar).count();

        if (countInGuess <= countInTarget) return true;

        int count = 0;
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == guessChar)
                count++;

            if (i == position && count <= countInTarget)
                return true;
        }

        return false;
    }

    private void printAvailableLetters() {
        System.out.print("Available Letters: ");
        for (Map.Entry<Character, Character> letter : availableLetters.entrySet()) {
            char color = letter.getValue();
            if (color == 'G')
                System.out.print(G + letter.getKey() + G_END);
            else if (color == 'Y')
                System.out.print(Y + letter.getKey() + Y_END);
            else if (color == 'K')
                System.out.print(K + letter.getKey() + K_END);
            else
                System.out.print(letter.getKey());
        }
        System.out.print("\n");
    }

    private String getPlayerGuess(int attemptNumber) {
        while(true) {
            clearConsole();
            System.out.print(guessHistory);
            printAvailableLetters();
            System.out.print(errorMessage);
            errorMessage = "";


            System.out.format("Attempt %s: Guess a 5-letter word.\n", attemptNumber);
            String guess = scan.nextLine().trim().toUpperCase();
            if (wordList.contains(guess))
                return guess;
            else if (guess.length() != 5)
                errorMessage = "Please enter a 5-letter word.\n";
            else
                errorMessage = "Are you sure that's a word?\n";
        }
    }


    private Set<String> getWordList() {
        List<String> answers = DictionaryManager.getWordsFromFile("word-lists/wordle-words.txt");
        List<String> guesses = DictionaryManager.getWordsFromFile("word-lists/five-letter-guesses.txt");

        return Stream.concat(answers.stream(), guesses.stream()).collect(Collectors.toSet());
    }

    private String chooseTargetWord() {
        List<String> words = DictionaryManager.getWordsFromFile("word-lists/wordle-words.txt");
        int randomIndex = rand.nextInt(words.size());

        return words.get(randomIndex).toUpperCase();
    }

    private void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (IOException | InterruptedException ex) {
            // I should probably do something here, but I don't know what yet
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
