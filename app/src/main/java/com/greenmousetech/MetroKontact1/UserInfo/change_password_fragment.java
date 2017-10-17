package com.greenmousetech.MetroKontact1.UserInfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.greenmousetech.MetroKontact1.SessionManagement;
import com.greenmousetech.MetroKontact1.LoginRegister.LoginActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Green Mouse Acer on 07/09/2017.
 */

public class change_password_fragment extends Fragment {
    EditText new_password, confirm_password;
    Button saveSettings;
    AlertDialog AlertDialog;
    SharedPreferences loginPreferences;
    SessionManagement session;
    public change_password_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.change_password_fragment, container, false);
        new_password = (EditText) rootView.findViewById(R.id.new_password);
        confirm_password = (EditText) rootView.findViewById(R.id.confirm_password);
        saveSettings = (Button) rootView.findViewById(R.id.save_settings);
        AlertDialog = new AlertDialog.Builder(getContext()).create();
        AlertDialog.setTitle("");
        session = new SessionManagement(getContext());
        loginPreferences = this.getActivity().getSharedPreferences(LoginActivity.USERNAME, Context.MODE_PRIVATE);
        final String user = loginPreferences.getString(LoginActivity.USERNAME,"");
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Changing password...");
                progressDialog.show();
                String url = "https://metrokontact.com/app/loginregister.php";
                StringRequest changePassword = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim();
                        if(response.equals("success")){
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Successfully Changed Password, Login With New Password", Toast.LENGTH_LONG).show();
                            session.logoutUser();
                        }else{
                            progressDialog.dismiss();
                            AlertDialog.setMessage(response);
                            AlertDialog.show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if(error instanceof NoConnectionError){
                            Toast.makeText(getContext(), "Mobile data Turned off or no Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> postData = new HashMap<String, String>();
                        postData.put("android", "change_password");
                        postData.put("user_name", user);
                        postData.put("new_password", new_password.getText().toString());
                        postData.put("confirm_password", confirm_password.getText().toString() );
                        return postData;
                    }

                };

                MySingleton.getInstance(getContext()).addToRequestQueue(changePassword);



            }
        });


        return rootView;
    }


}