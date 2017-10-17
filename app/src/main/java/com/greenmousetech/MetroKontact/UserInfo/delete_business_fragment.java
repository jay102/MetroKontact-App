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

public class delete_business_fragment extends Fragment implements Spinner.OnItemSelectedListener{
   Spinner business_spinner;
    Button delete_business;
    EditText business_delete_address;
    AlertDialog AlertDialog;
    SharedPreferences loginPreferences;
    private JSONArray result;
    private ArrayList<String> businessId;
    SessionManagement session;

    public delete_business_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.delete_business_fragment, container, false);
        loginPreferences = this.getActivity().getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        final String username = loginPreferences.getString(USERNAME, "");
       business_spinner = (Spinner) rootView.findViewById(R.id.business_to_delete);
        business_delete_address = (EditText) rootView.findViewById(R.id.business_delete_address);
       delete_business = (Button) rootView.findViewById(R.id.delete_business);
        AlertDialog = new AlertDialog.Builder(getContext()).create();
        AlertDialog.setTitle("");

        businessId = new ArrayList<>();
        session = new SessionManagement(getContext());
        business_spinner.setOnItemSelectedListener(this);
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
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

                    //Calling method getBusinessId to get the businessId from the JSON Array
                    getBusinessId(result);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, businessId);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    business_spinner.setPrompt("Select Business Id");
                    business_spinner.setAdapter(
                            new NothingSelectedSpinnerAdapter(
                                    adapter,
                                    R.layout.spinner_row_nothing_selected,
                                    getContext()));
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
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
                postData.put("android", "get_business_id");
                postData.put("user_name", username);
                return postData;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(getBusinessId);




        delete_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Deleting Business...");
                progressDialog.show();

                String url = "http://192.168.43.192/metrokontact/app/delete_edit_act_business.php";
                StringRequest deletebiz = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim();
                        if (response.equals("success")) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Successfully Deleted Business", Toast.LENGTH_LONG).show();
                            business_delete_address.setText("");
                            business_delete_address.setVisibility(View.GONE);
                            business_spinner.setSelection(0);
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
                        postData.put("android", "delete_business");
                        postData.put("user_name", username);
                        postData.put("business_id", business_spinner.getSelectedItem().toString());
                        return postData;
                    }
                };

                MySingleton.getInstance(getContext()).addToRequestQueue(deletebiz);


            }
        });


        return rootView;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            business_delete_address.setVisibility(View.VISIBLE);
            business_delete_address.setText(" "+ getAddress(position-1));
            business_delete_address.setEnabled(false);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        business_delete_address.setText("");
        business_delete_address.setVisibility(View.GONE);
    }

    private void getBusinessId(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the businessId of the businesses to array list
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