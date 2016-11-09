package com.example.manisharana.twitterclient.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.manisharana.twitterclient.R;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.UnsupportedEncodingException;

public class ComposeTweetFragment extends Fragment implements View.OnClickListener, TextWatcher {

    private static final int CHAR_COUNT = 140;
    private static final String TAG = ComposeTweetFragment.class.getSimpleName();
    private EditText tweetText;
    private Button sendTweetButton;
    private TextView charCountView;
    private TextView errorMsgView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_compose_new_tweet, container, false);
        tweetText = (EditText) rootView.findViewById(R.id.edit_text_compose_tweet);
        tweetText.addTextChangedListener(this);
        errorMsgView = (TextView) rootView.findViewById(R.id.text_view_error_message);
        charCountView = (TextView) rootView.findViewById(R.id.text_view_char_count);
        sendTweetButton = (Button) rootView.findViewById(R.id.tw__compose_tweet_button);
        sendTweetButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v == sendTweetButton){
            String inputText = tweetText.getText().toString();
            if(inputText.length()>0 && !inputText.isEmpty()){
                try {
                    String encodedString = new String(inputText.getBytes("UTF-8"), "UTF-8");
                    Twitter.getApiClient().getStatusesService().update(encodedString, null, true, null, null, null, false, true, null, new Callback<Tweet>() {
                        @Override
                        public void success(Result<Tweet> result) {
                            tweetText.setText("");
                            errorMsgView.setVisibility(View.VISIBLE);
                            errorMsgView.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_green_light));
                            errorMsgView.setText(getActivity().getString(R.string.tweet_posted_successfully));
                            //clear the data (text/ image) and set text message successful
                        }
                        @Override
                        public void failure(TwitterException exception) {
                            errorMsgView.setVisibility(View.VISIBLE);
                            errorMsgView.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_red_dark));
                            errorMsgView.setText(getActivity().getString(R.string.error_in_posting_tweet));

                            Log.i(TAG,"Error in posting tweet"+exception.getMessage());

                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    errorMsgView.setVisibility(View.VISIBLE);
                    errorMsgView.setTextColor(ContextCompat.getColor(getActivity(), R.color.holo_red_dark));
                    errorMsgView.setText(getActivity().getString(R.string.error_in_encoding));

                    Log.i(TAG,"Error in encoding tweet text"+e.getMessage());

                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String inputText = tweetText.getText().toString();
        int length = inputText.length();
        charCountView.setText(String.valueOf(CHAR_COUNT-length));
        if(length>0 && !inputText.isEmpty())
            sendTweetButton.setEnabled(true);
        else
        sendTweetButton.setEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
