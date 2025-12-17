package com.marotech.recording.service;

import com.marotech.recording.R;
import com.marotech.recording.api.HttpCode;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.callbacks.RemoteServiceCallback;
import com.marotech.recording.mock.MockRegDataBackend;
import com.marotech.recording.model.Platform;
import com.marotech.recording.tasks.RemoteServiceTask;

public class RegDataService extends BaseService {

    private RemoteServiceCallback callback;

    public RegDataService(RemoteServiceCallback callback) {
        this.callback = callback;
    }

    public void fetchRegData() {

        RemoteServiceTask task = new RemoteServiceTask(callback, null);

        if (callback == null || callback.getActivity() == null) {
            ServiceResponse response = new ServiceResponse();
            response.setCode(HttpCode.BAD_REQUEST);
            response.setMessage("activity is null");
            task.getCallback().onObjectsFetched(response);
            task.getCallback().onObjectsFetched(response);
            return;
        }

        Platform platform = Platform.valueOf(callback.getActivity().getString(R.string.app_platform));
        if (platform == Platform.LOCAL) {
            new MockRegDataBackend(callback).fetchRegData();
        } else {

            String url = callback.getActivity().getString(R.string.reg_data_service_url);
            task.execute(url);
        }
    }
}
