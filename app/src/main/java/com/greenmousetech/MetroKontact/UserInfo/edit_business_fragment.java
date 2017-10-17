package com.greenmousetech.MetroKontact.UserInfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.greenmousetech.MetroKontact.LoginRegister.LoginActivity.USERNAME;
import static com.greenmousetech.MetroKontact.R.id.business_state;

/**
 * Created by Green Mouse Acer on 16/09/2017.
 */

public class edit_business_fragment extends Fragment implements Spinner.OnItemSelectedListener {
    EditText business_name, business_address, phone1, phone2, website, business_services_render, lga, business_edit_address;
    Spinner business_to_edit, states_spinner;
    TextView businessid, position, phonelines, businessStatelga, businessCategorytype, businessNameAddress;
    Button edit_business, save;
    CheckBox director, other;
    Spinner business_category, business_type;
    private ArrayList<String> states;
    private JSONArray result;
    private ArrayList<String> businessId;
    private ArrayList<String> userInfo;
    AlertDialog alertDialog;
    SharedPreferences loginPreferences;
    private ArrayList<String> Profession;
    private List<String> sub_category;
  String Categoryname,Businesstype;
    public String bizCategory;
    public String bizType;
    public edit_business_fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_business_fragment, container, false);

        sub_category = new ArrayList<>();
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Add Business");
        loginPreferences = this.getActivity().getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        final String fullname = loginPreferences.getString("PersonalName", "");
        final String user = loginPreferences.getString(USERNAME, "");
        business_to_edit = (Spinner) rootView.findViewById(R.id.business_to_edit);
        business_edit_address = (EditText) rootView.findViewById(R.id.business_edit_address);
        edit_business = (Button) rootView.findViewById(R.id.edit_business);
        states_spinner = (Spinner) rootView.findViewById(business_state);
        business_name = (EditText) rootView.findViewById(R.id.business_name);
        business_address = (EditText) rootView.findViewById(R.id.business_address);
        business_category = (Spinner) rootView.findViewById(R.id.business_category);
        business_type = (Spinner) rootView.findViewById(R.id.business_type);
        lga = (EditText) rootView.findViewById(R.id.business_lga);
        phone1 = (EditText) rootView.findViewById(R.id.phone1);
        phone2 = (EditText) rootView.findViewById(R.id.phone2);
        website = (EditText) rootView.findViewById(R.id.website);
        business_services_render = (EditText) rootView.findViewById(R.id.business_services_render);
        director = (CheckBox) rootView.findViewById(R.id.director);
        businessid = (TextView) rootView.findViewById(R.id.businessId);
        position = (TextView) rootView.findViewById(R.id.position);
        phonelines = (TextView) rootView.findViewById(R.id.phoneLines);
        businessStatelga = (TextView) rootView.findViewById(R.id.businessStateandLga);
        businessCategorytype = (TextView) rootView.findViewById(R.id.businessCategoryandType);
        businessNameAddress = (TextView) rootView.findViewById(R.id.businessNameandAddress);
        other = (CheckBox) rootView.findViewById(R.id.other);
        save = (Button) rootView.findViewById(R.id.save);

        states = new ArrayList<>();
        businessId = new ArrayList<>();
        userInfo = new ArrayList<>();
        Profession = new ArrayList<>();
        business_to_edit.setOnItemSelectedListener(this);
        getBusinessId();

        edit_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBiz();}
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Editing Details...");
                progressDialog.show();
                String url = "http://192.168.43.192/metrokontact/app/delete_edit_act_business.php";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim();
                        if(response.equals("success")){
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"You Have Successfully Updated Your Business Details ",Toast.LENGTH_LONG).show();

                        }else if(response.equals("Business address can't be located on the map Enter a locatable address")){
                            progressDialog.dismiss();
                            business_address.setText("");
                        }else{
                            progressDialog.dismiss();
                            alertDialog.setMessage(response + "Or No internet connection to search address");
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
                            alertDialog.setMessage("Business address can't be located on the map Enter a Locateable address");
                            alertDialog.show();}
                    }
                }){

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
                        Map<String, String> postData = new HashMap<>();
                        postData.put("android","edit_business");
                        postData.put("business_id",business_to_edit.getSelectedItem().toString());
                        postData.put("business_name", business_name.getText().toString());
                        postData.put("business_address", business_address.getText().toString());
                        postData.put("business_category", bizCategory);
                        postData.put("business_type", bizType);
                        postData.put("phone1", phone1.getText().toString());
                        postData.put("phone2", phone2.getText().toString());
                        postData.put("fstate", states_spinner.getSelectedItem().toString());
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
                MySingleton.getInstance(getContext()).addToRequestQueue(request);
            }
        });
        return rootView;
    }

    public void parseStates() {

        String url = "http://192.168.43.192/metrokontact/app/states.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();
                JSONObject j = null;
                try {
                    j = new JSONObject(response);
                    result = j.getJSONArray("result");
                    getStates(result);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, states);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    states_spinner.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    void getStates(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                states.add(json.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void editBiz() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading User Details...");
        progressDialog.show();
        String url = "http://192.168.43.192/metrokontact/app/delete_edit_act_business.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();
                if (response.equals("success")) {

                    businessid.setVisibility(View.GONE);
                    business_to_edit.setVisibility(View.GONE);
                    edit_business.setVisibility(View.GONE);
                    business_edit_address.setVisibility(View.GONE);
                    businessNameAddress.setVisibility(View.VISIBLE);
                    businessCategorytype.setVisibility(View.VISIBLE);
                    businessStatelga.setVisibility(View.VISIBLE);
                    phonelines.setVisibility(View.VISIBLE);
                    position.setVisibility(View.VISIBLE);
                    business_category.setVisibility(View.VISIBLE);
                    business_type.setVisibility(View.VISIBLE);
                    states_spinner.setVisibility(View.VISIBLE);
                    director.setVisibility(View.VISIBLE);
                    other.setVisibility(View.VISIBLE);
                    save.setVisibility(View.VISIBLE);
                    extractDbInfo();
                    parseStates();
                    profession();
                    getProfession();
                    getSubCategory(result);
                    getSubCategory();
                    progressDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("android", "ebusiness");
                postData.put("business_id", business_to_edit.getSelectedItem().toString());
                return postData;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    void getBusinessId() {
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
                    business_to_edit.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.setMessage(error.getMessage());
                alertDialog.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String username = loginPreferences.getString(USERNAME, "");
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("android", "get_business_id");
                postData.put("user_name", username);
                return postData;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(getBusinessId);


    }


    private void extractDbInfo() {
        String url = "http://192.168.43.192/metrokontact/app/getEditBusinessDetails.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();
                JSONObject j = null;
                try {
                    j = new JSONObject(response);
                    result = j.getJSONArray("result");
                    getBusinessAddress(result);
                    getBusinessName(result);
                    getBusinessLga(result);
                    getPhone1(result);
                    getPhone2(result);
                    getBusinesswebsite(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("android", "dbInfo");
                postData.put("business_id",business_to_edit.getSelectedItem().toString());
                return postData;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(request);


    }

    private void getBusinessAddress(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                business_address.setVisibility(View.VISIBLE);
                business_address.setText(json.getString("business_address"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getBusinessName(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                business_name.setVisibility(View.VISIBLE);
                business_name.setText(json.getString("business_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getBusinessLga(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                lga.setVisibility(View.VISIBLE);
                lga.setText(json.getString("business_lga"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPhone1(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                phone1.setVisibility(View.VISIBLE);
                phone1.setText(json.getString("phone1"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPhone2(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                phone2.setVisibility(View.VISIBLE);
                phone2.setText(json.getString("phone2"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getBusinesswebsite(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                website.setVisibility(View.VISIBLE);
                website.setText(json.getString("website"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        business_edit_address.setVisibility(View.VISIBLE);
        business_edit_address.setText(" " + getAddress(position));
        business_edit_address.setEnabled(false);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        business_edit_address.setText("");
        business_edit_address.setVisibility(View.GONE);
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

    private String getAddress(int position) {
        String address = "";
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

    void profession() {


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
                    for (int i = 0; i < result.length(); i++) {
                    Categoryname = business_category.getSelectedItem().toString();}
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

               // sub_cat.add(json.getString("subcategory"));
                sub_category.add(json.getString("subcategory"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
        business_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bizType = business_type.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void getProfession(){

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
                                    android.R.layout.simple_spinner_dropdown_item, sub_category);
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
                sub_category.clear();


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
