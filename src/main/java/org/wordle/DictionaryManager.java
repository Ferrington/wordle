package org.wordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DictionaryManager {
    public static void addWordsToKidsDictionary(String fileName) {
        final String FOUR_LETTER_FILE = "word-lists/four-letter-words.txt";
        final String FIVE_LETTER_FILE = "word-lists/five-letter-words.txt";

        List<String> fourLetterWords = getWordsFromFile(FOUR_LETTER_FILE);
        List<String> fiveLetterWords = getWordsFromFile(FIVE_LETTER_FILE);
        List<String> newWords = getWordsFromFile(fileName);

        for (String word : newWords) {
            if (word.length() == 4 && !fourLetterWords.contains(word))
                fourLetterWords.add(word);
            else if (word.length() == 5 && !fiveLetterWords.contains(word))
                fiveLetterWords.add(word);
        }

        updateFile(FOUR_LETTER_FILE, fourLetterWords);
        System.out.println("4 letter words: " + fourLetterWords.size());
        updateFile(FIVE_LETTER_FILE, fiveLetterWords);
        System.out.println("5 letter words: " + fiveLetterWords.size());

    }

    public static List<String> getWordsFromFile(String fileName) {
        return getWordsFromFile(fileName, null);
    }

    public static List<String> getWordsFromFile(String fileName, Difficulty difficulty) {
        int stoppingPoint = difficulty == null ? Integer.MAX_VALUE : difficulty.lines;
        List<String> result = new ArrayList<>();
        try {
            File myFile = new File(fileName);
            Scanner fileReader = new Scanner(myFile);
            int i = 0;
            while(fileReader.hasNextLine() && i < stoppingPoint) {
                String word = fileReader.nextLine().trim().toUpperCase();
                if (word.length() > 0) {
                    result.add(word);
                    i++;
                }
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static Map<Character, String[]> getAnimationLettersFromFile(String fileName, int WORD_WIDTH, int WORD_HEIGHT) {
        final String LETTERS = "WORDLECASP";

        List<String> lines = new ArrayList<>();
        try {
            File myFile = new File(fileName);
            Scanner fileReader = new Scanner(myFile);
            while(fileReader.hasNextLine()){
                String line = fileReader.nextLine();
                line = String.format("%-"+ WORD_WIDTH +"s", line);
                if (line.length() > 0)
                    lines.add(line);
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Map<Character, String[]> letters = new HashMap<>();
        int charIndex = 0;
        String[] letterLines = new String[WORD_HEIGHT];
        for (int i = 0; i < lines.size() + 1; i++) {
            if (i % WORD_HEIGHT == 0 && i != 0) {
                letters.put(LETTERS.charAt(charIndex), letterLines);
                letterLines = new String[WORD_HEIGHT];
                charIndex++;

                if (i == lines.size())
                    break;
            }
            letterLines[i % WORD_HEIGHT] = lines.get(i);
        }

        return letters;
    }

    private static void updateFile(String fileName, List<String> words) {
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(String.join("\n", words));
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
