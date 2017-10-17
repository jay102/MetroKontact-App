package com.greenmousetech.MetroKontact.UserInfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.greenmousetech.MetroKontact.MySingleton;
import com.greenmousetech.MetroKontact.NothingSelectedSpinnerAdapter;
import com.greenmousetech.MetroKontact.R;
import com.greenmousetech.MetroKontact.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.greenmousetech.MetroKontact.LoginRegister.LoginActivity.USERNAME;

/**
 * Created by Green Mouse Acer on 07/09/2017.
 */

public class activate_business_fragment extends Fragment implements Spinner.OnItemSelectedListener {
    Spinner business_to_activate;
    Button activate_business;
    AlertDialog AlertDialog;
    EditText activation_code, business_activate_address;
    SharedPreferences loginPreferences;
    private JSONArray result;
    private ArrayList<String> businessId;
    SessionManagement session;

    public activate_business_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activate_business_fragment, container, false);
        loginPreferences = this.getActivity().getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        final String username = loginPreferences.getString(USERNAME, "");
        activation_code = (EditText) rootView.findViewById(R.id.business_activation_code);
        business_to_activate = (Spinner) rootView.findViewById(R.id.business_to_activate);
        activate_business = (Button) rootView.findViewById(R.id.activate_business);
        business_activate_address = (EditText) rootView.findViewById(R.id.business_activate_address);
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        AlertDialog = new AlertDialog.Builder(getContext()).create();
        AlertDialog.setTitle("");

        //Array List for Business Id
        businessId = new ArrayList<>();
        session = new SessionManagement(getContext());
        business_to_activate.setOnItemSelectedListener(this);
        //hide address editbox
        business_activate_address.setVisibility(View.GONE);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Business...");
        progressDialog.show();
        String link = "http://192.168.43.192/metrokontact/app/getBusinessId.php";
        StringRequest getBusinessId = new StringRequest(Request.Method.POST, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();
                JSONObject j = null;
                try {
                    j = new JSONObject(response);
                    //Storing the Array of JSON String to our JSON Array
                    result = j.getJSONArray("BusinessId");

                    //Calling method getBusiness to get the businessiD from the JSON Array
                    getBusinessId(result);
              if(businessId!=null){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, businessId);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                   // business_to_activate.setAdapter(adapter);
                  business_to_activate.setPrompt("Select Business Id");
                   business_to_activate.setAdapter(
                            new NothingSelectedSpinnerAdapter(
                                    adapter,
                                    R.layout.spinner_row_nothing_selected,
                                    getContext()));
                    adapter.notifyDataSetChanged();
                  progressDialog.dismiss();}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(getContext(), "Mobile data Turned off or no Connection", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(getContext(), "Authentication Error occurred ", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("android", "get_business_activate");
                postData.put("user_name", username);
                return postData;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(getBusinessId);

        activate_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Activating Business...");
                progressDialog.show();
                String url = "http://192.168.43.192/metrokontact/app/delete_edit_act_business.php";
                StringRequest activatebiz = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim();
                        if (response.contains("success")) {
                            progressDialog.dismiss();
                            business_activate_address.setText("");
                            business_activate_address.setVisibility(View.GONE);
                            business_to_activate.setSelection(0);
                            Toast.makeText(getContext(), "You have Successfully Activated Business, Login to effect Changes", Toast.LENGTH_LONG).show();
                           session.logoutUser();
                        } else {
                            progressDialog.dismiss();
                            AlertDialog.setMessage(response);
                            AlertDialog.show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error instanceof NoConnectionError) {
                            Toast.makeText(getContext(), "Mobile data Turned off or no Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> postData = new HashMap<String, String>();
                        postData.put("android", "activate_business");
                        postData.put("user_name", username);
                        postData.put("business_id", business_to_activate.getSelectedItem().toString());
                        postData.put("activation_code", activation_code.getText().toString());
                        postData.put("business_address", business_activate_address.getText().toString());
                        return postData;
                    }
                };

                MySingleton.getInstance(getContext()).addToRequestQueue(activatebiz);


            }
        });


        return rootView;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               business_activate_address.setVisibility(View.VISIBLE);
               business_activate_address.setText(" "+getAddress(position-1));
               business_activate_address.setEnabled(false);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        business_activate_address.setText("");
        business_activate_address.setVisibility(View.GONE);
    }

    private void getBusinessId(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                businessId.add(json.getString("businessId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
   //Method to get address  of a particular position
    private String getAddress(int position){
        String address="";
        try {
            //Getting object of given index
            JSONObject json = result.getJSONObject(position);

            //Fetching address from that object
            address = json.getString("businessAddress");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Returning the address
        return address;
    }
}