package com.marotech.recording.fragments;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.marotech.recording.R;
import com.marotech.recording.util.StringUtils;

public class StatusFragment extends Fragment {

    private String message;
    private String link;

    public StatusFragment(String message) {
        this.message = message;
    }

    public StatusFragment(String message, String link) {
        this.message = message;
        this.link = link;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_status, container, false);
        TextView messageView = root.findViewById(R.id.statusMessage);
        TextView linkView = root.findViewById(R.id.textViewLink);
        messageView.setText(message);
        if (!StringUtils.isBlank(link)) {
            linkView.setMovementMethod(LinkMovementMethod.getInstance());
            linkView.setText(Html.fromHtml(link, Html.FROM_HTML_MODE_LEGACY));
        }
        return root;
    }
}