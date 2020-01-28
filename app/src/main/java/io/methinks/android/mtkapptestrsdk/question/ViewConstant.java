package io.methinks.android.mtkapptestrsdk.question;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.methinks.mtkpatchersdk.Global;
import io.methinks.mtkpatchersdk.HttpManager;
import io.methinks.mtkpatchersdk.Log;


/**
 * Created by kgy 2019. 9. 24.
 */
public class ViewConstant {
    public static String questionPackId;
    public static QuestionPack pack;
    public static ArrayList<JSONObject> questions = new ArrayList<>();
    public static ArrayList<Object> openEndAnswers = new ArrayList<>();
    public static JSONObject answer = new JSONObject();
//    public static NetworkConnect nm = new NetworkConnect();
    public static Boolean hasSurveyed = false;
    public static boolean isRequired;

    public static void surveyCompletion(JSONObject answer) {
        try {
            new HttpManager().answer(questionPackId, answer, new HttpManager.Callback() {
                @Override
                public void done(JSONObject response, String error) {
                    try{
                        if(response != null){
                            if(response.has("status") && response.getString("status").equals(Global.RESPONSE_OK)){
                                ViewConstant.answer = new JSONObject();
                            }
                        }else{
                            Log.e("Can't save In App Survey answer : " + error);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void setQuestionPack(QuestionPack pack, HttpManager.Callback callback){
        isRequired = pack.isRequired();
        new HttpManager().getQuestions(pack.getPackId(), callback);
    }

    public static void clearQuestions(){
        questions = new ArrayList<>();
    }


}
