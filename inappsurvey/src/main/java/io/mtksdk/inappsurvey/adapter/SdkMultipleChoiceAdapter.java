package io.mtksdk.inappsurvey.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.mtksdk.inappsurvey.R;
import io.mtksdk.inappsurvey.ViewControllerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import io.mtksdk.inappsurvey.converter.Question;

/**
 * Created by kgy 2019. 9. 24.
 */

public class SdkMultipleChoiceAdapter extends Section {
    private String questionText;
    private ArrayList<String> choices;
    private LinearLayout openEndEditTextContainer;
    private EditText openEndEditText;
    private Context context;
    private HashMap<Integer, ViewHolder> viewHolders;
    private boolean isClickedLastTextEntry;
    private ArrayList<Integer> selectedPosition;
    private HashMap<Integer, ImageView> checkImages;
    private HashMap<Integer, LinearLayout> mainLayouts;
    private Boolean multipleSelection;
    private Boolean isShuffleOrder;
    private HashMap<String, ArrayList<Object>> answerMap;
    private String objId;
    private Question question;




    public SdkMultipleChoiceAdapter(Context context, Question question) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.sdk_cell_question_multiple_choice)
                .headerResourceId(R.layout.sdk_cell_question_header)
                .build());
        this.question = question;
        this.objId = question.getQuestionId();
        this.context = context;
        this.questionText = question.getText();
        this.choices = question.getFinalChoices();
        this.viewHolders = new HashMap<>();
        this.checkImages = new HashMap<>();
        this.mainLayouts = new HashMap<>();
        this.selectedPosition = new ArrayList<>();
        this.multipleSelection = (Boolean) question.getRule().get("allowMultipleSelection");

        //saved answer setting
