package com.example.jeffwang.filltheblank;

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
 * Levels are stored as
 */
public class LevelGenerator {
    // Level:

    // lower bound for # of possible solutions (inclusive)
    private static int lowerBound = 5;
    // upper bound for # of possible solutions (inclusive)
    private static int upperBound = 10;
    // # of levels to generate
    private static int levelsToMake = 5;
    // minimum word length of level
    private static int minWordLength = 5;
    // maximum word length of level
    private static int maxWordLength = 5;
    // maximum number of blanks in a word
    private static int blanksCount = 2;

    public static void main(String[] args) {
        generate(new Fragment());
    }

    public static void setBlanks(int blanks) {
        blanksCount = blanks;
    }

    public static void setUpperWordBound(int upper) {
        maxWordLength = upper;
    }

    public static void setLowerWordBound(int lower) {
        minWordLength = lower;
    }

    public static void setLevelsToMake(int levels) {
        levelsToMake = levels;
    }

    public static void setLowerBound(int lower) {
        lowerBound = lower;
    }

    public static void setUpperBound(int upper) {
        upperBound = upper;
    }

    public static Level generate(Fragment f) {
        levelsToMake = 1;

        ArrayList<String> words = new ArrayList<>();

        // Read all words from file into memory
        try (
                //InputStream ins = new FileInputStream(new File("app/src/main/res/raw/scrabble_dict.txt"));//words_alpha.txt"));
                InputStream ins = f.getResources().openRawResource(f.getResources().getIdentifier("scrabble_dict", "raw", Objects.requireNonNull(f.getActivity()).getPackageName()));
                BufferedReader br = new BufferedReader(new InputStreamReader(ins))) {
            String word;
            while ((word = br.readLine()) != null)
                words.add(word);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random r = new Random();

        ArrayList<Level> levels = new ArrayList<>();

        while (levels.size() < levelsToMake) {
            StringBuilder word = new StringBuilder(words.get(r.nextInt(words.size())));

            // ensure word is long enough
            if (word.length() >= minWordLength && word.length() <= maxWordLength) {
                //blanksCount = r.nextInt(word.length() / 2) + 1;
                blanksCount = 2;

                // add blanks to word
                while (blanksCount > 0) {
                    int makeBlank = r.nextInt(word.length());
                    if (word.charAt(makeBlank) != '_') {
                        blanksCount--;
                        word.setCharAt(makeBlank, '_');
                    }
                }

                StringBuilder pattern = new StringBuilder(word);
                while (pattern.indexOf("_") != -1) {
                    pattern.setCharAt(pattern.indexOf("_"), '.');
                }

                Pattern p = Pattern.compile("^" + pattern.toString() + "$");

                // count number of possible solutions
                ArrayList<String> solutions = new ArrayList<>();
                for (int i = 0; i < words.size(); i++) {
                    if (p.matcher(words.get(i)).find()) {
                        solutions.add(words.get(i));
                    }
                }

                if (solutions.size() >= lowerBound && solutions.size() <= upperBound) {
                    levels.add(new Level(solutions, word.toString(), f.getContext()));
                }
            }
        }

        for (int i = 0; i < levels.size(); i++) {
            StringBuilder s = new StringBuilder();
            String w = levels.get(i).getWordToFill();
            while (!w.equals("")) {
                s.append(w.charAt(0)).append(" ");
                w = w.substring(1);
            }
            System.out.print("\n" + s + " " + levels.get(i).getRemainingCount() + " ");
            for (int j = 0; j < levels.get(i).getRemainingWords().size(); j++) {
                System.out.print(levels.get(i).getRemainingWords().get(j) + " ");
            }
            //System.out.println(levels.get(i).getWordToFill() + " " + levels.get(i).getRemainingCount());
        }

        return levels.get(0);
    }
}
