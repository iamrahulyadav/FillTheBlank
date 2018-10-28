package com.example.jeffwang.filltheblank;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean mSoundOn;
    private boolean mMusicOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize frame as the main menu 
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main_container, MainMenuFragment.newInstance())
                .commit();

    }

    @Override
    public void onBackPressed() {
        tellFragments();
        super.onBackPressed();
    }

    public boolean isSoundOn() {
        return mSoundOn;
    }

    public boolean isMusicOn() {
        return mMusicOn;
    }

    /**
     * Toggles state of music, pauses/resumes, then saves new settings
     */
    public void toggleMusic() {
        mMusicOn = !mMusicOn;
        if (mMusicOn)
            resumeMusic();
        else
            pauseMusic();
        save();
    }

    /**
     * Toggles state of sound, pauses/resumes, then saves new settings
     */
    public void toggleSound() {
        mSoundOn = !mSoundOn;
        if (mSoundOn)
            resumeSound();
        else
            pauseSound();
        save();
    }

    /**
     * Resumes music
     */
    private void resumeMusic() {

    }

    /**
     * Enables sounds
     */
    private void resumeSound() {

    }

    /**
     * Pauses music
     */
    private void pauseSound() {

    }

    /**
     * Disables sound
     */
    private void pauseMusic() {

    }

    /**
     * Saves all settings and user information
     */
    private void save() {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("sound", mSoundOn);
        editor.putBoolean("music", mMusicOn);
        editor.apply();
    }

    /**
     * Tells fragments which custom onBackPressed to use when the back button is pressed
     */
    private void tellFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment f : fragments) {
            if (f != null) {
                /*
                if (f instanceof XFragment) {
                    ((XFragment) f).onBackPressed();
                }
                */
            }
        }
    }
}
