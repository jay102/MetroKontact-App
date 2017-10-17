package com.greenmousetech.MetroKontact1.UserInfo;

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
import com.greenmousetech.MetroKontact1.SessionManagement;

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

import static com.greenmousetech.MetroKontact1.LoginRegister.LoginActivity.USERNAME;

public class add_business_fragment extends Fragment implements AdapterView.OnItemClickListener {
    EditText business_name, phone1, phone2, website, business_services_render, lga;
    Spinner business_state;
     AutoCompleteTextView autoCompView;
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
    Fragment fragment;
    private ArrayList<String> states;
    //AutoComplete service
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCw-cLns3PcQlZXG9jr982XaUWKceDOSXo";
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
        business_state = (Spinner) rootView.findViewById(R.id.business_state);
        lga = (EditText) rootView.findViewById(R.id.business_lga);
        phone1 = (EditText) rootView.findViewById(R.id.phone1);
        phone2 = (EditText) rootView.findViewById(R.id.phone2);
        website = (EditText) rootView.findViewById(R.id.website);
        business_services_render = (EditText) rootView.findViewById(R.id.business_services_render);
        director = (CheckBox) rootView.findViewById(R.id.director);
        other = (CheckBox) rootView.findViewById(R.id.other);
        submit_business = (Button) rootView.findViewById(R.id.submit_business);
         autoCompView = (AutoCompleteTextView) rootView.findViewById(R.id.addBizCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), android.R.layout.simple_list_item_1));
        autoCompView.setOnItemClickListener(this);
        alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("");
        Profession = new ArrayList<>();
        sub_cat = new ArrayList<>();
        session = new SessionManagement(getContext());
        states = new ArrayList<>();
        parseStates();
        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);

        business_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               bizCategory = business_category.getSelectedItem().toString();
                String url = "https://metrokontact.com/app/getProfession.php";
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
                    public Priority getPriority() {
                        return Priority.HIGH;
                    }
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


        String profession = "https://metrokontact.com/app/getProfession.php";
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
            public Priority getPriority() {
                return Priority.HIGH;
            }

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
                fragment = null;
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Submitting Business...");
                progressDialog.show();
                String url = "https://metrokontact.com/app/loginregister.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.trim();
                        if (response.equals("success")) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "You've successfully added your business", Toast.LENGTH_LONG).show();
                            resetFields();
                            fragment = new dashboard_fragment();

                            if (fragment != null) {
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, fragment);
                                ft.commit();
                            }
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

                       }
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
                        HashMap<String, String> postData = new HashMap<String, String>();
                        Log.d("LAT",String.valueOf(latitude));
                        Log.d("LONG",String.valueOf(longitude));
                     postData.put("android", "add_business");
                        postData.put("business_name", business_name.getText().toString());
                        postData.put("business_address",autoCompView.getText().toString());
                        postData.put("business_category", bizCategory);
                        postData.put("business_type", bizType);
                        postData.put("phone1", phone1.getText().toString());
                        postData.put("phone2", phone2.getText().toString());
                        postData.put("fstate", business_state.getSelectedItem().toString());
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
        lga.setText("");
        phone1.setText("");
        phone2.setText("");
        website.setText("");
        business_services_render.setText("");
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
    public void parseStates() {

        String url = "https://metrokontact.com/app/states.php";
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
                    business_state.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };
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


}
