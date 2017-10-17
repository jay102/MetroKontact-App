package com.greenmousetech.MetroKontact1.UserInfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.greenmousetech.MetroKontact1.MySingleton;
import com.greenmousetech.MetroKontact1.R;
import com.greenmousetech.MetroKontact1.LoginRegister.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.greenmousetech.MetroKontact1.R.id.business_state;

/**
 * Created by Green Mouse Acer on 16/09/2017.
 */

public class edit_business_fragment extends Fragment implements Spinner.OnItemSelectedListener,AdapterView.OnItemClickListener {
    EditText business_name, business_address, phone1, phone2, website, business_services_render, lga, business_edit_address,business_category, business_type,state;
    Spinner business_to_edit;
    TextView businessid, position, phonelines, businessStatelga, businessCategorytype, businessNameAddress;
    Button edit_business, save;
    CheckBox director, other;
    AutoCompleteTextView autoCompView;
    private ArrayList<String> states;
    private JSONArray result;
    private ArrayList<String> businessId;
    private ArrayList<String> userInfo;
    AlertDialog alertDialog;
    SharedPreferences loginPreferences;
    private ArrayList<String> Profession;
    private List<String> sub_category;
    Fragment fragment;
    //AutoComplete service
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCw-cLns3PcQlZXG9jr982XaUWKceDOSXo";
    public edit_business_fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.edit_business_fragment, container, false);

        sub_category = new ArrayList<>();
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Add Business");
        loginPreferences = this.getActivity().getSharedPreferences(LoginActivity.USERNAME, Context.MODE_PRIVATE);
        final String fullname = loginPreferences.getString("PersonalName", "");
        final String user = loginPreferences.getString(LoginActivity.USERNAME, "");
        business_to_edit = (Spinner) rootView.findViewById(R.id.business_to_edit);
        business_edit_address = (EditText) rootView.findViewById(R.id.business_edit_address);
        edit_business = (Button) rootView.findViewById(R.id.edit_business);
        state = (EditText) rootView.findViewById(business_state);
        business_name = (EditText) rootView.findViewById(R.id.business_name);
        autoCompView = (AutoCompleteTextView) rootView.findViewById(R.id.business_address);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), android.R.layout.simple_list_item_1));
        autoCompView.setOnItemClickListener(this);
        business_category = (EditText) rootView.findViewById(R.id.business_category);
        business_type = (EditText) rootView.findViewById(R.id.business_type);
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
                String url = "https://metrokontact.com/app/delete_edit_act_business.php";
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        fragment = null;
                        response = response.trim();
                        if(response.equals("success")){
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"You Have Successfully Updated Your Business Details ",Toast.LENGTH_LONG).show();
                            fragment = new dashboard_fragment();

                            if (fragment != null) {
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, fragment);
                                ft.commit();
                            }

                        }else if(response.equals("Business address can't be located on the map Enter a locatable address")){
                            progressDialog.dismiss();
                            autoCompView.setText("");
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
                            alertDialog.setMessage("Business address can't be located on the map Enter a Locatable address");
                            autoCompView.setText("");
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
                            address = coder.getFromLocationName(autoCompView.getText().toString(), 5);

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
                        postData.put("business_address", autoCompView.getText().toString());
                        postData.put("business_category", business_category.getText().toString());
                        postData.put("business_type", business_type.getText().toString());
                        postData.put("phone1", phone1.getText().toString());
                        postData.put("phone2", phone2.getText().toString());
                        postData.put("fstate", state.getText().toString());
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


    void editBiz() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading User Details...");
        progressDialog.show();
        String url = "https://metrokontact.com/app/delete_edit_act_business.php";
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
                    state.setVisibility(View.VISIBLE);
                    director.setVisibility(View.VISIBLE);
                    other.setVisibility(View.VISIBLE);
                    save.setVisibility(View.VISIBLE);
                    extractDbInfo();
                    getSubCategory(result);
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
        String link = "https://metrokontact.com/app/getBusinessId.php";
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
                String username = loginPreferences.getString(LoginActivity.USERNAME, "");
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("android", "get_business_id");
                postData.put("user_name", username);
                return postData;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(getBusinessId);


    }


    private void extractDbInfo() {
        String url = "https://metrokontact.com/app/getEditBusinessDetails.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();
                JSONObject j = null;
                try {
                    j = new JSONObject(response);
                    result = j.getJSONArray("result");
                    getBusinessAddress(result);
                    getEditBizCategory(result);
                    getEditBizCategoryType(result);
                    getEditBizState(result);
                    getBusinessName(result);
                    getBusinessLga(result);
                    getPhone1(result);
                    getPhone2(result);
                    getBusinesswebsite(result);
                    getServicesRender(result);
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
                autoCompView.setVisibility(View.VISIBLE);
                autoCompView.setText(json.getString("business_address"));
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
                lga.setEnabled(false);
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

    private void getEditBizState(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                state.setVisibility(View.VISIBLE);
                state.setText(json.getString("business_state"));
                state.setEnabled(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void getEditBizCategory(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                business_category.setVisibility(View.VISIBLE);
                business_category.setText(json.getString("business_category"));
                business_category.setEnabled(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void getEditBizCategoryType(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                business_type.setVisibility(View.VISIBLE);
                business_type.setText(json.getString("business_type"));
                business_type.setEnabled(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void getServicesRender(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject json = j.getJSONObject(i);

                //Adding the name of the student to array list
                business_services_render.setVisibility(View.VISIBLE);
                business_services_render.setText(json.getString("services_render"));
                business_services_render.setEnabled(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        business_edit_address.setVisibility(View.VISIBLE);
        business_edit_address.setText(getAddress(position));
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
    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:ng");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());


            // Load the results into a StringBuilder

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

        } catch (MalformedURLException e) {
            Log.e("TAG", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("TAG", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {

        }


        return resultList;

    }


    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String) resultList.get(index);

        }


        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());
                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Geocoder coder = new Geocoder(getContext());
        List<Address> address;

        try {
            //Get latLng from String
            address = coder.getFromLocationName(str, 5);

            //Lets take first possibility from the all possibilities.
            Address location = address.get(0);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
