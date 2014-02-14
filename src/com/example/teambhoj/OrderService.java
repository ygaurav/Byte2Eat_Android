package com.example.teambhoj;

import android.net.Uri;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class OrderService {

    public String placeOrder(Order order) throws Exception {
        return Send(Constants.Method.Post,Constants.Url.OrderUrl,
                new JSONObject()
                        .put("Quantity",order.Quantity)
                        .put("DailyMenuid", order.DailyMenuId)
                        .put("UserId", order.UserId)
                        .put("DeviceInfo", order.DeviceInfo)
                        .toString());
    }

    private String Send(String method, String urlSegment, String data) throws Exception {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, 15000);
        HttpConnectionParams.setSoTimeout(httpParameters, 15000);

        HttpClient client = new DefaultHttpClient(httpParameters);
        HttpRequestBase request;

        if(method.equalsIgnoreCase("POST")){
            HttpPost req = new HttpPost(getUrl(urlSegment));
            req.setEntity(new StringEntity(data));
            request = req;
        }else{
            request = new HttpGet(getUrl(urlSegment));
        }

        request.setHeader("content-type","application/json");
        HttpResponse response = client.execute(request);
        StatusLine status = response.getStatusLine();
        String responseBody = EntityUtils.toString(response.getEntity());
        if(status.getStatusCode() == HttpStatus.SC_OK){
            return responseBody;
        }else {
            Log.e("ESMS", response.toString());
            throw new Exception("Operation was unsuccessful");
        }
    }

    public static String getUrl(String urlSegment) {
        return Uri.withAppendedPath(
                Uri.parse(Constants.Url.ServerUrl),
                urlSegment
        ).toString();
    }

    public String getOrderHistory(String userId) throws Exception {
        return Send(Constants.Method.Get, String.format("%s/%s", Constants.Url.OrderHistoryUrl, userId), null);
    }

    public String getDailyMenu() throws Exception {
        return Send(Constants.Method.Get, Constants.Url.DailyMenuUrl, null);
    }

    public String validateUser(String userId) throws Exception {
        return Send(Constants.Method.Get, String.format("%s/%s", Constants.Url.UserUrl, userId), null);
    }

    public String getUserDetails(String userName) throws Exception {
        return Send(Constants.Method.Get, String.format("%s/%s", Constants.Url.UserUrl, userName), null);
    }
}
