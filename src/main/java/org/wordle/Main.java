package org.wordle;

import java.util.Scanner;

public class Main {
    static Scanner scan;
    public static void main(String[] args) {
        scan = new Scanner(System.in);

        GameMode mode = getModeSelection();
        int wordLength = mode == GameMode.WORDLE_JUNIOR ? getWordLength() : 5;
        Difficulty difficulty = mode == GameMode.WORDLE_JUNIOR ? getDifficulty() : null;

        Wordle wordle = new Wordle(scan, mode, wordLength, difficulty);
        wordle.start();
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
    };

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