package com.marotech.recording.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.marotech.recording.R;
import com.marotech.recording.adapters.RecordingGridAdapter;
import com.marotech.recording.api.HttpCode;
import com.marotech.recording.api.Page;
import com.marotech.recording.api.RecordingDTO;
import com.marotech.recording.api.RecordingsRequest;
import com.marotech.recording.api.ServiceResponse;
import com.marotech.recording.callbacks.RemoteServiceCallback;
import com.marotech.recording.model.Constants;
import com.marotech.recording.service.RecordingService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class RecordingsFragment extends BaseFragment implements RemoteServiceCallback,
        RecordingGridAdapter.OnBuyClickListener {

    private ListView listView;
    private List<RecordingDTO> products = new ArrayList<>();
    private RecordingGridAdapter adapter;

    public RecordingsFragment(Context context) {
        super(context);
        adapter = new RecordingGridAdapter(this.context, products, this);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        updateSession();
        final View root = inflater.inflate(R.layout.fragment_recordings, container, false);
        listView = root.findViewById(R.id.recordingsListView);
        listView.setAdapter(adapter);
        setupClickListener(listView);
        RecordingService service = new RecordingService(this);
        RecordingsRequest request = new RecordingsRequest();
        request.setPage(new Page());
        service.fetchProducts(request);
        return root;
    }

    @Override
    public void onBuyClick(RecordingDTO product) {
        RecordingsFragment purchaseFragment = new RecordingsFragment(context);
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, purchaseFragment).commit();
    }

    private void setupClickListener(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecordingDTO clickedProduct = adapter.getItem(position);
                RecordingsFragment purchaseFragment = new RecordingsFragment(context);
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, purchaseFragment).commit();
            }
        });
    }

    @Override
    public void onObjectsFetched(ServiceResponse serviceResponse) {

        if (serviceResponse == null || serviceResponse.getCode() != HttpCode.OK) {
            Log.d(TAG, "Error fetching products: " + serviceResponse.getMessage());
            sendToStatusPage("Error fetching products: " + serviceResponse.getMessage());
            return;
        }

        try {
            Collection<? extends RecordingDTO> col =
                    (Collection<? extends RecordingDTO>) serviceResponse.getAdditionalInfo().get(Constants.RECORDINGS);
            if (col == null || col.isEmpty()) {
                sendToStatusPage("No products were found");
                return;
            }

            products.addAll(col);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            sendToStatusPage("Error fetching products: " + serviceResponse.getMessage());
            return;
        }
    }

    private final String TAG = "recorder_ProductsFragment";
}
