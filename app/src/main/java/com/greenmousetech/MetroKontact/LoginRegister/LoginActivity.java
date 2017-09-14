package com.greenmousetech.MetroKontact.LoginRegister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.greenmousetech.MetroKontact.MySingleton;
import com.greenmousetech.MetroKontact.R;
import com.greenmousetech.MetroKontact.UserInfo.DashBoard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button login;
    TextView signup;
    AlertDialog alertDialog;
    EditText user_name, user_password;
    Context ctx = LoginActivity.this;
    
    final String TAG = this.getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle("Login Information");

        user_name = (EditText) findViewById(R.id.e_mail);
        user_password = (EditText) findViewById(R.id.login_password);


        signup = (TextView) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://192.168.8.101/metrokontact/app/loginregister.php";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    public String reg_biz;
                    public String act_biz;
                    public  String reg_biz_date;
                    @Override
                    public void onResponse(String result) {
                        result = result.trim();
                        try {
                            JSONObject obj = new JSONObject(result.replaceAll("success", ""));
                            reg_biz_date = obj.getString("RegBizDate");
                            reg_biz = obj.getString("RegBiz");
                            act_biz = obj.getString("ActivatedBiz");
                            Log.d("DATE", reg_biz_date);
                            Log.d("REGBIZ", reg_biz);
                            Log.d("ACTIVATED", act_biz);
                            View view;
                            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            view  = inflater.inflate(R.layout.dashboard_fragment, null);
                           TextView   Reg_biz = (TextView) view.findViewById(R.id.registered_biz_no);
                           TextView Act_biz = (TextView) view.findViewById(R.id.activate_business_no);
                           TextView   Reg_biz_date = (TextView) view.findViewById(R.id.reg_biz_date);
                            Reg_biz.setText(reg_biz);
                            Act_biz.setText(act_biz);
                            Reg_biz_date.setText(reg_biz_date);
                            Log.d("after setting",reg_biz_date);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (result.contains("success")) {
                            Toast.makeText(ctx, "Login Successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ctx, DashBoard.class);
                            startActivity(intent);
                        } else {
                            alertDialog.setMessage(result);
                            alertDialog.show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Mobile data Turned off or no Connection", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Authentication Error occured ", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("android", "log");
                        postData.put("user_name", user_name.getText().toString());
                        postData.put("user_password", user_password.getText().toString());
                        return postData;
                    }
                };
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


}


