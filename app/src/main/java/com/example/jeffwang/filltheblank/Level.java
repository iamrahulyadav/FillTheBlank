package com.example.jeffwang.filltheblank;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Pre-made levels are stored as
 * word with blanks, word1, word2, ...
 * <p>
 * Randomly generated levels can be stored as
 * word with blanks
 * and the possible words can be found at runtime
 */
public class Level {
    static final int GUESS_CORRECT = 1;
    static final int GUESS_INCORRECT = 2;
    static final int GUESSED_CORRECT = 3;
    static final int GUESSED_INCORRECT = 4;

    private ArrayList<Word> remainingWords;

    private MyAdapter<Word> mCorrectWordsAdapter;
    private MyAdapter<Word> mIncorrectWordsAdapter;

    private String mWordToFill;

    /**
     * TODO this is a temporary constructor with String word inputs
     *
     * @param words      list of solutions (string)
     * @param wordToFill word with blanks
     * @param context    app context
     */
    Level(ArrayList<String> words, String wordToFill, Context context) {
        constructor(wordToFill, words, context);
    }

    /**
     * Creates a level with the given word and list of solutions
     *
     * @param wordToFill word with blanks (Word)
     * @param words      list of solutions
     * @param context    app context
     */
    Level(String wordToFill, ArrayList<Word> words, Context context) {
        remainingWords = words;
        mWordToFill = wordToFill;

        mCorrectWordsAdapter = new MyAdapter<>(new ArrayList<Word>(), context);
        mIncorrectWordsAdapter = new MyAdapter<>(new ArrayList<Word>(), context);
    }

    /**
     * Generates a random level with default settings
     *
     * @param fragment calling fragment, required for file access
     */
    Level(Fragment fragment) {
        this(fragment, 5, 10, 4, 6, 2);
    }

    /**
     * Generates a random level with the given settings
     * TODO get list of all Words with definitions instead of just strings
     *
     * @param fragment      calling fragment, required for file accesss
     * @param lowerBound    lower bound for # of solutions (inclusive)
     * @param upperBound    upper bound for # of solutions (inclusive)
     * @param minWordLength min word length for level
     * @param maxWordLength max word length for level
     * @param blanksCount   maximum number of blanks in a word
     */
    Level(Fragment fragment, int lowerBound, int upperBound, int minWordLength, int maxWordLength, int blanksCount) {
        ArrayList<String> words = new ArrayList<>();
        Random r = new Random();

        // Read all words from file into memory
        try (InputStream ins = fragment.getResources().openRawResource(fragment.getResources()
                .getIdentifier("scrabble_dict", "raw",
                        Objects.requireNonNull(fragment.getActivity()).getPackageName()));
             BufferedReader br = new BufferedReader(new InputStreamReader(ins))) {
            String word;
            while ((word = br.readLine()) != null)
                words.add(word);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean levelValid = false;
        while (!levelValid) {
            // Choose a random word to generate a level from
            StringBuilder word = new StringBuilder(words.get(r.nextInt(words.size())));

            // Ensure word is long enough
            if (word.length() >= minWordLength && word.length() <= maxWordLength) {
                int blanks = blanksCount;

                // Add blanks to word
                while (blanks > 0) {
                    int index = r.nextInt(word.length()); // index to make blank
                    if (word.charAt(index) != '_') {
                        blanks--;
                        word.setCharAt(index, '_');
                    }
                }

                Pattern p = Pattern.compile("^" + word.toString().replaceAll("_", "\\.") + "$");

                // Count number of possible solutions
                ArrayList<String> solutions = new ArrayList<>();
                for (String w : words)
                    if (p.matcher(w).find())
                        solutions.add(w);

                // Ensure there aren't too many or few solutions
                if (solutions.size() >= lowerBound && solutions.size() <= upperBound) {
                    levelValid = true;
                    constructor(word.toString(), words, fragment.getContext());
                }
            }
        }
    }

    private void constructor(String wordToFill, ArrayList<String> words, Context context) {
        remainingWords = new ArrayList<>();
        for (String word : words) {
            remainingWords.add(new Word(word, ""));
        }

        mWordToFill = wordToFill;
        mCorrectWordsAdapter = new MyAdapter<>(new ArrayList<Word>(), context);
        mIncorrectWordsAdapter = new MyAdapter<>(new ArrayList<Word>(), context);
    }

    /**
     * Gets adapter for correctly guessed words
     *
     * @return adapter
     */
    MyAdapter<Word> getCorrectWordsAdapter() {
        return mCorrectWordsAdapter;
    }

    /**
     * Gets adapter for incorrectly guessed words
     *
     * @return adapter
     */
    MyAdapter<Word> getIncorrectWordsAdapter() {
        return mIncorrectWordsAdapter;
    }

    /**
     * Gets list of remaining words/solutions
     *
     * @return list
     */
    ArrayList<Word> getRemainingWords() {
        return remainingWords;
    }

    /**
     * Returns a count of the number of remaining words
     *
     * @return how many words are left
     */
    int getRemainingCount() {
        return remainingWords.size();
    }

    /**
     * Checks if level is complete based on remaining words
     *
     * @return if level is complete
     */
    boolean isLevelComplete() {
        return remainingWords.isEmpty();
    }

    /**
     * Check if guessed word is correct, incorrect, or already guessed
     *
     * @param guess guessed word
     * @return if guess if correct and not guessed before
     */
    int guessWord(Word guess) {
        if (remainingWords.contains(guess)) {
            // guess correct
            remainingWords.remove(guess);
            mCorrectWordsAdapter.add(guess);
            return GUESS_CORRECT;
        } else if (mCorrectWordsAdapter.contains(guess)) {
            // already guessed correctly
            return GUESSED_CORRECT;
        } else if (mIncorrectWordsAdapter.contains(guess)) {
            // already guessed incorrectly
            return GUESSED_INCORRECT;
        } else {
            // guess incorrect
            mIncorrectWordsAdapter.add(guess);
            return GUESS_INCORRECT;
        }
    }

    String getWordToFill() {
        return mWordToFill;
    }
}
