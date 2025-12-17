package com.marotech.recording.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.marotech.recording.R;
import com.marotech.recording.api.HttpCode;
import com.marotech.recording.api.PasswordVerificationRequest;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.callbacks.RemoteServiceCallback;
import com.marotech.recording.service.AuthenticationService;

public class LoginFragment extends BaseFragment implements RemoteServiceCallback {

    private Fragment lastVisitedPage;

    public LoginFragment(Context context, Fragment lastVisitedPage) {
        super(context);
        this.lastVisitedPage = lastVisitedPage;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        clearSession();
        final View root = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText mobile_number_field = root.findViewById(R.id.mobile_number_field);
        final EditText home_password_field = root.findViewById(R.id.home_password_field);
        final TextView mobile_label = root.findViewById(R.id.mobile_number_field_label);
        final TextView home_password_field_label = root.findViewById(R.id.mobile_number_field_label);

        Button clearButton = root.findViewById(R.id.homeBtnClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mobile_number_field.setText("");
                home_password_field.setText("");
            }
        });

        final LoginFragment fragment = this;

        Button logInButton = root.findViewById(R.id.btnLogin);
        logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean validated = true;

                StringBuilder message = new StringBuilder();
                if (!validate(mobile_number_field, mobile_label, "mobileNumber", message)) {
                    validated = false;
                }
                if (!validate(home_password_field, home_password_field_label, "password", message)) {
                    validated = false;
                }

                if (!validated) {
                    Log.e(TAG, message.toString());
                }
                if (validated) {
                    AuthenticationService service = new AuthenticationService(fragment);
                    PasswordVerificationRequest request = new PasswordVerificationRequest();
                    request.setMobileNumber(mobile_number_field.getText().toString());
                    request.setPassword(home_password_field.getText().toString());
                    Log.e(TAG,"Calling login service with: " + request);
                    service.authenticate(request);
                }
            }
        });
        return root;
    }

    @Override
    public void onObjectsFetched(ServiceResponse response) {

        Log.e(TAG,"Response is : " + response);

        if (response.getCode() == HttpCode.OK) {
            String token = response.getToken();
            putSession(token);
            if (lastVisitedPage == null) {
                lastVisitedPage = new RecordingsFragment(context);
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, lastVisitedPage).addToBackStack(null);
            fragmentTransaction.commit();
            return;
        }
        sendToStatusPage("Failed to login: " + response.getMessage());
    }

    final String TAG = "paystream_LoginFragment";
}
