package com.marotech.recording.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.marotech.recording.R;

public class LogoutFragment extends BaseFragment {
    public LogoutFragment(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        clearSession();
        sendToStatusPage(getString(R.string.logged_out));
        return null;
    }
}