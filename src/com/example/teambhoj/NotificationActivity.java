package com.example.teambhoj;

import android.os.Bundle;
import android.widget.Toast;

public class NotificationActivity extends BaseActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final long stopPKey = this.getIntent().getLongExtra("Order", 0);
        Toast.makeText(getApplicationContext(),"Order " + stopPKey, 0).show();
        finish();
    }
}
