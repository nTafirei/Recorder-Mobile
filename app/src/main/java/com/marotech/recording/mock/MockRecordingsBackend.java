package com.marotech.recording.mock;

import com.marotech.recording.api.RecordingDTO;
import com.marotech.recording.api.RecordingsRequest;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.callbacks.RemoteServiceCallback;
import com.marotech.recording.model.Constants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockRecordingsBackend {

    private RemoteServiceCallback callback;

    public MockRecordingsBackend(RemoteServiceCallback callback) {
        this.callback = callback;
    }

    public void fetchProducts(RecordingsRequest request) {

        ServiceResponse response = new ServiceResponse();
        response.setToken(UUID.randomUUID().toString());
        List<RecordingDTO> list = new ArrayList<>();
        int max = request.getPage().getItemsPerPage();

        for (int i = 0; i < max; i++) {
            RecordingDTO product = new RecordingDTO();
            product.setId(UUID.randomUUID().toString());
            product.setName("Recording " + i);
            list.add(product);
        }
        response.getAdditionalInfo().put(Constants.RECORDINGS, list);
        callback.onObjectsFetched(response);
    }
}