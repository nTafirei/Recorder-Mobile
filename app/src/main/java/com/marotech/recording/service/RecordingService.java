package com.marotech.recording.service;

import android.util.Log;

import com.marotech.recording.R;
import com.marotech.recording.api.HttpCode;
import com.marotech.recording.api.RecordingsRequest;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.fragments.RecordingsFragment;
import com.marotech.recording.mock.MockRecordingsBackend;
import com.marotech.recording.model.Platform;
import com.marotech.recording.tasks.RemoteServiceTask;

public class RecordingService extends BaseService {
    private RecordingsFragment fragment;

    public RecordingService(RecordingsFragment fragment) {
        this.fragment = fragment;
    }


    public void fetchProducts(RecordingsRequest request) {
        try {
            String jsonString = GSON.toJson(request);
            RemoteServiceTask task = new RemoteServiceTask(fragment, jsonString);
            if (fragment == null || fragment.getActivity() == null) {
                ServiceResponse response = new ServiceResponse();
                response.setCode(HttpCode.BAD_REQUEST);
                response.setMessage("activity is null");
                task.getCallback().onObjectsFetched(response);
                return;
            }

            Platform platform = Platform.valueOf(fragment.getActivity().getString(R.string.app_platform));
            if (platform == Platform.LOCAL) {
                new MockRecordingsBackend(fragment).fetchProducts(request);
            } else {
                String url = fragment.getActivity().getString(R.string.recordings_service_url);
                task.execute(url);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private final String TAG = "recorder_RecordingService";
}
