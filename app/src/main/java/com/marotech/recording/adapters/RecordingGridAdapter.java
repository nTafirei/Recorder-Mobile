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
    private OnBuyClickListener buyClickListener;

    public RecordingGridAdapter(Context context, List<RecordingDTO> recordings, OnBuyClickListener listener) {
        this.context = context;
        this.recordings = recordings;
        this.buyClickListener = listener;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_recording, parent, false);
            holder = new ViewHolder();
            holder.recordingBtnPlay = convertView.findViewById(R.id.recordingBtnPlay);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final RecordingDTO recording = getItem(position);
        holder.nameTextView.setText(recording.getName());

        holder.recordingBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buyClickListener != null) {
                    buyClickListener.onBuyClick(recording);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView;
        Button recordingBtnPlay;
    }

    // Interface for callback
    public interface OnBuyClickListener {
        void onBuyClick(RecordingDTO recording);
    }
}
