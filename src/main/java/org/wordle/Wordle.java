package org.wordle;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Wordle {
    final int NUMBER_OF_ATTEMPTS = 6;
    final static String Y = "\033[0;33m";
    final static String Y_END = "\033[0m";
    final static String G = "\033[0;32m";
    final static String G_END = "\033[0m";
    final static String K = "\033[0;30m";
    final static String K_END = "\033[0m";

    GameMode mode;
    int wordLength;
    Difficulty difficulty;
    int guess;
    Map<Character, Character> availableLetters;
    String guessHistory;
    String errorMessage;
    String targetWord;
    String answerFile;
    String guessFile;
    Set<String> wordList;
    Random rand;
    Scanner scan;

    Wordle(Scanner scan, GameMode mode, int wordLength, Difficulty difficulty) {
        this.scan = scan;
        this.mode = mode;
        this.wordLength = wordLength;
        this.difficulty = difficulty;
        guess = 1;
        rand = new Random();
        guessHistory = "";
        errorMessage = "";

        availableLetters = new HashMap<>();
        for (char c = 'A'; c <= 'Z'; c++) {
            availableLetters.put(c, '0');
        }
        setWordLists();
    }

    public int start() {
        targetWord = chooseTargetWord();
        wordList = getWordList();

        for (int i = 1; i <= NUMBER_OF_ATTEMPTS; i++) {
            String guess = getPlayerGuess(i);
            modifyGuessHistory(guess);
            animateGuessHistory();

            if (guess.equals(targetWord)) {
                printAvailableLetters();
                System.out.format("You Win! You got it in %s attempt%s.\n\n", i, i == 1 ? "" : "s");
                return i;
            }
        }
        System.out.format("You Lose :( The answer was %s.\n", targetWord);
        return 0;
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


            System.out.format("Attempt %s: Guess a %s-letter word.\n", attemptNumber, wordLength);
            String guess = scan.nextLine().trim().toUpperCase();
            if (wordList.contains(guess))
                return guess;
            else if (guess.length() != wordLength)
                errorMessage = String.format("Please enter a %s-letter word.\n", wordLength);
            else
                errorMessage = "Are you sure that's a word?\n";
        }
    }

    private void setWordLists() {
        if (mode == GameMode.WORDLE)
            answerFile = "word-lists/wordle-words.txt";
        else if (wordLength == 4)
            answerFile = "word-lists/four-letter-words.txt";
        else
            answerFile = "word-lists/five-letter-words.txt";

        if (wordLength == 5)
            guessFile = "word-lists/five-letter-guesses.txt";
        else
            guessFile = "word-lists/four-letter-guesses.txt";
    }

    private Set<String> getWordList() {
        List<String> answers = DictionaryManager.getWordsFromFile(answerFile);
        List<String> guesses = DictionaryManager.getWordsFromFile(guessFile);

        if (mode == GameMode.WORDLE_JUNIOR) {
            List<String> customGuesses = wordLength == 5 ?
                    DictionaryManager.getWordsFromFile("word-lists/five-letter-words-custom.txt"):
                    DictionaryManager.getWordsFromFile("word-lists/four-letter-words-custom.txt");

            if (customGuesses.size() > 0)
                return concatenateToSet(answers, guesses, customGuesses);
        }

        return concatenateToSet(answers, guesses);
    }

    private String chooseTargetWord() {
        if (mode == GameMode.WORDLE_JUNIOR) {
            List<String> words =
                    wordLength == 5 ?
                    DictionaryManager.getWordsFromFile("word-lists/five-letter-words-custom.txt"):
                    DictionaryManager.getWordsFromFile("word-lists/four-letter-words-custom.txt");

            int wordsSize = words.size();
            if (wordsSize > 0 && rand.nextInt(100) < Math.min(5 + (wordsSize * .5), 20)) {
                int randomIndex = rand.nextInt(words.size());
                return words.get(randomIndex).toUpperCase();
            }
        }

        List<String> words = DictionaryManager.getWordsFromFile(answerFile, difficulty);
        int randomIndex = rand.nextInt(words.size());

        return words.get(randomIndex).toUpperCase();
    }

    public static void welcomeAnimation() {
        final int WORD_HEIGHT = 7;
        final int WORD_WIDTH = 11;
        Map<Character, String[]> letters = DictionaryManager.getAnimationLettersFromFile("word-lists/wordle-animation.txt", WORD_WIDTH, WORD_HEIGHT);

        final String[] WORDS = new String[]{"CEDARS", "POWDER", "WORDLE"};
        final String[] COLORS = new String[]{" YY Y ", " GYGYY", "GGGGGG"};

        for (int wordIndex = 0; wordIndex < WORDS.length; wordIndex++) {
            clearConsole();
            String COLOR = COLORS[wordIndex];
            for (int line = 0; line < WORD_HEIGHT; line++) {
                for (int letterIndex = 0; letterIndex < WORDS[wordIndex].length(); letterIndex++) {
                    char colorChar = COLOR.charAt(letterIndex);
                    String color = "";
                    String color_end = "";
                    if (colorChar == 'G') {
                        color = G;
                        color_end = G_END;
                    } else if (colorChar == 'Y') {
                        color = Y;
                        color_end = Y_END;
                    }

                    String segment = letters.get(WORDS[wordIndex].charAt(letterIndex))[line];
                    System.out.format("%s%s%s", color, segment, color_end);
                }
                System.out.print("\n");
            }
            sleep(2000);
        }
        System.out.print("\n\n");
    }

    public static void clearConsole() {
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

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @SafeVarargs
    public static<T> Set<T> concatenateToSet(List<T>... lists)
    {
        return Stream.of(lists)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
