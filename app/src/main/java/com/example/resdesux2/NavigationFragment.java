package com.example.resdesux2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.resdesux2.Activities.DashboardActivity;
import com.example.resdesux2.Activities.Info;
import com.example.resdesux2.Activities.MainActivity;
import com.example.resdesux2.Models.BottomNavigate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavigationFragment extends Fragment {

    public enum Options {
        Friends,
        Home,
        Insight;
    }
    private static final String ARG_CURRENT = "param1";

    private Options current = Options.Home;
    private final BottomNavigate bottomNavigate;



    public NavigationFragment(BottomNavigate bottomNavigate) {
        // Required empty public constructor
        this.bottomNavigate = bottomNavigate;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentOption The selected option.
     * @return A new instance of fragment NavigationFragment.
     */
    public static NavigationFragment newInstance(Options currentOption, BottomNavigate bottomNavigate) {
        NavigationFragment fragment = new NavigationFragment(bottomNavigate);
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT, currentOption.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            current = Options.values()[(getArguments().getInt(ARG_CURRENT))];
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView friendsBtn = view.findViewById(R.id.FriendsNavBtn);
        ImageView homeBtn = view.findViewById(R.id.HomeNavBtn);
        ImageView insightBtn = view.findViewById(R.id.InsightNavBtn);

        friendsBtn.setOnClickListener((View v) -> optionClicked(Options.Friends));
        homeBtn.setOnClickListener((View v) -> optionClicked(Options.Home));
        insightBtn.setOnClickListener((View v) -> optionClicked(Options.Insight));

        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true);
        int color = typedValue.data;

        switch (current) {
            case Friends:
                friendsBtn.setColorFilter(color);
                break;
            case Home:
                homeBtn.setColorFilter(color);
                break;
            case Insight:
                insightBtn.setColorFilter(color);
                break;
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    private void optionClicked(Options option) {
        if (current == option) return;

        switch (option) {
            case Friends:
                bottomNavigate.navigateTo(DashboardActivity.class);
                break;
            case Home:
                bottomNavigate.navigateTo(MainActivity.class);
                break;
            case Insight:
                bottomNavigate.navigateTo(Info.class);
                break;
        }
    }
}