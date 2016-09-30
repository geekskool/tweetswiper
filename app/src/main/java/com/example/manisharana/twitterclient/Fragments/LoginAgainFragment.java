package com.example.manisharana.twitterclient.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.manisharana.twitterclient.Activities.MainActivity;
import com.example.manisharana.twitterclient.R;

public class LoginAgainFragment extends Fragment implements View.OnClickListener {

    private Button loginAgainLater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_login_again, container, false);
        loginAgainLater = (Button) rootView.findViewById(R.id.login_again);
        loginAgainLater.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v==loginAgainLater){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }
}
