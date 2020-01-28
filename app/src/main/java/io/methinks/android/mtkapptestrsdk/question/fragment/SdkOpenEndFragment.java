package io.methinks.android.mtkapptestrsdk.question.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import io.methinks.mtkpatchersdk.R;


/**
 * Created by kgy 2019. 9. 24.
 */


public class SdkOpenEndFragment extends BaseFragment implements TextWatcher, View.OnClickListener {
    private static final String TAG = SdkOpenEndFragment.class.getSimpleName();


    private String title;
    private int page;
    private LinearLayout placeholder;
    private LinearLayout answerForm;
    private ArrayList<LinearLayout> answerForms;
    private ArrayList<EditText> answers;
    private boolean isTextAnswerState = true;
    protected ArrayList<Object> getLastesUpdate;


    private ScrollView scrollView;
    private LinearLayout textAnswerContainer;
    private TextView questionContent;
    private ImageButton replaceConditionButton;
    private ImageButton addAnswer;
    private View view;
    private Boolean isShortForm;
    private HashMap<String, ArrayList<Object>> answerMap;
    private ArrayList<Object> answer;
    protected JSONObject question;

    protected int getMinShort;
    protected  int getMaxShort;
    private String prev;
    private String curr;
    private HashSet<String> shortAnswerSet;


    public static SdkOpenEndFragment getInstance(JSONObject question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkOpenEndFragment openEndFragment = new SdkOpenEndFragment();
        Bundle args = new Bundle();
        String questionStringfy = question.toString();
        args.putString("questionString", questionStringfy);
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
        String stringQuestion = getArguments().getString("questionString");
        try {
            question = new JSONObject(stringQuestion);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        answerMap = (HashMap<String, ArrayList<Object>>) getArguments().getSerializable("answerMap");
        answers = new ArrayList<>();
        answerForms = new ArrayList<>();
        answer = new ArrayList<>();
        getLastesUpdate = new ArrayList<>();
        getMaxShort = question.optInt("maximumShortFormCount");
        getMinShort = question.optInt("minimumShortFormCount");
        shortAnswerSet = new HashSet<>();
        prev = "";
        curr = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sdk_fragment_open_end, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.question_content);
        textAnswerContainer = view.findViewById(R.id.text_answer_container);
        placeholder = (LinearLayout) getLayoutInflater().from(getActivity()).inflate(R.layout.sdk_open_end_answer_placeholder, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) convertDpToPixel(getActivity(), 19);
        placeholder.setLayoutParams(params);
        addAnswer = placeholder.findViewById(R.id.add_answer);
        scrollView = view.findViewById(R.id.scroll_view);
        tvLabel.setText(getQuestionText());

        ArrayList<Object> saved = answerMap.get(getSubItemId());
        if (saved != null) {
            //implement saved answers
        } else {
            if (isShortForm()) {
                handleAnswerForm(getMinShort, getMaxShort);
            } else {
                handleAnswerForm(1,1);
                answers.get(0).addTextChangedListener(this);
            }
        }

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
                        e.addTextChangedListener(this);
                        answers.add(e);
                    }

                }
            }
        }

        return answerForm;
    }

    public boolean isShortForm() {
        return question.has("isShortForm") && question.optBoolean("isShortForm");
    }

    public String getQuestionText() {
        return question.optString("text");
    }

    public String getSubItemId() {
        return question.optString("objectId");
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

        if (isShortForm()) {
            if (curr != null && curr.length() != 0 && !shortAnswerSet.contains(curr)) {
                shortAnswerSet.add(curr);
            }
            if (shortAnswerSet.size() < getMinShort) {
                return false;
            }
        } else {
            if (curr != null && curr.length() != 0 && !shortAnswerSet.contains(curr)) {
                shortAnswerSet.add(curr);
            }
            if (shortAnswerSet.size() == 0) {
                return false;
            }
        }

        ArrayList<Object> finalAnswer = new ArrayList<>();
        for (String answer : shortAnswerSet) {
            finalAnswer.add(answer);
        }
        answerMap.put(getSubItemId(), finalAnswer);
        return true;
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()){
//            case R.id.add_answer:
//            case R.id.add_answer_container:
            case R.id.question_open_end_answer_placeholder_container:
                textAnswerContainer.removeView(placeholder);
                handleAnswerForm(question.getMinimumShortFormCount(), question.getMaximumShortFormCount());
                break;

            default:
                break;
        }*/
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (s.toString() != null && s.toString().length() != 0 && curr.equals(s.toString())) {
            prev = s.toString();
        } else {
            if (curr != null && curr.length() != 0) {
                if (shortAnswerSet.contains(s.toString())) {
                    shortAnswerSet.remove(s.toString());
                }
                prev = curr;
            } else {
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        curr = s.toString();
        if (curr.length() > prev.length()) {
            if (!curr.substring(0, curr.length() - 1).equals(prev)) {
                shortAnswerSet.add(curr);
            }
        } else {
            if (prev != null && prev.length() != 0) {
                if (prev.substring(0, prev.length() - 1).equals(curr)) {
                    if (shortAnswerSet.contains(prev)) {
                        shortAnswerSet.remove(prev);
                    }
                } else {
                    shortAnswerSet.add(prev);
                }
            }
        }
    }

    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void skipped(){
        answerMap.remove(getSubItemId());
    }

    private void handleAnswerForm(int minCount, int maxCount){
        if(answerForms == null){
            answerForms = new ArrayList<>();
        }

        if(minCount == 1 && maxCount == 1){ // single answer
            addAnswerForm();
        }else if(answerForms.size() == 0 && minCount > 0){    // exist minimum short form count
            for(int i = 0; i < minCount; i++){
                addAnswerForm();
            }
        }else if(answerForms.size() <= maxCount){   // add form(addition) action
            addAnswerForm();
            if(answerForms.size() > 0){
                final LinearLayout lastForm = answerForms.get(answerForms.size() - 1);
                for(int i = 0; i < lastForm.getChildCount(); i++){
                    View child = lastForm.getChildAt(i);
                    if(child instanceof LinearLayout){
                        for(int j = 0; j < ((LinearLayout)child).getChildCount(); j++){
                            View child2 = ((LinearLayout)child).getChildAt(j);
                            if(child2.getId() == R.id.answer_delete_button_container){
                                child2.setVisibility(View.VISIBLE);
                                child2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        textAnswerContainer.removeView(placeholder);
                                        int index = answerForms.indexOf(lastForm);
                                        answers.remove(index);
                                        answerForms.remove(index);
                                        textAnswerContainer.removeView(lastForm);
                                        createAnswerPlaceholder();
                                        textAnswerContainer.addView(placeholder);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        if(answerForms.size() < maxCount){
            createAnswerPlaceholder();
            textAnswerContainer.addView(placeholder);
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
//        placeholder.findViewById(R.id.add_answer).setOnClickListener(this);
//        placeholder.findViewById(R.id.add_answer_container).setOnClickListener(this);
    }

}

