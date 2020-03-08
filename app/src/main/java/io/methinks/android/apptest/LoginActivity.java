package io.methinks.android.apptest;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Global.DEFAULT_BACKGROUND_COLOR_HEX));
        }

        ImageView logoImageView = (ImageView)findViewById(R.id.logo_image);
        if(Global.logoBitmap != null){
            logoImageView.setImageBitmap(Global.logoBitmap);
        }else{
            logoImageView.setImageResource(R.drawable.img_logo_methinks);
        }


        EditText testUserCodeEditText = (EditText)findViewById(R.id.testUserCode);
        testUserCodeEditText.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4D77DD")));

        testUserCodeEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Global.sTestUserCode = testUserCodeEditText.getText().toString().trim();

                JSONObject deviceInfo = DeviceInfo.getDeviceInfo(LoginActivity.this.getApplicationContext());
                JSONObject lastSessionLog = LocalStore.getInstance().getSessionLog();
                JSONObject lastAnswer = LocalStore.getInstance().getAnswer();

                new HttpManager().login(deviceInfo, lastSessionLog, lastAnswer, (response, error) -> {
                    try{
                        if(response != null && response.has("status") && response.getString("status").equals(Global.RESPONSE_OK)){
                            Log.d("AppTest user is completed login.");
                            LocalStore.getInstance().putTestUserCode(Global.sTestUserCode);

                            JSONObject result = response.getJSONObject("result");
                            Global.isScreenStreamAllowed = result.has("isScreenStreamAllowed") && result.getBoolean("isScreenStreamAllowed");
                            Global.sLoginTime = new Date().getTime() / 1000;
                            Global.sessionStartTime = Global.sLoginTime;
                            Global.sForegroundTime = Global.sLoginTime;
                            Global.loginResult = result;
                            Global.sCampaignParticipantId = result.getString("participantId");
                            Global.sUserId = result.getString("userId");
                            Global.sScreenName = result.getString("screenName");
                            Global.sId = Global.generateRandomString();
                            Global.isLogined = true;
                            Global.isNew = true;


                            Intent announcementIntent = new Intent(LoginActivity.this, AnnouncementActivity.class);
                            startActivity(announcementIntent);
                            finish();
                            return;
                        }else{
                            Log.e("AppTest user can't login to AppTest server now. Reason is : " + error);

                            new ErrorDialogFragment(error).show(getSupportFragmentManager(), "login_error");
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                            Global.sTestUserCode = null;
                            testUserCodeEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                        Global.sTestUserCode = null;
                        testUserCodeEditText.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    }
                });
            }
            return false; // pass on to other listeners.
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Global.REQUEST_SCREEN_SHARING_PERM && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onBackPressed() {}
}
