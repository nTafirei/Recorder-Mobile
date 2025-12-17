package com.marotech.recording.service;

import com.marotech.recording.R;
import com.marotech.recording.api.HttpCode;
import com.marotech.recording.api.RegisterRequest;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.fragments.RegisterFragment;
import com.marotech.recording.mock.MockRegistrationBackend;
import com.marotech.recording.model.Platform;
import com.marotech.recording.tasks.RemoteServiceTask;

public class RegistrationService extends BaseService {
    private RegisterFragment fragment;

    public RegistrationService(RegisterFragment fragment) {
        this.fragment = fragment;
    }

    public void register(RegisterRequest request) {

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
            response.setMessage("RegisterRequest is null");
            task.getCallback().onObjectsFetched(response);
            return;
        }
        Platform platform = Platform.valueOf(fragment.getActivity().getString(R.string.app_platform));
        if (platform == Platform.LOCAL) {
            new MockRegistrationBackend(fragment).register(request);
        } else {
            String url = fragment.getActivity().getString(R.string.register_service_url);
            task.execute(url);
        }
    }
}
