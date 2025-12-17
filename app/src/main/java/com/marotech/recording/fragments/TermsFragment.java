package com.marotech.recording.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marotech.recording.R;


public class TermsFragment extends BaseFragment {

    public TermsFragment(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateSession();
        sendToStatusPageWithLink(getString(R.string.visit_terms), context.getString(R.string.terms_url));
        return null;
    }
}