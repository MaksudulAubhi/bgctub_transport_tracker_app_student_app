package com.example.bgctub_transport_tracker_studentapp.ui.tipsActivity.tips0;

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

public class Tips0Fragment extends Fragment implements View.OnClickListener{

    private Tips0ViewModel mViewModel;
    private Button cancelButton;
    private Button nextButton;

    public static Tips0Fragment newInstance() {
        return new Tips0Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tips0_fragment, container, false);

        cancelButton = root.findViewById(R.id.tip0CancelButton);
        nextButton = root.findViewById(R.id.tip0NextButton);

        cancelButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(Tips0ViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        try {
            if (v == cancelButton) {
                //goto signin activity
                startActivity(new Intent(getActivity(), SigninActivity.class));
                getActivity().finish();
            }
            if (v == nextButton) {
                // goto tips1 fragment
                Tips1Fragment tips1Fragment = new Tips1Fragment();
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, tips1Fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

        } catch (Exception exception) {
            //give error message
            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}