package com.greenmousetech.MetroKontact1.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.greenmousetech.MetroKontact1.R;
import com.greenmousetech.MetroKontact1.LoginRegister.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Green Mouse Acer on 07/09/2017.
 */

public class dashboard_fragment extends Fragment {
    final String TAG = this.getClass().getSimpleName();
    TextView reg_biz, act_biz, reg_biz_date, no_activated_biz,
            online_payment_no, online_payment_date, bank_payment_no, bank_payment_date, expired_biz_no, expired_biz_date;
    public String Reg_biz;
    public String Act_biz;
    public String Reg_biz_date;
    public String Personal_name;
    public String Online_payment;
    public String Online_paymentDate;
    public String Bank_payment;
    public String Bank_paymentDate;
    public String ExpiredBiz;
    public String ExpiredBizDate;
    String user;
    SharedPreferences loginPreferences;
    Fragment fragment;

    public dashboard_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dashboard_fragment, container, false);


        no_activated_biz = (TextView) rootView.findViewById(R.id.no_activated_biz);
        reg_biz = (TextView) rootView.findViewById(R.id.registered_biz_no);
        act_biz = (TextView) rootView.findViewById(R.id.activate_business_no);
        reg_biz_date = (TextView) rootView.findViewById(R.id.reg_biz_date);
        online_payment_no = (TextView) rootView.findViewById(R.id.online_payments_no);
        online_payment_date = (TextView) rootView.findViewById(R.id.online_payment_date);
        bank_payment_no = (TextView) rootView.findViewById(R.id.bank_payments_no);
        bank_payment_date = (TextView) rootView.findViewById(R.id.bank_payments_date);
        expired_biz_no = (TextView) rootView.findViewById(R.id.expired_biz_no);
        expired_biz_date = (TextView) rootView.findViewById(R.id.expired_biz_date);
        UpdateDashboard();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    void UpdateDashboard(){
        // Setup 1 MB disk-based cache for Volley
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024);

        // Use HttpURLConnection as the HTTP client
        Network network = new BasicNetwork(new HurlStack());
        loginPreferences = this.getActivity().getSharedPreferences(LoginActivity.USERNAME, Context.MODE_PRIVATE);
        user = loginPreferences.getString(LoginActivity.USERNAME, "");
        String url = "https://metrokontact.com/app/dashboard.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String result) {
                result = result.trim();
                Log.d("result", result);
                try {
                    JSONObject obj = new JSONObject(result.replaceAll("success", ""));
                    loginPreferences.edit().clear();
                    Reg_biz_date = obj.getString("RegBizDate");
                    Reg_biz = obj.getString("RegBiz");
                    Act_biz = obj.getString("ActivatedBiz");
                    Personal_name = obj.getString("PersonalName");
                    Online_payment = obj.getString("OnlinePayments");
                    Online_paymentDate = obj.getString("OnlinePaymentsDate");
                    Bank_payment = obj.getString("BankPayments");
                    Bank_paymentDate = obj.getString("BankPaymentDate");
                    ExpiredBiz = obj.getString("ExpiredBiz");
                    ExpiredBizDate = obj.getString("ExpiredBizDate");
                    loginPreferences.edit().putString("PersonalName", Personal_name).commit();

                    //setting all the values
                    reg_biz.setTextSize(TypedValue.COMPLEX_UNIT_SP,65);
                    reg_biz.setText(Reg_biz);
                    act_biz.setTextSize(TypedValue.COMPLEX_UNIT_SP,65);
                    act_biz.setText(Act_biz);
                    reg_biz_date.setText(Reg_biz_date);
                    online_payment_no.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                    online_payment_no.setText(Online_payment);
                    online_payment_date.setText(Online_paymentDate);
                    bank_payment_no.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                    bank_payment_no.setText(Bank_payment);
                    bank_payment_date.setText(Bank_paymentDate);
                    expired_biz_no.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                    expired_biz_no.setText(ExpiredBiz);
                    expired_biz_date.setText(ExpiredBizDate);

                    if(Integer.parseInt(ExpiredBiz)>0){
                        fragment = null;
                        Toast.makeText(getContext(),"You have an Expired Business",Toast.LENGTH_LONG).show();
                        fragment = new activate_business_fragment();

                        if (fragment != null) {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.content_frame, fragment);
                            ft.commit();
                        }
                    }
                    if (Reg_biz_date.equals("null")) {
                        reg_biz_date.setText("No Registered Business Date Set Yet");
                    }
                    if (!Act_biz.substring(0).equals("0")) {
                        no_activated_biz.setText("Updated");
                    }
                    if (Online_paymentDate.equals("null")) {
                        online_payment_date.setText("No online payment");
                    }
                    if (Bank_paymentDate.equals("null")) {
                        bank_payment_date.setText("No bank payment");
                    }
                    if (ExpiredBizDate.equals("null")) {
                        expired_biz_date.setText("No Expired business");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("user_name",user);
                return postData;
            }
        };
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();
        queue.add(request);
    }
}