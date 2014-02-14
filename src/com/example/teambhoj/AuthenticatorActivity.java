package com.example.teambhoj;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
//import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;


public class AuthenticatorActivity extends BaseActivity {
    private boolean isValidUserid = false;
    private static final  int ValidUser = 3;
    private PendingIntent pendingIntent;

    protected final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return onBackGroundTaskComplete(msg);
        }
    });

    private boolean onBackGroundTaskComplete(Message msg) {
        hideProgress();
        onValidatingUser(msg);
        return false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("ESMS","onResume");
//        int orderNumber = getIntent().getIntExtra("Order", 0);
//        Toast.makeText(getApplicationContext(),"on Resume Order " + orderNumber, Toast.LENGTH_SHORT).show();
        registerAlarm();
    }

    private void onValidatingUser(Message msg) {
        try {
            String json = msg.obj.toString();
            JSONObject user = new JSONObject(json.substring(0, json.length()));
            int userId = user.getInt("UserId");
            String userName = user.getString("UserName");
            if(userId != 0){
                setUserId(userId, userName);
                goToOrderScreen(userId);
            }else{
                Toast.makeText(getApplicationContext(),"User does not exist. Please contact administrator.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Some error occured. Please try again later.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void goToOrderScreen(int userId) {
        Intent orderIntent = new Intent(getApplicationContext(),OrderActivity.class);
        orderIntent.putExtra(Constants.Intent.UserId,userId);
        startActivity(orderIntent);
        finish();
    }

    private void setUserId(int userId, String userName) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt("UserId",userId);
        editor.putString("UserName", userName);
        editor.commit();
    }

    private boolean isUserLoggedIn(){
        int userId = getSharePreferences().getInt("UserId", 0);
        return userId != 0;
    }

    private int getUserId() {
        return getSharePreferences().getInt("UserId", 0);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authenticator_acitivity);
        Log.d("ESMS","onCreate");

//        int orderNumber = getIntent().getIntExtra("Order", 0);
//        toat.makeText(getApplicationContext(),"on Create Order " + orderNumber, Toast.LENGTH_SHORT).show();
        registerAlarm();
        if(isUserLoggedIn()){
            goToOrderScreen(getUserId());
        }

        Button loginButton = (Button)findViewById(R.id.loginButton);
        final EditText userIdField = (EditText)findViewById(R.id.userid_edit_text);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = userIdField.getText().toString().trim();

                if (!userId.isEmpty()) {
                    validateUser(userId);
                } else {
                    showAlert();
                }
            }
        });
    }

    private void validateUser(final String userId) {
        showProgress("Validating...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = null;
                try {
                    response = new OrderService().validateUser(userId);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ESMS",e.getMessage());
                }
                handler.sendMessage(Message.obtain(handler, ValidUser, response));
            }
        }).start();
    }

    public void showAlert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("UserId cannot be empty");
        builder.setTitle("Alert");
        builder.setNegativeButton("Ok",null);
        builder.show();
    }

    public void registerAlarm(){
//        Log.d("ESMS", "registering for alarm.");
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR,1);
//        calendar.set(Calendar.MINUTE,28);
//        Intent myIntent = new Intent(AuthenticatorActivity.this, AlarmReceiver.class);
//        pendingIntent = PendingIntent.getBroadcast(AuthenticatorActivity.this, 0, myIntent,0);
//        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),60*1000,pendingIntent);
    }

}