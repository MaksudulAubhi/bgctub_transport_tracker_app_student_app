package com.example.bgctub_transport_tracker_studentapp.ui.tipsActivity.tips3;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.bgctub_transport_tracker_studentapp.R;
import com.example.bgctub_transport_tracker_studentapp.SigninActivity;
import com.example.bgctub_transport_tracker_studentapp.ui.tipsActivity.tips1.Tips1Fragment;

public class Tips3Fragment extends Fragment implements View.OnClickListener {

    private Tips3ViewModel mViewModel;
    private Button nextButton;

    public static Tips3Fragment newInstance() {
        return new Tips3Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tips3_fragment, container, false);

        nextButton = root.findViewById(R.id.tip3NextButton);
        nextButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(Tips3ViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        try {
            if (v == nextButton) {
                //goto signin activity
                startActivity(new Intent(getActivity(), SigninActivity.class));
                getActivity().finish();
            }
        } catch (Exception exception) {
            //give error message
            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}