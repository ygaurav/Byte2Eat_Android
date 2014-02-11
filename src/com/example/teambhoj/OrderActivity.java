package com.example.teambhoj;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderActivity extends BaseActivity implements Animation.AnimationListener{
    private int currentQuantity = 1;
    private Animation animation;
    private static final int PlaceOrder = 0;
    private static final int DailyMenu = 1;
    private static final int UserDetails = 4;
    private int pricePerUnit = 0;
    private int dailyMenuId = 0;

    private int todaysTotal = 0;
    private int userCredit = 0;
    private Order order = new Order();
    protected final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return onBackGroundTaskComplete(msg);
        }
    });

    private boolean onBackGroundTaskComplete(Message msg) {
        hideProgress();
        if(msg.obj == null){
            showToast("Connection problem. Try later..");
            return false;
        }
        else if(msg.what == DailyMenu){
            onDownloadingDailyMenu(msg);
            setUserDetails();
        }else if(msg.what == PlaceOrder){
            onOrderComplete(msg);
        }else if(msg.what == UserDetails){
            onUserDetailsComplete(msg);
        }

        return false;
    }

    private void onUserDetailsComplete(Message msg) {
        TextView remainingBalance = (TextView) findViewById(R.id.remainingBalanceAmount);
        TextView todaysTotalOrder = (TextView) findViewById(R.id.todaysOrder);
        try {
            String json = msg.obj.toString();
            JSONObject userDetail = new JSONObject(json.substring(0, json.length()));
            userCredit = userDetail.getInt("Balance");
            todaysTotal = userDetail.getInt("TodaysOrderQty");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        remainingBalance.setText(String.format("%d/-", userCredit));
        todaysTotalOrder.setText(String.valueOf(todaysTotal));
        if(userCredit < 0){
            remainingBalance.setTextColor(Color.RED);
        }else{
            remainingBalance.setTextColor(Color.GREEN);
        }
    }



    private void onDownloadingDailyMenu(Message msg) {

        TextView itemNameText = (TextView)findViewById(R.id.itemNameText);
        TextView priceText = (TextView)findViewById(R.id.priceText);
        TextView amountView = (TextView)findViewById(R.id.totalamount);

        try {
            String json = msg.obj.toString();
            JSONObject todaysMenu = new JSONObject(json.substring(0, json.length()));
            pricePerUnit = todaysMenu.getInt("ItemPrice");
            itemNameText.setText(todaysMenu.getString("ItemName"));
            dailyMenuId = todaysMenu.getInt("Id");
            amountView.setText(String.valueOf(pricePerUnit));
            priceText.setText(String.format("Price per unit Rs %d/-", pricePerUnit));
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean onOrderComplete(Message msg) {
        if(msg.obj != null && msg.obj.equals("true")){
            Intent thankYou = new Intent(getApplicationContext(),ThankYouActivity.class);
            startActivity(thankYou);
        }else{
            String text = msg.obj.toString();
            try {
                JSONObject response = new JSONObject(text.substring(0, text.length()));
                String responseMessage = response.getString("ResponseMessage");
                Boolean boolValue = response.getBoolean("BoolValue");
                showToast(responseMessage);
                if(boolValue){
                    Intent thankYou = new Intent(getApplicationContext(),ThankYouActivity.class);
                    startActivity(thankYou);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
                showToast("Error occurred. Try later");
            }
        }

        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);
        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate);
        assert animation != null;
        animation.setAnimationListener(this);

        getTodaysMenu();
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        order.UserId = getIntent().getIntExtra(Constants.Intent.UserId, 0);
        order.DeviceInfo = String.format(" Number : %s, Operator : %s,Model : %s, Android : %s",tm.getLine1Number(), tm.getSimOperatorName(), getDeviceName(), getAndroidVersionName());

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.number_of_unit_picker);
        numberPicker.setValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);

        final TextView amountView = (TextView)findViewById(R.id.totalamount);
        amountView.setText(String.valueOf(pricePerUnit));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                currentQuantity = newVal;
                TextView amountView = (TextView) findViewById(R.id.totalamount);
                amountView.setText("" + currentQuantity * pricePerUnit);
//                amountView.startAnimation(animation);
            }
        });

        Button confirmOrderButton = (Button) findViewById(R.id.confirm_order_button);
        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirmOrder();
            }
        });

        SpannableString spannableString = new SpannableString("Terms and Condition");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(getApplicationContext(), TermsAndConditions.class));
            }
        };
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView termsAndCondition = (TextView) findViewById(R.id.terms);
        termsAndCondition.setText(spannableString);
        termsAndCondition.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order_screen_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.order_history:
                onOrderHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        todaysTotal = 0;
        currentQuantity = 1;
        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.number_of_unit_picker);
        numberPicker.setValue(1);
        getTodaysMenu();
    }

    public static String getAndroidVersionName() {
        return "Android " + Build.VERSION.RELEASE;
    }

    private void onConfirmOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm");
        builder.setMessage(String.format("Today's Order Summary \n\nEarlier Order Qty: %d\nCurrent Order Qty : %d\n-------------------------------\nTotal order Qty: %d", todaysTotal, currentQuantity, todaysTotal + currentQuantity));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                order.Quantity = currentQuantity;
                order.DailyMenuId = dailyMenuId;
                placeOrder();
            }
        });
        builder.setNegativeButton("No",null);
        builder.show();
    }

    private void setUserDetails() {
        showProgress("Fetching user details..");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String userResponse = null;
                try {
                    userResponse = new OrderService().getUserDetails(getUserName());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ESMS",e.getMessage());
                }
                handler.sendMessage(Message.obtain(handler, UserDetails, userResponse));
            }
        }).start();
    }

    private void placeOrder() {


        showProgress("Placing order. Please wait...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = null;
                try {
                    response = new OrderService().placeOrder(order);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("ESMS", String.format("Error occurred : %s", e.getMessage()));
                }
                handler.sendMessage(Message.obtain(handler, PlaceOrder, response));
            }
        }).start();
    }

    private void getTodaysMenu(){
        showProgress("Fetching today's menu. Please wait...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = null;
                try {
                    response = new OrderService().getDailyMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendMessage(Message.obtain(handler, DailyMenu, response));
            }
        }).start();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER, model = Build.MODEL;

        if (!model.startsWith(manufacturer)) {
            return String.format("%s %s", manufacturer, model).toUpperCase();
        }

        return model.toUpperCase();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        TextView amountView = (TextView) findViewById(R.id.totalamount);
        amountView.setText("" + currentQuantity * 10);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public void onOrderHistory() {
        Intent orderHistoryActivityIntent = new Intent(getApplicationContext(),OrderHistoryActivity.class);
        startActivity(orderHistoryActivityIntent);
    }
}
