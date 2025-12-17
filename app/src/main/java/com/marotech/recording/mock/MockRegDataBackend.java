package com.marotech.recording.mock;

import com.marotech.recording.api.ResponseType;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.callbacks.RemoteServiceCallback;
import com.marotech.recording.model.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockRegDataBackend {

    private RemoteServiceCallback callback;

    public MockRegDataBackend(RemoteServiceCallback callback) {
        this.callback = callback;
    }

    public void fetchRegData() {

        ServiceResponse response = new ServiceResponse();
        response.setToken(UUID.randomUUID().toString());

        response.setResponseType(ResponseType.REG_RESPONSE);
        response.getAdditionalInfo().put(Constants.SEC_QUESTION_1, "What is your height?");
        response.getAdditionalInfo().put(Constants.SEC_QUESTION_2, "What is your weight?");
        response.getAdditionalInfo().put(Constants.SEC_QUESTION_3, "Where do you come from?");
        callback.onObjectsFetched(response);
    }
}