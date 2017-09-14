package com.greenmousetech.MetroKontact.UserInfo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.greenmousetech.MetroKontact.MySingleton;
import com.greenmousetech.MetroKontact.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Green Mouse Acer on 07/09/2017.
 */

public class delete_business_fragment extends Fragment {
   Spinner business_spinner;
    Button delete_business;
    AlertDialog AlertDialog;
    public delete_business_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.delete_business_fragment, container, false);

       business_spinner = (Spinner) rootView.findViewById(R.id.business_to_delete);
       delete_business = (Button) rootView.findViewById(R.id.delete_business);
        AlertDialog = new AlertDialog.Builder(getContext()).create();
        AlertDialog.setTitle("Important");
        delete_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://192.168.8.106/metrokontact/app/change_password.php";
                StringRequest changePassword = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim();
                        if(response.equals("success")){
                            Toast.makeText(getContext(),"Succesfully Deleted Business", Toast.LENGTH_LONG).show();
                        }else{
                            AlertDialog.setMessage(response);
                            AlertDialog.show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof NoConnectionError){
                            Toast.makeText(getContext(), "Mobile data Turned off or no Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> postData = new HashMap<String, String>();
                        postData.put("android", "delete_business");
                        return postData;
                    }
                };

                MySingleton.getInstance(getContext()).addToRequestQueue(changePassword);



            }
        });


        return rootView;
    }


}