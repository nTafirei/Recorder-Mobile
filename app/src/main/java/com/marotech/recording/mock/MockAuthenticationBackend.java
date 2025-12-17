package com.marotech.recording.mock;

import com.marotech.recording.api.PasswordVerificationRequest;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.callbacks.RemoteServiceCallback;

import java.util.UUID;

public class MockAuthenticationBackend {

    private RemoteServiceCallback callback;

    public MockAuthenticationBackend(RemoteServiceCallback callback) {
        this.callback = callback;
    }

    public void authenticate(PasswordVerificationRequest request) {
        ServiceResponse response = new ServiceResponse();
        response.setToken(UUID.randomUUID().toString());
        callback.onObjectsFetched(response);
    }
}
