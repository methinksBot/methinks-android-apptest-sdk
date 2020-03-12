package io.methinks.android.apptest.question.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.methinks.android.apptest.R;


public class SdkSmileyFragment extends BaseFragment {
    private View view;
    protected RadioGroup radioGroup;
    private ArrayList<Integer> selectedPosition;
    private HashMap<Integer, Integer> answerContainer;
    private HashMap<Integer, View> imageMap;
    protected JSONObject question;
    private ArrayList<String> choiceList;




    public static SdkSmileyFragment getInstance(JSONObject question, HashMap<String, ArrayList<Object>> answerMap) {
        SdkSmileyFragment smileyFragment = new SdkSmileyFragment();
        Bundle args = new Bundle();
        String questionStringfy = question.toString();
        args.putString("questionString", questionStringfy);
        args.putSerializable("answerMap", answerMap);
        smileyFragment.setArguments(args);
        return smileyFragment;
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
        answerContainer = new HashMap<>();
        imageMap = new HashMap<>();
        choiceList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sdk_fragment_smiley, container, false);
        TextView tvLabel = (TextView) view.findViewById(R.id.question_content);
        tvLabel.setText(getQuestionText());
        LinearLayout original = view.findViewById(R.id.original);

        /*if (question.has("choices")) {
            *//*original.setVisibility(View.GONE);
            choiceList = getChoiceList();
            int selectionNum = choiceList.size();
            if (selectionNum == 1) {
                LinearLayout one = view.findViewById(R.id.selection_one);
                one.setVisibility(View.VISIBLE);
                final TextView oneOne = view.findViewById(R.id.so_one);
                oneOne.setText(choiceList.get(0));
                oneOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(1)) {
                            oneOne.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(1);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(1, 1);
                            oneOne.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });
            } else if (selectionNum == 2) {
                LinearLayout two = view.findViewById(R.id.selection_two);
                two.setVisibility(View.VISIBLE);
                final TextView twoOne = view.findViewById(R.id.stw_one);
                final TextView twoTwo = view.findViewById(R.id.stw_two);
                twoOne.setText(choiceList.get(0));
                twoTwo.setText(choiceList.get(1));
                twoOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(1)) {
                            twoOne.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(1);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(1, 1);
                            twoOne.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                twoTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(2)) {
                            twoTwo.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(2);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(2, 2);
                            twoTwo.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

            } else if (selectionNum == 3) {
                LinearLayout three = view.findViewById(R.id.scale_three);
                three.setVisibility(View.VISIBLE);
                final TextView threeOne = view.findViewById(R.id.st_one);
                final TextView threeTwo = view.findViewById(R.id.st_two);
                final TextView threeThree = view.findViewById(R.id.st_three);
                threeOne.setText(choiceList.get(0));
                threeTwo.setText(choiceList.get(1));
                threeThree.setText(choiceList.get(2));
                threeOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(1)) {
                            threeOne.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(1);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(1, 1);
                            threeOne.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                threeTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(2)) {
                            threeTwo.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(2);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(2, 2);
                            threeTwo.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                threeThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(3)) {
                            threeThree.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(3);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(3, 3);
                            threeThree.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

            } else if (selectionNum == 4) {
                LinearLayout four = view.findViewById(R.id.selection_four);
                four.setVisibility(View.VISIBLE);
                final TextView fourOne = view.findViewById(R.id.sf_one);
                final TextView fourTwo = view.findViewById(R.id.sf_two);
                final TextView fourThree = view.findViewById(R.id.sf_three);
                final TextView fourFour = view.findViewById(R.id.sf_four);
                fourOne.setText(choiceList.get(0));
                fourTwo.setText(choiceList.get(1));
                fourThree.setText(choiceList.get(2));
                fourFour.setText(choiceList.get(3));

                fourOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(1)) {
                            fourOne.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(1);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(1, 1);
                            fourOne.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                fourTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(2)) {
                            fourTwo.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(2);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(2, 2);
                            fourTwo.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                fourThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(3)) {
                            fourThree.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(3);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(3, 3);
                            fourThree.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                fourFour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(4)) {
                            fourFour.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(4);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(3, 3);
                            fourFour.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

            } else {
                LinearLayout five = view.findViewById(R.id.original);
                five.setVisibility(View.VISIBLE);
                final TextView fiveOne = view.findViewById(R.id.s_strong_disagree);
                final TextView fiveTwo = view.findViewById(R.id.s_disagree);
                final TextView fiveThree = view.findViewById(R.id.s_neutral);
                final TextView fiveFour = view.findViewById(R.id.s_agree);
                final TextView fiveFive = view.findViewById(R.id.s_strong_agree);
                fiveOne.setText(choiceList.get(0));
                fiveTwo.setText(choiceList.get(1));
                fiveThree.setText(choiceList.get(2));
                fiveFour.setText(choiceList.get(3));
                fiveFive.setText(choiceList.get(4));

                fiveOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(1)) {
                            fiveOne.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(1);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(1, 1);
                            fiveOne.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                fiveTwo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(2)) {
                            fiveTwo.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(2);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(2, 2);
                            fiveTwo.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                fiveThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(3)) {
                            fiveThree.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(3);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(3, 3);
                            fiveThree.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                fiveFour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(4)) {
                            fiveFour.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(4);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(4, 4);
                            fiveFour.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });

                fiveFive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (answerContainer.containsKey(5)) {
                            fiveFive.setBackgroundColor(getResources().getColor(R.color.transparent));
                            answerContainer.remove(5);
                        } else {
                            if (answerContainer.size() != 0) {
                                for (Integer key : answerContainer.keySet()) {
                                    ImageView prevAns = imageMap.get(key);
                                    prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                    answerContainer.remove(key);
                                }
                            }
                            answerContainer.put(5, 5);
                            fiveFive.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                        }
                    }
                });
            }*//*
        } else */
        if (question.has("scale") && question.optInt("scale") == 3) {
            original.setVisibility(View.GONE);
            LinearLayout three = view.findViewById(R.id.scale_three);
            three.setVisibility(View.VISIBLE);
            final TextView sOne = view.findViewById(R.id.st_one);
            final TextView sTwo = view.findViewById(R.id.st_two);
            final TextView sThree = view.findViewById(R.id.st_three);
            sOne.setText(R.string.frwoning);
            sTwo.setText(R.string.neutral);
            sThree.setText(R.string.smile);

            imageMap.put(1, sOne);
            imageMap.put(2, sTwo);
            imageMap.put(3, sThree);

            sOne.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(1)) {
                        sOne.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(1);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = (TextView) imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(1, 1);
                        sOne.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            sTwo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(2)) {
                        sTwo.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(2);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = (TextView) imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(2, 2);
                        sTwo.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            sThree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(3)) {
                        sThree.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(3);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = (TextView) imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(3, 3);
                        sThree.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });
        } else {
            final TextView rb1 =  view.findViewById(R.id.s_strong_agree);
            final TextView rb2 =  view.findViewById(R.id.s_agree);
            final TextView rb3 =  view.findViewById(R.id.s_neutral);
            final TextView rb4 =  view.findViewById(R.id.s_disagree);
            final TextView rb5 =  view.findViewById(R.id.s_strong_disagree);
            rb1.setText(R.string.smile);
            rb2.setText(R.string.slightly_smile);
            rb3.setText(R.string.neutral);
            rb4.setText(R.string.slightly_frowning);
            rb5.setText(R.string.frwoning);

            imageMap.put(1, rb1);
            imageMap.put(2, rb2);
            imageMap.put(3, rb3);
            imageMap.put(4, rb4);
            imageMap.put(5, rb5);

            rb1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(1)) {
                        rb1.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(1);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = (TextView) imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(1, 1);
                        rb1.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            rb2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(2)) {
                        rb2.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(2);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = (TextView) imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(2, 2);
                        rb2.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            rb3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(3)) {
                        rb3.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(3);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = (TextView) imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(3, 3);
                        rb3.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            rb4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(4)) {
                        rb4.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(4);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = (TextView) imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(4, 4);
                        rb4.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });

            rb5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (answerContainer.containsKey(5)) {
                        rb5.setBackgroundColor(getResources().getColor(R.color.transparent));
                        answerContainer.remove(5);
                    } else {
                        if (answerContainer.size() != 0) {
                            for (Integer key : answerContainer.keySet()) {
                                TextView prevAns = (TextView) imageMap.get(key);
                                prevAns.setBackgroundColor(getResources().getColor(R.color.transparent));
                                answerContainer.remove(key);
                            }
                        }
                        answerContainer.put(5, 5);
                        rb5.setBackground(getResources().getDrawable(R.drawable.sdk_smile_image_background));
                    }
                }
            });
        }
        return view;
    }

    @Override
    public boolean validate() {
        if (answerContainer.size() == 0) {
            return false;
        } else {
            ArrayList<Object> value = new ArrayList<>();
            for (Integer key : answerContainer.keySet()) {
                value.add(answerContainer.get(key));
                break;
            }
            answerMap.put(getSubItemId(), value);
            return true;
        }
    }

    public String getSubItemId() {
        return question.optString("objectId");
    }

    public String getQuestionText() {
        return question.optString("text");
    }

    @Override
    public void skipped() {}

    public ArrayList<String> getChoiceList() {
        ArrayList<String> choices = new ArrayList<>();
        JSONArray choiceFromQ = question.optJSONArray("choices");
        for (int i = 0; i < choiceFromQ.length(); i++) {
            choices.add(choiceFromQ.optString(i));
        }
        return choices;
    }
}

