package com.example.jeffwang.filltheblank;

import android.support.annotation.NonNull;

/**
 * Word with the word and its definition TODO add possibility of multiple definitions
 */
public class Word {
    private String word;
    private String definition;

    Word(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    public String getWord() {
        return word;
    }

    public String getDefinition() {
        return definition;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Word) {
            return word.equals(((Word) o).getWord());
        } else if (o instanceof String) {
            return word.equals(o);
        } else {
            return false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return word;
    }
}
