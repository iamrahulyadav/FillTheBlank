package com.example.jeffwang.filltheblank;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Activity containing game play
 */
public class GameFragment extends Fragment implements OnBackPressed {

    private Level mLevel;

    private TextView mTextWord;
    private TextView mTextRemaining;
    private EditText mTextGuess;
    private ListView mTextCorrectWords;
    private ListView mTextIncorrectWords;

    private int mLevelCount;

    /* How to store the game files:
     * Word with _ for blanks, possible word 1, possible word 2...
     *
     * Each line should be one 'game'
     * Parse it by commas
     * First word is the word to fill
     * All following are possible words that can fit the blanks
     *
     * 2 modes:
     * 1. use levels.txt file to get pre-made levels
     * 2. random, uses words.txt to generate solution list
     *
     * TODO: add definitions
     */

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextWord = view.findViewById(R.id.game_word);
        mTextCorrectWords = view.findViewById(R.id.game_correct_words);
        mTextIncorrectWords = view.findViewById(R.id.game_incorrect_words);
        mTextGuess = view.findViewById(R.id.game_guess);
        mTextRemaining = view.findViewById(R.id.game_remaining_words);
        Button btnGuess = view.findViewById(R.id.game_guess_btn);

        // read game values and initialize data values
        getLevelCount();
        //setPremadeRandomLevel();
        setRandomlyGeneratedLevel();

        mTextCorrectWords.setAdapter(mLevel.getCorrectWordsAdapter());
        mTextIncorrectWords.setAdapter(mLevel.getIncorrectWordsAdapter());

        btnGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guess = mTextGuess.getText().toString().trim().toLowerCase();

                int guessStatus = mLevel.guessWord(new Word(guess, ""));

                if (guessStatus == Level.GUESS_CORRECT) {
                    // guessed one of the remaining words
                    setRemainingText();
                    Toast.makeText(getContext(), "Correct guess", Toast.LENGTH_SHORT).show();
                } else if (guessStatus == Level.GUESSED_CORRECT || guessStatus == Level.GUESSED_INCORRECT) {
                    // already guessed
                    Toast.makeText(getContext(), "Already guessed", Toast.LENGTH_SHORT).show();
                } else if (guessStatus == Level.GUESS_INCORRECT) {
                    // incorrect guess
                    Toast.makeText(getContext(), "Incorrect guess", Toast.LENGTH_SHORT).show();
                }

                if (mLevel.isLevelComplete()) {
                    // TODO end game
                    Toast.makeText(getContext(), "Guessed all words", Toast.LENGTH_SHORT).show();
                    setPremadeRandomLevel();
                }

                mTextGuess.getText().clear();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Custom method to handle the back pressed action
     */
    @Override
    public void onBackPressed() {
        //openFragment(PauseFragment.newInstance());
    }

    /**
     * Open a fragment to replace the main container
     *
     * @param f fragment to open/replace current one
     */
    private void openFragment(Fragment f) {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main_container, f)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Count the total number of levels available
     * Should only be called once
     */
    private void getLevelCount() {
        try (InputStream ins = getResources().openRawResource(getResources().
                getIdentifier("levels", "raw",
                        Objects.requireNonNull(getActivity()).getPackageName()));
             BufferedReader br = new BufferedReader(new InputStreamReader(ins))) {
            mLevelCount = 0;
            while (br.readLine() != null)
                mLevelCount++;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRandomlyGeneratedLevel() {
        mLevel = new Level(this);
        setRemainingText();
        setGameWord(mLevel.getWordToFill());
    }

    /**
     * Sets the game to a random level and display the word of the level
     * TODO Consider storing these in memory, since it may be more efficient
     * than doing IO operations every time
     */
    private void setPremadeRandomLevel() {
        int lvl = new Random().nextInt(mLevelCount);

        // Read level from text file
        try (InputStream ins = getResources().openRawResource(getResources().
                getIdentifier("levels", "raw",
                        Objects.requireNonNull(getActivity()).getPackageName()));
             BufferedReader br = new BufferedReader(new InputStreamReader(ins))) {

            // Skip lines until reaching the selected level
            int counter = 0;
            while (counter++ < lvl)
                br.readLine();

            // Split the data and set the game word as first index
            String[] line = br.readLine().split(", ");

            ArrayList<Word> words = new ArrayList<>();
            for (int i = 1; i < line.length; i++) {
                words.add(new Word(line[i], ""));
            }

            // Create new level with the word and solutions
            mLevel = new Level(setGameWord(line[0]), words, getContext());

            setRemainingText();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setRemainingText() {
        mTextRemaining.setText(getString(R.string.game_remaining, mLevel.getRemainingCount()));
    }

    /**
     * Sets the game word to 'word'
     *
     * @param word current 'word' to fill in
     * @return word with spaces between every letter
     */
    private String setGameWord(String word) {
        StringBuilder spaced = new StringBuilder();
        while (!word.equals("")) {
            spaced.append(word.charAt(0)).append(" ");
            word = word.substring(1);
        }

        mTextWord.setText(spaced);
        return spaced.toString();
    }
}
