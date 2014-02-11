package com.example.teambhoj;

import android.os.Bundle;
import android.view.View;


public class ThankYouActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you_activity);
    }


    public void onOk(View view) {
        finish();
    }

    public void onStop(){
        super.onStop();
        finish();
    }
}