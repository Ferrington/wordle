package org.wordle;

public enum Difficulty {
    EASY(50),
    MEDIUM(100),
    HARD(150),
    GENIUS(Integer.MAX_VALUE);

    final int lines;

    Difficulty(int lines) {
        this.lines = lines;
    }
}
