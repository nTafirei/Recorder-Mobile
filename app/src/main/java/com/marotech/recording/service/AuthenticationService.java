package com.marotech.recording.service;

import com.marotech.recording.R;
import com.marotech.recording.api.HttpCode;
import com.marotech.recording.api.PasswordVerificationRequest;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.fragments.LoginFragment;
import com.marotech.recording.mock.MockAuthenticationBackend;
import com.marotech.recording.model.Platform;
import com.marotech.recording.tasks.RemoteServiceTask;

public class AuthenticationService extends BaseService {
    private LoginFragment fragment;

    public AuthenticationService(LoginFragment fragment) {
        this.fragment = fragment;
    }

    public void authenticate(PasswordVerificationRequest request) {

        String jsonString = GSON.toJson(request);
        RemoteServiceTask task = new RemoteServiceTask(fragment, jsonString);

        if (fragment == null || fragment.getActivity() == null) {
            ServiceResponse response = new ServiceResponse();
            response.setCode(HttpCode.BAD_REQUEST);
            response.setMessage("activity is null");
            task.getCallback().onObjectsFetched(response);
            return;
        }
        if (request == null) {
            ServiceResponse response = new ServiceResponse();
            response.setCode(HttpCode.BAD_REQUEST);
            response.setMessage("PasswordVerificationRequest is null");
            task.getCallback().onObjectsFetched(response);
            return;
        }
        Platform platform = Platform.valueOf(fragment.getActivity().getString(R.string.app_platform));
        if (platform == Platform.LOCAL) {
            new MockAuthenticationBackend(fragment).authenticate(request);
        } else {
            String url = fragment.getActivity().getString(R.string.auth_service_url);
            task.execute(url);
        }
    }
}
