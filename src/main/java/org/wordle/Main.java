package org.wordle;

import java.util.Scanner;

public class Main {
    static Scanner scan;
    static int[] statistics;
    static int streak;
    static int maxStreak;

    public static void main(String[] args) {
        scan = new Scanner(System.in);
        statistics = new int[7];
        streak = 0;
        maxStreak = 0;

        Wordle.welcomeAnimation();

        GameMode mode = getModeSelection();
        int wordLength = mode == GameMode.WORDLE_JUNIOR ? getWordLength() : 5;
        Difficulty difficulty = mode == GameMode.WORDLE_JUNIOR ? getDifficulty() : null;

        boolean playAgain = true;
        while (playAgain) {
            Wordle wordle = new Wordle(scan, mode, wordLength, difficulty);
            int numberOfGuesses = wordle.start();

            statistics[numberOfGuesses]++;
            if (numberOfGuesses == 0) {
                streak = 0;
            } else {
                streak++;
                if (streak > maxStreak)
                    maxStreak = streak;
            }

            printStatistics();
            playAgain = getPlayAgain();
        }
    }

    private static void printStatistics() {
        int totalGamesPlayed = 0;
        int maxGuesses = 0;
        for (int i = 0; i < statistics.length; i++) {
            totalGamesPlayed += statistics[i];
            if (i > 0 && statistics[i] > maxGuesses)
                maxGuesses = statistics[i];
        }

        int winPercent = 100 * (totalGamesPlayed - statistics[0]) / totalGamesPlayed;

        System.out.println("Played\tWin %\tCurrent Streak\tMax Streak");
        System.out.format("%6s\t%5s\t%14s\t%10s\n", totalGamesPlayed, winPercent, streak, maxStreak);

        System.out.println("Guess Distribution");
        for (int i = 1; i <= 6; i++) {
            int bars = 40 * statistics[i] / maxGuesses;
            String line = "";
            for (int j = 0; j < bars; j++) {
                line += "-";
            }

            String guessCountDisplay = statistics[i] > 0 ? String.valueOf(statistics[i]) : "";
            System.out.format("%s |", i);
            System.out.format("%-40s| %s\n", line, guessCountDisplay);
        }

        System.out.println("\n");
    }

    private static boolean getPlayAgain() {
        while (true) {
            System.out.println("Would you like to play again?");
            System.out.println("\t1. Yes");
            System.out.println("\t2. No");
            String response = scan.nextLine().trim();
            if (response.charAt(0) == '1') {
                return true;
            } else if (response.charAt(0) == '2') {
                Wordle.clearConsole();
                System.out.println("Thanks for playing!");
                return false;
            }
        }
    }

    private static Difficulty getDifficulty() {
        while (true) {
            System.out.println("Please select a difficulty.");
            System.out.format("\t1. Easy (%s words + Custom Words)\n", Difficulty.EASY.lines);
            System.out.format("\t2. Medium (%s words + Custom Words)\n", Difficulty.MEDIUM.lines);
            System.out.format("\t3. Hard (%s words + Custom Words)\n", Difficulty.HARD.lines);
            System.out.println("\t4. Genius (All words + Custom Words)");
            String response = scan.nextLine().trim();
            char c = response.charAt(0);
            if (c == '1')
                return Difficulty.EASY;
            else if (c == '2')
                return Difficulty.MEDIUM;
            else if (c == '3')
                return Difficulty.HARD;
            else if (c == '4')
                return Difficulty.GENIUS;

            System.out.println("Invalid Selection");
        }
    }

    private static int getWordLength() {
        while (true) {
            System.out.println("Should words have (4) or (5) letters?");
            String response = scan.nextLine().trim();
            char c = response.charAt(0);
            if (c == '4' || c == '5')
                return Integer.parseInt(Character.toString(c));

            System.out.println("Invalid Selection");
        }
    }

    private static GameMode getModeSelection() {
        while (true) {
            System.out.println("Which game mode would you like to play?");
            System.out.println("\t1. Wordle");
            System.out.println("\t2. Wordle Junior");
            String response = scan.nextLine().trim();
            char c = response.charAt(0);
            if (c == '1')
                return GameMode.WORDLE;
            else if (c == '2')
                return GameMode.WORDLE_JUNIOR;

            System.out.println("Invalid Selection");
        }
    }
}