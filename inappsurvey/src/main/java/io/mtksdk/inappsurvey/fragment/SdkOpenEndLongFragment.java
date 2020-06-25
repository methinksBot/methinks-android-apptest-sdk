package io.mtksdk.inappsurvey.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.mtksdk.inappsurvey.R;
import io.mtksdk.inappsurvey.converter.Question;

public class SdkOpenEndLongFragment extends BaseFragment implements TextWatcher, View.OnClickListener{

    private LinearLayout placeholder;
    private LinearLayout answerForm;
    private static ArrayList<LinearLayout> answerForms;
    private static ArrayList<EditText> answers;
    private HashMap<String, ArrayList<Object>> answerMap;
    private Question question;
    private View view;
    private ScrollView scrollView;
    private LinearLayout textAnswerContainer;



    public static SdkOpenEndLongFragment getInstance(Question question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkOpenEndLongFragment sdkOpenEndLongFragment = new SdkOpenEndLongFragment();
        Bundle args = new Bundle();
        args.putSerializable("questionClass", question);
        args.putSerializable("answerMap", answerMap);
        sdkOpenEndLongFragment.setArguments(args);
        return sdkOpenEndLongFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question = (Question) getArguments().getSerializable("questionClass");
        answerMap = (HashMap<String, ArrayList<Object>>) getArguments().getSerializable("answerMap");
        /*answer = new ArrayList<>();
        getLastesUpdate = new ArrayList<>();
        openAnswerMap = new HashMap<>();
        answerContainer = new HashMap<>();*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        answers = new ArrayList<>();
        answerForms = new ArrayList<>();
        view = inflater.inflate(R.layout.sdk_fragment_open_end, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.question_content);
        textAnswerContainer = view.findViewById(R.id.text_answer_container);
        scrollView = view.findViewById(R.id.scroll_view);
        placeholder = (LinearLayout) getLayoutInflater().from(getActivity()).inflate(R.layout.sdk_open_end_answer_placeholder, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) convertDpToPixel(getActivity(), 19);
        placeholder.setLayoutParams(params);

        handleAnswerForm(1, 1);
        answers.get(0).addTextChangedListener(this);
        tvLabel.setText(getQuestionText());
        return view;
    }

    private void handleAnswerForm(int minCount, int maxCount){
        if(answerForms == null){
            answerForms = new ArrayList<>();
        }

        if(minCount == 1 && maxCount == 1) { // single answer
            addAnswerForm();
        }
        scrollView.invalidate();
        scrollView.requestLayout();
    }

    private void addAnswerForm(){
        LinearLayout l = createAnswerForm();
        answerForms.add(l);
        textAnswerContainer.addView(l);
        for(int i = 0; i < l.getChildCount(); i++){
            if(l.getChildAt(i) instanceof LinearLayout){
                LinearLayout childLinearLayout = (LinearLayout)l.getChildAt(i);
                for(int j = 0; j < childLinearLayout.getChildCount(); j++){
                    if(childLinearLayout.getChildAt(j) instanceof EditText){
                        EditText child = (EditText)childLinearLayout.getChildAt(j);
                        child.requestFocus();
                        break;
                    }
                }
            }
        }
    }

    private LinearLayout createAnswerForm(){
        answerForm = (LinearLayout)getLayoutInflater().from(getActivity()).inflate(R.layout.sdk_open_end_answer_form, null);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) convertDpToPixel(getActivity(), 19);
        answerForm.setLayoutParams(params);
        for(int i = 0; i < answerForm.getChildCount(); i++){
            View child = answerForm.getChildAt(i);
            if(child instanceof LinearLayout){
                for(int j = 0; j < ((LinearLayout) child).getChildCount(); j++){
                    if(((LinearLayout) child).getChildAt(j) instanceof EditText){
                        EditText e = (EditText) ((LinearLayout) child).getChildAt(j);
                        if(isShortForm()){ // single line
                            e.setLines(1);
                            e.setMaxLines(1);
                            e.setSingleLine();
                        }else{  // multi line
                            e.setLines(3);
                            e.setMinLines(1);
                            e.setMaxLines(3);
                            e.setSingleLine(false);
                        }

                        // set the keyboard type
                        /*answerContainer.put(currPos, "");
                        currPos++;*/
                        e.setOnFocusChangeListener(answerFocusListener);
                        answers.add(e);
                    }
                }
            }
        }

        return answerForm;
    }

    View.OnFocusChangeListener answerFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            LinearLayout parent = (LinearLayout) v.getParent().getParent();
            for(int i = 0; i < parent.getChildCount(); i++){
                View child = parent.getChildAt(i);
                if(child.getId() == R.id.open_end_answer_underline){
                    if(hasFocus){
                        child.setBackgroundColor(getActivity().getResources().getColor(R.color.cornflower));
                    }else{
                        child.setBackgroundColor(getActivity().getResources().getColor(R.color.pale_grey_two));
                    }
                }
            }
        }
    };



    public boolean isShortForm() {
        return question.getIsShortForm();
    }

    public String getQuestionText() {
        return question.getText();
    }

    public String getSubItemId() {
        return question.getQuestionId();
    }

    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    @Override
    public boolean validate() {
        hideKeyboard();
        for (EditText e : answers) {
            Log.i("getText()", e.getText().toString());
        }

        EditText e = answers.get(0);
        if (TextUtils.isEmpty(e.getText().toString())) {
            Toast.makeText(getActivity(), "Please anwser first", Toast.LENGTH_SHORT).show();
            return false;
        }
        ArrayList<Object> finalAnswer = new ArrayList<>();
        for (EditText et : answers) {
            finalAnswer.add(et.getText().toString());
            et.setText("");
        }
        question.setAnswerMap(finalAnswer);
//        answerMap.put(getSubItemId(), finalAnswer);
        return true;
    }

    @Override
    public void onClick(View v) {
    }

    public void hideKeyboard() {
        if(answers != null && answers.size() > 0){
            for(EditText editText : answers){
                if(editText != null){
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
                }
            }
        }
    }

    @Override
    public void skipped(){
        answerMap.remove(getSubItemId());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void afterTextChanged(Editable s) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public String getType() {
        return question.getQuestionType();
    }

}

