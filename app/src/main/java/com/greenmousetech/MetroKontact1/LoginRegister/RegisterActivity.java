package com.greenmousetech.MetroKontact1.LoginRegister;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.greenmousetech.MetroKontact1.MySingleton;
import com.greenmousetech.MetroKontact1.R;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {
Button createacct;
    EditText fullname,user_email,userpassword,user_confirm_password;
    Context ctx =   RegisterActivity.this;
    AlertDialog RegisterDialog;
    CheckBox user_agree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this,
                R.style.AppTheme_Dark_Dialog);
        fullname = (EditText) findViewById(R.id.reg_name);
        user_email = (EditText) findViewById(R.id.reg_email);
        userpassword = (EditText) findViewById(R.id.reg_pass);
        user_confirm_password = (EditText) findViewById(R.id.reg_confirm_pass);
        user_agree = (CheckBox) findViewById(R.id.agree);
        RegisterDialog = new AlertDialog.Builder(ctx).create();
        RegisterDialog.setTitle("");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        createacct = (Button) findViewById(R.id.create_account);
        createacct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Creating Account...");
                progressDialog.show();
                final String boxValue;
                if(user_agree.isChecked()){
                    boxValue ="true"; }else{
                    boxValue = "false";
                }

                String url = "https://metrokontact.com//app/loginregister.php";
                StringRequest registerRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String result) {
                        result = result.trim();
                        if(result.equals("Account was successfully created.Click on Login to list your business")){
                            progressDialog.dismiss();
                            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ctx,LoginActivity.class);
                            startActivity(intent);
                        }else {
                            progressDialog.dismiss();
                            RegisterDialog.setMessage(result);
                            RegisterDialog.show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if(error instanceof NoConnectionError){
                            Toast.makeText(getApplicationContext(), "Mobile data Turned off or no Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> postData = new HashMap<String, String>();
                        postData.put("android", "reg");
                        postData.put("fullname",fullname.getText().toString());
                        postData.put("user_email", user_email.getText().toString() );
                        postData.put("userpassword", userpassword.getText().toString() );
                        postData.put("user_confirm_password", user_confirm_password.getText().toString() );
                        postData.put("user_agree", boxValue);
                        return postData;
                    }
                }
                        ;
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(registerRequest);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
