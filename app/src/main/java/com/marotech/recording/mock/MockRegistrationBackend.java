package com.marotech.recording.mock;

import com.marotech.recording.api.RegisterRequest;
import com.marotech.recording.api.ResponseType;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.callbacks.RemoteServiceCallback;

import java.util.UUID;

public class MockRegistrationBackend {

    private RemoteServiceCallback callback;

    public MockRegistrationBackend(RemoteServiceCallback callback) {
        this.callback = callback;
    }

    public void register(RegisterRequest request) {
        ServiceResponse response = new ServiceResponse();
        response.setToken(UUID.randomUUID().toString());
        response.setResponseType(ResponseType.REG_RESPONSE);
        response.setMessage("You have been registered. Please approach one of our agents for ID verification");
        callback.onObjectsFetched(response);
    }
}
