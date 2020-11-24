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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import io.mtksdk.inappsurvey.R;
import io.mtksdk.inappsurvey.converter.Question;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by kgy 2019. 9. 24.
 */


public class SdkOpenEndShortFragment extends BaseFragment implements TextWatcher, View.OnClickListener {
    private static final String TAG = SdkOpenEndShortFragment.class.getSimpleName();



    private LinearLayout placeholder;
    private LinearLayout answerForm;
    private ArrayList<LinearLayout> answerForms;
    private ArrayList<EditText> answers;


    private ScrollView scrollView;
    private LinearLayout textAnswerContainer;
    private ImageButton replaceConditionButton;
    private ImageButton addAnswer;
    private View view;
    private HashMap<String, ArrayList<Object>> answerMap;

    protected int getShortForm;
    protected  int getMaxShort;
    private Question question;



    public static SdkOpenEndShortFragment getInstance(Question question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkOpenEndShortFragment openEndFragment = new SdkOpenEndShortFragment();
        Bundle args = new Bundle();
        args.putSerializable("questionClass", question);
        args.putSerializable("answerMap", answerMap);
        openEndFragment.setArguments(args);
        return openEndFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        answerMap = (HashMap<String, ArrayList<Object>>) getArguments().getSerializable("answerMap");
        getShortForm = (int) question.getRule().get("shortFormCount");
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

        handleAnswerForm(getShortForm);

        tvLabel.setText(getQuestionText());
        return view;
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
                        e.setOnFocusChangeListener(answerFocusListener);
                        answers.add(e);
                    }

                }
            }
        }

        return answerForm;
    }

    public boolean isShortForm() {
        return question.getIsShortForm();
    }

    public String getQuestionText() {
        return question.getText();
    }

    public String getSubItemId() {
        return question.getQuestionId();
    }

    @Override
    public void onResume(){
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean validate() {

        hideKeyboard();
        for (EditText e : answers) {
            Log.i("getText()", e.getText().toString());
        }

        int validEditTextCount = 0;
        for (EditText e : answers) {
            if (!TextUtils.isEmpty(e.getText())) {
                validEditTextCount++;
            }
        }
        if (validEditTextCount < getShortForm) {
            Toast.makeText(getActivity(), "Please answer first", Toast.LENGTH_SHORT).show();
            return false;
        }

        ArrayList<Object> finalAnswer = new ArrayList<>();
        for (EditText e : answers) {
            finalAnswer.add(e.getText().toString());
            e.setText("");
        }
        question.setAnswerMap(finalAnswer);
        return true;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {}

    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
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

    private void handleAnswerForm(int minCount){
        if(answerForms == null){
            answerForms = new ArrayList<>();
        }

        if(answerForms.size() == 0 && minCount > 0){    // exist minimum short form count
            for(int i = 0; i < minCount; i++){
                addAnswerForm();
            }
        }
        scrollView.invalidate();
        scrollView.requestLayout();
    }

    private void createAnswerPlaceholder(){
        placeholder = (LinearLayout)getLayoutInflater().from(getActivity()).inflate(R.layout.sdk_open_end_answer_placeholder, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) convertDpToPixel(getActivity(), 19);
        placeholder.setLayoutParams(params);
        placeholder.findViewById(R.id.question_open_end_answer_placeholder_container).setOnClickListener(this);
    }

    @Override
    public String getType() {
        return question.getQuestionType();
    }

}

