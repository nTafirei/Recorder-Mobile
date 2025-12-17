package com.marotech.recording.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.marotech.recording.R;
import com.marotech.recording.model.Constants;
import com.marotech.recording.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseFragment extends Fragment {

    protected Context context;

    public BaseFragment(Context context) {
        if (context == null) {
            Log.e(TAG, "Null context found while creating fragment");
            System.exit(0);
        }
        this.context = context;
    }

    protected String formatDate(LocalDateTime date) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm");
        return fmt.format(date);
    }

    protected void updateSession() {

        SharedPreferences sessionPrefs = context.getSharedPreferences(Constants.SESSION_PREFS, Context.MODE_PRIVATE);
        if (sessionPrefs == null) {
            return;
        }
        String sessionToken = sessionPrefs.getString(Constants.SESSION_TOKEN, null);
        SharedPreferences.Editor editor = sessionPrefs.edit();

        if (StringUtils.isBlank(sessionToken)) {
            clearSession();
            editor.apply();
        } else if (!isSessionValid()) {
            clearSession();
            editor.apply();
        } else {
            String ttlStr = context.getString(R.string.session_ttl);
            long ttl = new Long(ttlStr);
            long sessionExpiryTime = System.currentTimeMillis() + ttl;
            editor.putLong(Constants.SESSION_EXP_TIME, sessionExpiryTime);
            editor.apply();
        }
    }

    protected void clearSession() {

        SharedPreferences sessionPrefs = context.getSharedPreferences(Constants.SESSION_PREFS, Context.MODE_PRIVATE);
        if (sessionPrefs == null) {
            return;
        }
        SharedPreferences.Editor editor = sessionPrefs.edit();
        editor.putLong(Constants.SESSION_EXP_TIME, 0L);
        editor.putString(Constants.SESSION_TOKEN, null);
        editor.apply();
    }

    protected String retrieveSessionToken() {
        SharedPreferences sessionPrefs = context.getSharedPreferences(Constants.SESSION_PREFS, Context.MODE_PRIVATE);
        if (sessionPrefs == null) {
            return null;
        }
        return sessionPrefs.getString(Constants.SESSION_TOKEN, null);
    }

    protected void putSession(String token) {
        SharedPreferences sessionPrefs = context.getSharedPreferences(Constants.SESSION_PREFS, Context.MODE_PRIVATE);
        if (sessionPrefs == null) {
            return;
        }
        if (token == null) {
            return;
        }
        SharedPreferences.Editor editor = sessionPrefs.edit();
        String ttlStr = context.getString(R.string.session_ttl);
        long ttl = new Long(ttlStr);
        long sessionExpiryTime = System.currentTimeMillis() + ttl;
        editor.putLong(Constants.SESSION_EXP_TIME, sessionExpiryTime);
        editor.putString(Constants.SESSION_TOKEN, token);
        editor.apply();
    }

    protected void sendToStatusPageWithLink(String message, String link) {
        StatusFragment statusFragment = new StatusFragment(message, link);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, statusFragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    protected void sendToStatusPage(String message) {
        StatusFragment statusFragment = new StatusFragment(message);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, statusFragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    protected boolean validate(EditText field, TextView label, String fieldName, StringBuilder message) {
        if (field.getText() == null || field.getText().toString().trim().length() == 0) {
            label.setTextColor(Color.parseColor("#E9967A"));
            message.append(fieldName + " was found empty\n");
            return false;
        }
        label.setTextColor(Color.parseColor("#ff009688"));
        return true;
    }

    protected boolean isSessionValid() {

        SharedPreferences sessionPrefs = context.getSharedPreferences(Constants.SESSION_PREFS, Context.MODE_PRIVATE);
        Long expiryTime = sessionPrefs.getLong(Constants.SESSION_EXP_TIME, 0);
        if (expiryTime == null || System.currentTimeMillis() > expiryTime) {
            clearSession();
            return false;
        }
        if (sessionPrefs.getString(Constants.SESSION_TOKEN, null) == null) {
            clearSession();
            return false;
        }

        return true;
    }

    protected void sendToLoginPage(Fragment lastVisistedFragment) {
        LoginFragment homeFragment = new LoginFragment(context, lastVisistedFragment);
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, homeFragment).commit();
    }

    private final String TAG = "paystream_BaseFragment";
}
