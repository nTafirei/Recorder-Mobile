package com.marotech.recording.callbacks;

import android.app.Activity;

import com.marotech.recording.api.ServiceResponse;

public interface RemoteServiceCallback {

    void onObjectsFetched(ServiceResponse serviceResponse);
    Activity getActivity();
}
