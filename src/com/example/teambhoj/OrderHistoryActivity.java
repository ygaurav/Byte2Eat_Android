package com.example.teambhoj;

import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class OrderHistoryActivity extends BaseActivity {
    private static final int OrderHistory = 1;
    private static boolean isDownloading = false;

    protected final Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return onBackGroundTaskComplete(msg);
        }
    });

    private boolean onBackGroundTaskComplete(Message msg) {
        hideProgress();
        if(msg.obj == null){
            showToast("Error occurred. Try later");
        }
        else if(msg.what == OrderHistory){
            onDownloadingOrderHistory(msg);
        }
        isDownloading = false;
        return false;
    }

    private void onDownloadingOrderHistory(Message msg) {
        try {
            String json = msg.obj.toString();
            JSONObject orderHistory = new JSONObject(json.substring(0, json.length()));
            mapOrderHistory(orderHistory.getJSONArray("OrderHistory"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        bindData();
    }

    private void mapOrderHistory(JSONArray orderHistoryJson) throws JSONException {
        List<Order> orders = new ArrayList<Order>();
        for(int i = 0; i < orderHistoryJson.length(); i++){
            Order o = new Order();
            JSONObject order = (JSONObject) orderHistoryJson.get(i);
            o.Quantity = order.getInt("Quantity");
            o.ItemName = order.getJSONObject("DailyMenu").getString("ItemName");
            o.Price = order.getJSONObject("DailyMenu").getInt("ItemPrice");
            o.Cost = o.Price * o.Quantity;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                o.orderDate = format.parse(order.getString("OrderDate"));
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            orders.add(o);

        }
        OrderHistoryAdapter orderHistoryAdapter = new OrderHistoryAdapter(orders);
        ((ListView)findViewById(R.id.orderHistoryList)).setAdapter(orderHistoryAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_history_activity);
//        bindData();

    }

    public void bindData(){
        if(isDownloading) return;

        showProgress("Fetching order history..");
        isDownloading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String userResponse = null;
                try {
                    userResponse = new OrderService().getOrderHistory(getUserName());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ESMS", e.getMessage());
                }
                handler.sendMessage(Message.obtain(handler, OrderHistory, userResponse));
            }
        }).start();
    }



    private class OrderHistoryAdapter extends BaseAdapter{
        private List<Order> orders;

        public OrderHistoryAdapter(List<Order> orderList){
            orders = orderList;
        }

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Order getItem(int i) {
            return orders.get(i);  //getItem
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null)
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_history_item, viewGroup, false);

            ((TextView)view.findViewById(R.id.order_item_name)).setText(orders.get(i).ItemName);
            ((TextView)view.findViewById(R.id.itemQuantity)).setText(String.valueOf(orders.get(i).Quantity));
            ((TextView)view.findViewById(R.id.itemPrice)).setText(String.format("Rs. %s", String.valueOf(orders.get(i).Price)));
            ((TextView)view.findViewById(R.id.itemCost)).setText(String.format("Rs. %s", String.valueOf(orders.get(i).Cost)));

            SimpleDateFormat sdf = new SimpleDateFormat("dd - MMM - yyyy");
            ((TextView)view.findViewById(R.id.orderDateTime)).setText(sdf.format(orders.get(i).orderDate));

            return view;
        }
    }
}