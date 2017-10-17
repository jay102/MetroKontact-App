package com.greenmousetech.MetroKontact.UserInfo;

/**
 * Created by Green Mouse Acer on 07/09/2017.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.greenmousetech.MetroKontact.MySingleton;
import com.greenmousetech.MetroKontact.R;
import com.greenmousetech.MetroKontact.SessionManagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.greenmousetech.MetroKontact.LoginRegister.LoginActivity.USERNAME;

public class add_business_fragment extends Fragment {
    EditText business_name, business_address, phone1, phone2, website, business_services_render, lga, business_state;
    CheckBox director, other;
    Button submit_business;
    AlertDialog alertDialog;
    SharedPreferences loginPreferences;
    private JSONArray result;
    private ArrayList<String> Profession;
    private ArrayList<String> sub_cat;
    public String bizCategory;
    public String bizType;
    Spinner business_type;
    SessionManagement session;
    public add_business_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.add_business_fragment, container, false);

        loginPreferences = this.getActivity().getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        final String fullname = loginPreferences.getString("PersonalName", "");
        final String user = loginPreferences.getString(USERNAME, "");
        final Spinner business_category = (Spinner) rootView.findViewById(R.id.business_category);
        business_type  = (Spinner) rootView.findViewById(R.id.business_type);
        business_name = (EditText) rootView.findViewById(R.id.business_name);
        business_address = (EditText) rootView.findViewById(R.id.business_address);
        business_state = (EditText) rootView.findViewById(R.id.business_state);
        lga = (EditText) rootView.findViewById(R.id.business_lga);
        phone1 = (EditText) rootView.findViewById(R.id.phone1);
        phone2 = (EditText) rootView.findViewById(R.id.phone2);
        website = (EditText) rootView.findViewById(R.id.website);
        business_services_render = (EditText) rootView.findViewById(R.id.business_services_render);
        director = (CheckBox) rootView.findViewById(R.id.director);
        other = (CheckBox) rootView.findViewById(R.id.other);
        submit_business = (Button) rootView.findViewById(R.id.submit_business);
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("");
        Profession = new ArrayList<>();
        sub_cat = new ArrayList<>();
        session = new SessionManagement(getContext());
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);

        business_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               bizCategory = business_category.getSelectedItem().toString();
                String url = "http://192.168.43.192/metrokontact/app/getProfession.php";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim();

                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("result");
                            getSubCategory(result);

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                    android.R.layout.simple_spinner_dropdown_item, sub_cat);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            business_type.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> postData = new HashMap<>();
                        postData.put("android", "subCat");
                        postData.put("cat", bizCategory);
                        return postData;
                    }
                };
                MySingleton.getInstance(getContext()).addToRequestQueue(request);
                sub_cat.clear();


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
business_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        bizType = business_type.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
});


        String profession = "http://192.168.43.192/metrokontact/app/getProfession.php";
        StringRequest category = new StringRequest(Request.Method.POST, profession, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();
                JSONObject j = null;
                try {
                    j = new JSONObject(response);
                    result = j.getJSONArray("result");
                    getProfession(result);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, Profession);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    business_category.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("android", "get_profession");
                return postData;
            }
        };
       MySingleton.getInstance(getContext()).addToRequestQueue(category);
        submit_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Submitting Business...");
                progressDialog.show();
                String url = "http://192.168.43.192/metrokontact/app/loginregister.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim();
                        if (response.equals("success")) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "You've successfully added your business, Login to effect Changes and Activate to show on map", Toast.LENGTH_LONG).show();
                            resetFields();
                           session.logoutUser();
                        } else if(response.equals("Business address can't be located on the map Enter a locatable address")){
                            business_address.setText("");
                        }else {
                            progressDialog.dismiss();
                            alertDialog.setMessage(response);
                            alertDialog.show();
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
                        }else {

                        progressDialog.dismiss();
                        alertDialog.setMessage(error.getMessage());
                        alertDialog.show();}
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        final String Director;
                        final String Other;
                        if (director.isChecked()) {
                            Director = "director";
                        } else {
                            Director = "Other";
                        }
                        if (other.isChecked()) {
                            Other = "other";
                        } else {
                            Other = "Not set";
                        }
                        double latitude = 0,longitude = 0;
                        //Create coder with Activity context - this
                        Geocoder coder = new Geocoder(getContext());
                        List<Address> address;

                        try {
                            //Get latLng from String
                            address = coder.getFromLocationName(business_address.getText().toString(), 5);

                            //Lets take first possibility from the all possibilities.
                            Address location = address.get(0);
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            if(address==null){
                                progressDialog.dismiss();
                                alertDialog.setMessage("Address not Located on Map");
                                alertDialog.show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        HashMap<String, String> postData = new HashMap<String, String>();
                        postData.put("android", "add_business");
                        postData.put("business_name", business_name.getText().toString());
                        postData.put("business_address", business_address.getText().toString());
                        postData.put("business_category", bizCategory);
                        postData.put("business_type", bizType);
                        postData.put("phone1", phone1.getText().toString());
                        postData.put("phone2", phone2.getText().toString());
                        postData.put("fstate", business_state.getText().toString());
                        postData.put("flocalgovt", lga.getText().toString());
                        postData.put("lat", String.valueOf(latitude));
                        postData.put("lng", String.valueOf(longitude));
                        postData.put("website", website.getText().toString());
                        postData.put("checkbox1", Director);
                        postData.put("user_name", user);
                        postData.put("hname", fullname);
                        postData.put("checkbox3", Other);
                        postData.put("services_render", business_services_render.getText().toString());
                        return postData;
                    }
                };
                MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
            }
        });


        return rootView;
    }


    private void getProfession(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                Profession.add(json.getString("category"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getSubCategory(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                sub_cat.add(json.getString("subcategory"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void resetFields() {

        business_name.setText("");
        business_address.setText("");
        business_state.setText("");
        lga.setText("");
        phone1.setText("");
        phone2.setText("");
        website.setText("");
        business_services_render.setText("");
    }

void getSubCategory(){

    String url = "http://192.168.43.192/metrokontact/app/getProfession.php";
    StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            response = response.trim();

            JSONObject j = null;
            try {
                j = new JSONObject(response);
                //Storing the Array of JSON String to our JSON Array
                result = j.getJSONArray("result");
                getSubCategory(result);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> postData = new HashMap<>();
            postData.put("android", "subCat");
            postData.put("cat", bizCategory);
            return postData;
        }
    };
    MySingleton.getInstance(getContext()).addToRequestQueue(request);

}

}
