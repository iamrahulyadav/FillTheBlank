package com.example.jeffwang.filltheblank;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment {

    public MainMenuFragment() {
        // Required empty public constructor
    }

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // read and initialize values from settings file
        // todo make/read from file, default values are on

        Button start = view.findViewById(R.id.btn_main_start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(GameFragment.newInstance());
            }
        });

        Button shop = view.findViewById(R.id.btn_main_shop);
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openFragment(ShopFragment.newInstance());
            }
        });

        Button settings = view.findViewById(R.id.btn_main_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openFragment(SettingsFragment.newInstance());
            }
        });
    }

    private void openFragment(Fragment f) {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_main_container, f)
                .addToBackStack(null)
                .commit();
    }
}
