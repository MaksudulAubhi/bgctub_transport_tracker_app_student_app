package com.example.bgctub_transport_tracker_studentapp.ui.transport_info;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bgctub_transport_tracker_studentapp.R;

public class TransportInformationFragment extends Fragment {

    private TransportInformationViewModel mViewModel;

    public static TransportInformationFragment newInstance() {
        return new TransportInformationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.transport_information_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TransportInformationViewModel.class);
        // TODO: Use the ViewModel
    }

}