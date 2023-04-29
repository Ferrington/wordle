package org.wordle;

import java.util.Dictionary;

public class Main {
    public static void main(String[] args) {
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current working directory: " + currentDir);

        Wordle wordle = new Wordle();
        wordle.start();
    }
}