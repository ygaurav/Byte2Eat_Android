package com.example.teambhoj;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.widget.Toast;

public class BaseActivity extends Activity {
    protected ProgressDialog progressDialog;

    protected SharedPreferences.Editor getEditor() {
        return getSharePreferences().edit();
    }

    protected SharedPreferences getSharePreferences() {
        return getApplication().getSharedPreferences("ESMS_PREFERENCES",0);
    }

    protected void showProgress(String message) {
        final ProgressDialog dialog = getProgressDialog();
        dialog.setMessage(message);
        dialog.show();
    }

    protected void hideProgress(){
        getProgressDialog().dismiss();
    }

    protected String getUserName() {
        return getSharePreferences().getString("UserName", "");
    }

    private ProgressDialog getProgressDialog() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
        }
        return progressDialog;
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
