package io.methinks.android.apptest;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static io.methinks.android.apptest.Global.redirectToGuide;
import static java.lang.Thread.sleep;

import io.methinks.sdk.common.custom.widget.MethinksTextView;

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
        MethinksTextView loginText = (MethinksTextView)findViewById(R.id.textView_explain);
//        if(Global.logoBitmap != null){
//            logoImageView.setImageBitmap(Global.logoBitmap);
//        }else{
//            logoImageView.setImageResource(R.drawable.img_logo_methinks);
//        }

        //String platform = Global.platform.equals("methinks") ? "methinks" : "nexonfirst";
        String userCodeMessage = getString(R.string.patcher_msg_enter_user_code).replace("methinks", "Nexon First").replace("미띵스", "넥슨퍼스트");
        loginText.setText(userCodeMessage);
        Log.e(userCodeMessage);


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
                            Global.isInternalTester = result.getBoolean("isInternalTester");
                            Global.hideHoverButton = result.getBoolean("hideHoverButton");
                            JSONObject getBuildNumber = new JSONObject(result.getString("minimumTestBuildNumber"));
                            Global.minimumTestBuildNumber = getBuildNumber.getInt("android");
                            Global.platform = result.getString("platform");

                            String presetString = null;
                            try {
                                InputStream is = Global.applicationTracker.getTopActivity().getAssets().open("preset.json");
                                int fileSize = is.available();
                                byte[] buffer = new byte[fileSize];
                                is.read(buffer);
                                is.close();
                                presetString = new String(buffer, "UTF-8");
                                JSONObject presetObject = new JSONObject(presetString);
                                int currentBuildNumber = presetObject.getInt("build_number");
                                Log.e("[Current BuildNumber]: " + currentBuildNumber);

                                if (currentBuildNumber < Global.minimumTestBuildNumber) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Global.applicationTracker.getTopActivity(), R.style.MyDialogTheme);
                                    builder.setTitle(R.string.patcher_build_number_cont);
                                    builder.setCancelable(false);
                                    // positive 버튼 설정
                                    builder.setPositiveButton(R.string.patcher_next, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Global.redirectToGuide();
                                        }
                                    });
                                    Dialog installdialog = builder.create();
                                    installdialog.setCanceledOnTouchOutside(false);
                                    AlertDialog alertDialog = (AlertDialog) installdialog;
                                    alertDialog.show();
                                    try {
                                        sleep(6000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            /** Block emulator**/
                            try {
                                Global.blockEmulator = result.getBoolean("blockEmulator");
                            } catch (Exception e) {
                                Log.e("No blockEmulator fom Patcher Server.\n" + e.toString());
                            }
                            Log.e("[EMULATOR BLOCKING] :" + Global.isPlayedByEmulator + " / " + Global.blockEmulator);
                            if (Global.blockEmulator && Global.isPlayedByEmulator) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Global.applicationTracker.getTopActivity(), R.style.MyDialogTheme);
                                builder.setTitle(R.string.patcher_block_emulator_title).setMessage(R.string.patcher_block_emulator_desc);
                                builder.setCancelable(false);
                                // positive 버튼 설정
                                builder.setPositiveButton(R.string.patcher_next, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Global.applicationTracker.getTopActivity().stopService(Global.hoverIntent);
                                        Global.applicationTracker.getTopActivity().finishAffinity();
                                        System.exit(0);
                                    }
                                });
                                Dialog installdialog = builder.create();
                                installdialog.setCanceledOnTouchOutside(false);
                                AlertDialog alertDialog = (AlertDialog) installdialog;
                                alertDialog.show();

                            }


                            Intent announcementIntent = new Intent(LoginActivity.this, AnnouncementActivity.class);
                            startActivity(announcementIntent);
                            finish();
                            return;
                        }else{
                            Log.e("AppTest user can't login to AppTest server now. Reason is : " + error);

                            String errorString = "";
                            switch (error) {
                                case "removedFromCampaign":
                                    errorString = "You are no longer a part of this project.";
                                    break;
                                case "cantSeeProject":
                                    errorString = "The project has ended.";
                                    break;
                                case "invalidUserCode":
                                    errorString = "Invalid code.";
                                    break;
                                case "projectIsOver":
                                    errorString = "The project has ended. Thank you for your participation!";
                                    break;
                                case "projectIsNotStarted":
                                    errorString = "The project has not yet started. Please stay tuned.";
                                    break;
                                case "invalidProject":
                                    errorString = "This app is no longer available for testing.";
                                    break;
                                default:
                                    if (error.startsWith("invalidUserCode"))
                                        errorString = "Invalid code.";
                                    break;
                            }
                            new ErrorDialogFragment(errorString).show(getSupportFragmentManager(), "login_error");
                            testUserCodeEditText.getText().clear();


                            //Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
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
