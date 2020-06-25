package io.mtksdk.inappsurvey.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.mtksdk.inappsurvey.R;
import io.mtksdk.inappsurvey.ViewControllerManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kgy 2019. 9. 24.
 */

public abstract class BaseFragment extends Fragment {
    protected ViewControllerManager activity;
    protected HashMap<String, ArrayList<Object>> answerMap;
    protected int position;

    /*@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater infalter, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = infalter.inflate(R.layout.sdk_activity_question, container, false);
        return view;
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (ViewControllerManager) getActivity();
        this.answerMap = (HashMap<String, ArrayList<Object>>)getArguments().getSerializable("answerMap");

    }

    public abstract String getType();

    public abstract boolean validate();

    public abstract void skipped();

    public void init(@Nullable ArrayList<Object> savedAnswer) {

    }
}
