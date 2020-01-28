package io.methinks.android.mtkapptestrsdk.question.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

import io.methinks.mtkpatchersdk.question.ViewControllerManager;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            setRetainInstance(true);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = (ViewControllerManager) getActivity();
        this.answerMap = (HashMap<String, ArrayList<Object>>)getArguments().getSerializable("answerMap");

    }

    public abstract boolean validate();

    public abstract void skipped();

    public void init(@Nullable ArrayList<Object> savedAnswer) {

    }
}
