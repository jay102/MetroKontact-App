package com.greenmousetech.MetroKontact.LoginRegister;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.greenmousetech.MetroKontact.SessionManagement;
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
    private boolean saveLogin;

    final String TAG = this.getClass().getSimpleName();
    SharedPreferences loginPreferences;
    public static final String USERNAME = "username";
    SessionManagement session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginPreferences = getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        session = new SessionManagement(getApplicationContext());
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);

        alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle("");
        user_name = (EditText) findViewById(R.id.e_mail);
        user_password = (EditText) findViewById(R.id.login_password);

        signup = (TextView) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.login);
          if(session.isLoggedIn()==true){
              Intent i = new Intent(getApplicationContext(),DashBoard.class);
              startActivity(i);
          }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Logging in...");
                progressDialog.show();
                String url = "http://192.168.43.192/metrokontact/app/loginregister.php";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    public String reg_biz;
                    public String act_biz;
                    public String reg_biz_date;
                    public String personal_name;

                    @Override
                    public void onResponse(String result) {
                        result = result.trim();
                        try {
                            JSONObject obj = new JSONObject(result.replaceAll("success", ""));
                            reg_biz_date = obj.getString("RegBizDate");
                            reg_biz = obj.getString("RegBiz");
                            act_biz = obj.getString("ActivatedBiz");
                            personal_name = obj.getString("PersonalName");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (result.contains("success")) {
                            session.createLoginSession(user_name.getText().toString(), user_password.getText().toString());
                            progressDialog.dismiss();
                            loginPreferences.edit().putString(USERNAME, user_name.getText().toString()).commit();
                            loginPreferences.edit().putString("RegBizDate", reg_biz_date).commit();
                            loginPreferences.edit().putString("RegBiz", reg_biz).commit();
                            loginPreferences.edit().putString("ActBiz", act_biz).commit();
                            loginPreferences.edit().putString("PersonalName", personal_name).commit();
                            Toast.makeText(ctx, "Login Successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ctx, DashBoard.class);
                            startActivity(intent);
                            finish();
                        }  else {
                            progressDialog.dismiss();
                            alertDialog.setMessage(result);
                            alertDialog.show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(), "Mobile data Turned off or no Connection", Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(), "Authentication Error occurred ", Toast.LENGTH_LONG).show();
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


