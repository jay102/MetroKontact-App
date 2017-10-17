package com.greenmousetech.MetroKontact1.LoginRegister;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.greenmousetech.MetroKontact1.MySingleton;
import com.greenmousetech.MetroKontact1.R;
import com.greenmousetech.MetroKontact1.SessionManagement;
import com.greenmousetech.MetroKontact1.UserInfo.DashBoard;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    Button login;
    TextView signup;
    AlertDialog alertDialog;
    EditText user_name, user_password;
    Context ctx = LoginActivity.this;

    final String TAG = this.getClass().getSimpleName();
    SharedPreferences loginPreferences;
    public static final String USERNAME = "username";
    SessionManagement session;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginPreferences = getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        session = new SessionManagement(getApplicationContext());
        progressDialog = new ProgressDialog(LoginActivity.this,
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
                if(isNetworkAvailable()==true && isOnline()==true ){
                    login();
                }else {
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                    login.setEnabled(false);
                }

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
    void login(){
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        String url = "https://metrokontact.com/app/loginregister.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                result = result.trim();
                if (result.equals("success")) {
                    progressDialog.dismiss();
                    session.createLoginSession(user_name.getText().toString(), user_password.getText().toString());
                    loginPreferences.edit().putString(USERNAME, user_name.getText().toString()).commit();
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
                if (error instanceof NoConnectionError) {
                    Toast.makeText(getApplicationContext(), "Mobile data Turned off or no Connection", Toast.LENGTH_LONG).show();
                    login.setEnabled(false);
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


    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}


