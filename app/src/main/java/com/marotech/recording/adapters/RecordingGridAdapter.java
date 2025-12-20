package com.marotech.recording.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.marotech.recording.R;
import com.marotech.recording.api.RecordingDTO;

import java.util.List;

public class RecordingGridAdapter extends BaseAdapter {
    private Context context;
    private List<RecordingDTO> recordings;
    private OnRecordingsClickListener recordingsClickListener;

    public RecordingGridAdapter(Context context, List<RecordingDTO> recordings, OnRecordingsClickListener listener) {
        this.context = context;
        this.recordings = recordings;
        this.recordingsClickListener = listener;
    }

    @Override
    public int getCount() {
        return recordings.size();
    }

    @Override
    public RecordingDTO getItem(int position) {
        return recordings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item_recording, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = view.findViewById(R.id.recordingNameTextView);
            holder.recordingLocation = view.findViewById(R.id.recordingLocation);
            holder.recordingBtnPlay = view.findViewById(R.id.recordingBtnPlay);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final RecordingDTO recording = getItem(position);
        holder.nameTextView.setText(recording.getName());
        holder.recordingLocation.setText(recording.getDeviceLocation());

        holder.recordingBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordingsClickListener != null) {
                    recordingsClickListener.OnRecordings(recording);
                }
            }
        });

        return view;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView recordingLocation;
        Button recordingBtnPlay;
    }

    // Interface for callback
    public interface OnRecordingsClickListener {
        void OnRecordings(RecordingDTO recording);
    }
}
