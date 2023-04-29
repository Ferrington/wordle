package org.wordle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        List<String> result = new ArrayList<>();
        try {
            File myFile = new File(fileName);
            Scanner fileReader = new Scanner(myFile);
            while(fileReader.hasNextLine()) {
                String word = fileReader.nextLine().trim().toUpperCase();
                if (word.length() > 0)
                    result.add(word);
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return result;
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