//        ArrayList<Object> saved = this.answerMap.get(objId);
//        if(saved != null && saved.size() > 0){  // exist saved answer(because of prev question)
//            for(Object choice : saved){
//                if(existValueInStringArray(this.choices, (String)choice)){  // just selected choice
//                    for(int i = 0; i < this.choices.size(); i++){
//                        if(this.choices.get(i).equals(choice)){
//                            selectedPosition.add(i);
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public int getContentItemsTotal() {
        return choices.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view){
        return new QuestionHeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder itemHolder = (ViewHolder) holder;
        itemHolder.setIsRecyclable(false);
        if(!viewHolders.containsKey(position)){
            viewHolders.put(position, itemHolder);
        }
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)itemHolder.itemView.getLayoutParams();
        if(position == 0){  // for top margin
            params.setMargins(0, (int) convertDpToPixel(context, 24), 0, 0);
        }else{
            params.setMargins(0, 0, 0, 0);
        }

        itemHolder.itemView.setLayoutParams(params);

        checkImages.put(position, itemHolder.imageView);
        mainLayouts.put(position, itemHolder.mainLayout);
        itemHolder.choiceForm.setVisibility(View.VISIBLE);
        itemHolder.openEndAnswerForm.setVisibility(View.GONE);


        hideKeyboard();

        itemHolder.choiceForm.setVisibility(View.VISIBLE);
        itemHolder.isOpenEndType = false;
        itemHolder.underline.setVisibility(View.GONE);
        itemHolder.choice.setVisibility(View.VISIBLE);
        itemHolder.choice.setText(choices.get(position));
        itemHolder.choice.setTextColor(context.getResources().getColor(R.color.charcoal_grey));
        itemHolder.openEndAnswerForm.setVisibility(View.GONE);

        itemHolder.imageView.setClickable(false);
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                handleSelectedPosition(multipleSelection, true, position, itemHolder);
            }
        });

        if(selectedPosition.contains(position)){    // current position contained into selected
            itemHolder.imageView.setImageResource(R.drawable.img_single_select_active_color_01);
            itemHolder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.pale_grey_five));
        }else{
            itemHolder.imageView.setImageResource(R.drawable.ic_img_single_select_nor);
            itemHolder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final QuestionHeaderViewHolder headerHolder = (QuestionHeaderViewHolder) holder;
        headerHolder.questionContent.setText(questionText);
    }

    /**
     * hiding keyboard
     */
    public void hideKeyboard(){
        if(openEndEditText != null){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(openEndEditText.getApplicationWindowToken(), 0);
        }
    }

    /**
     * converting dp to pixel for the layout params for choice view
     * @param context
     * @param dp
     * @return
     */
    public static float convertDpToPixel(Context context, float dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     *
     */
    private void handleSelectedPosition(boolean isMultiple, boolean isAdd, int position, ViewHolder viewHolder) {
        if (isMultiple) { //multiple selection
            if (selectedPosition.contains(position)){    // exist selected position(check remove action)
                selectedPosition.remove(selectedPosition.indexOf(position));
                viewHolder.imageView.setImageResource(R.drawable.ic_img_single_select_nor);
                viewHolder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else  {
                selectedPosition.add(position);
                viewHolder.imageView.setImageResource(R.drawable.img_single_select_active_color_01);
                viewHolder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.pale_grey_five));
            }
        } else { //single selection
            if(selectedPosition.contains(position)) {
                selectedPosition.clear();
                if (selectedPosition.size() == 0) {
                    Log.e("Single", "resetting the position worked");
                }
                viewHolder.imageView.setImageResource(R.drawable.ic_img_single_select_nor);
                viewHolder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                selectedPosition.clear();
                for(Integer index : checkImages.keySet()){
                    checkImages.get(index).setImageResource(R.drawable.ic_img_single_select_nor);
                }
                for(Integer index : mainLayouts.keySet()){
                    mainLayouts.get(index).setBackgroundColor(context.getResources().getColor(R.color.white));
                }
                selectedPosition.add(position);
                viewHolder.imageView.setImageResource(R.drawable.img_single_select_active_color_01);
                viewHolder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.pale_grey_five));
            }
        }

        ArrayList<Object> answer = new ArrayList<>();

        for (Integer index : selectedPosition) {
            Log.e("answer", choices.get(index));
            answer.add(choices.get(index));
        }

        question.setAnswerMap(answer);
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;
        public LinearLayout mainLayout;
        public ImageView imageView;
        public TextView choice;
        public LinearLayout openEndAnswerForm, choiceForm;
        public EditText answerForOpenEnd;
        public View underline;
        public View underlineForOpenEnd;
        public boolean isOpenEndType;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mainLayout = itemView.findViewById(R.id.cell_multiple_choice_main_layout);
            this.itemView = itemView;
            this.imageView = itemView.findViewById(R.id.check_box);
            this.choice = itemView.findViewById(R.id.choice);
            this.openEndAnswerForm = itemView.findViewById(R.id.open_end_answer_form);
            this.choiceForm = itemView.findViewById(R.id.choice_form);
            this.answerForOpenEnd = itemView.findViewById(R.id.open_end_answer);
            this.underline = itemView.findViewById(R.id.underline);
            this.underlineForOpenEnd = itemView.findViewById(R.id.open_end_answer_underline);

            openEndEditTextContainer = openEndAnswerForm;
        }
    }

    public class QuestionHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView questionContent;
        public ImageView attachedImage;

        public QuestionHeaderViewHolder(View itemView) {
            super(itemView);
            this.questionContent = itemView.findViewById(R.id.question_content);
            this.attachedImage = itemView.findViewById(R.id.attached_image);
        }
    }

    public static boolean existValueInStringArray(List<String> items, String value){           //List has to be ArrayList when connected to the sever
        for(int i = 0; i < items.size(); i++){
            String item = items.get(i);
            if(item.equals(value)){
                return true;
            }
        }

        return false;
    }

    public boolean validate() {
        ArrayList<Object> answer = question.getAnswerMap().get(objId);
        if (answer == null || answer.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public void shuffleArrayList(ArrayList arrayList){
        long seed = System.nanoTime();
        Collections.shuffle(arrayList, new Random(seed));
    }


}
