package com.marotech.recording.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.marotech.recording.api.HttpCode;
import com.marotech.recording.api.RecordingDTO;
import com.marotech.recording.api.ResponseType;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.callbacks.RemoteServiceCallback;
import com.marotech.recording.gson.CustomExclusionStrategy;
import com.marotech.recording.gson.LocalDateAdapter;
import com.marotech.recording.gson.LocalDateTimeAdapter;
import com.marotech.recording.model.Constants;
import com.marotech.recording.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RemoteServiceTask extends AsyncTask<String, Void, String> {

    protected static final Gson GSON = new GsonBuilder()
            .setExclusionStrategies(new CustomExclusionStrategy())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();
    private String jsonRequestBody;
    private RemoteServiceCallback callback;
    private static final String TAG = "recorder_RemoteServiceTask";

    public RemoteServiceTask(RemoteServiceCallback callback, String jsonRequestBody) {
        this.callback = callback;
        this.jsonRequestBody = jsonRequestBody;
    }

    @Override
    protected String doInBackground(String... urls) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urls[0]);

            Log.e(TAG, "NOW CALLING URL " + url);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            byte[] postDataBytes = null;
            if (!StringUtils.isBlank(jsonRequestBody)) {
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);
                postDataBytes = jsonRequestBody.getBytes("UTF-8");
                conn.setFixedLengthStreamingMode(postDataBytes.length);
            }
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);

            conn.connect();
            if (postDataBytes != null) {
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(postDataBytes);
                    os.flush();
                }
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
                if (inputStream == null) {
                    // In case error stream is null, fallback to empty string
                    Log.e(TAG, "Error stream is null for response code: " + responseCode);
                    return "";
                }
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error running task", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result.toString();
    }

    public RemoteServiceCallback getCallback() {
        return callback;
    }

    @Override
    protected void onPostExecute(String jsonString) {
        super.onPostExecute(jsonString);

        // Log.e(TAG, "JSON IS " + jsonString);
        if (StringUtils.isBlank(jsonString)) {
            ServiceResponse response = new ServiceResponse();
            response.setCode(HttpCode.INTERNAL_SERVER_ERROR);
            response.setMessage("There was a problem servicing the request. Perhaps the remote server is down");
            callback.onObjectsFetched(response);
            return;
        }
        ServiceResponse response = GSON.fromJson(jsonString, ServiceResponse.class);
        ResponseType responseType = response.getResponseType();
        if (ResponseType.REG_DATA == responseType) {
        } else if (ResponseType.RECORDINGS == responseType) {
            String objStr = (String) response.getAdditionalInfo().get(Constants.RECORDINGS);

            Type objListType = new TypeToken<List<RecordingDTO>>() {
            }.getType();
            List<RecordingDTO> list = GSON.fromJson(objStr, objListType);
            response.getAdditionalInfo().put(Constants.RECORDINGS, list);
        }
        callback.onObjectsFetched(response);
    }
}
