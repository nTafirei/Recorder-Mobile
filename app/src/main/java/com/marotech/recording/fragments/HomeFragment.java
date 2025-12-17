package com.marotech.recording.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.marotech.recording.R;

public class HomeFragment extends BaseFragment {

    public HomeFragment(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        updateSession();
        CardView homeLoginView = root.findViewById(R.id.homeLoginCard);
        CardView homeRegisterCard = root.findViewById(R.id.homeRegisterCard);
        CardView homeSyncCard = root.findViewById(R.id.homeSyncCard);
        CardView homeRecordingsCard = root.findViewById(R.id.homeRecordingsCard);
        CardView homeHelpCard = root.findViewById(R.id.homeHelpCard);
        CardView homePointsCard = root.findViewById(R.id.homePointsCard);

        homeLoginView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginFragment loginFragment = new LoginFragment(context, null);
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, loginFragment).commit();
            }
        });

        homeRegisterCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RegisterFragment registerFragment =
                        new RegisterFragment(context);
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, registerFragment).commit();
            }
        });

        homeSyncCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LogoutFragment syncFragment = new LogoutFragment(context);
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, syncFragment).commit();
            }
        });

        homeRecordingsCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RecordingsFragment recordingsFragment = new RecordingsFragment(context);
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, recordingsFragment).commit();
            }
        });

        homeHelpCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HelpFragment helpFragment = new HelpFragment(context);
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, helpFragment).commit();
            }
        });
    }
}
