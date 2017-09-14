package com.greenmousetech.MetroKontact.UserInfo;

/**
 * Created by Green Mouse Acer on 07/09/2017.
 */

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.greenmousetech.MetroKontact.MapActivity;
import com.greenmousetech.MetroKontact.MySingleton;
import com.greenmousetech.MetroKontact.R;
import com.greenmousetech.MetroKontact.States;
import com.kosalgeek.android.json.JsonConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class add_business_fragment extends Fragment{
    public double latitude;
    public static double longitude;
    EditText business_name, business_address, phone1, phone2, website, business_services_render, lga;
    CheckBox director, other;
    private GoogleMap mMap;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public add_business_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.add_business_fragment, container, false);


        final Spinner business_category = (Spinner) rootView.findViewById(R.id.business_category);
        final Spinner business_type = (Spinner) rootView.findViewById(R.id.business_type);
        business_name = (EditText) rootView.findViewById(R.id.business_name);
        business_address = (EditText) rootView.findViewById(R.id.business_address);
        phone1 = (EditText) rootView.findViewById(R.id.phone1);
        phone2 = (EditText) rootView.findViewById(R.id.phone2);
        website = (EditText) rootView.findViewById(R.id.website);
        business_services_render = (EditText) rootView.findViewById(R.id.business_services_render);
        director = (CheckBox) rootView.findViewById(R.id.director);
        other = (CheckBox) rootView.findViewById(R.id.other);
       String lat;
        String lon;


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.notifyDataSetChanged();
        business_category.setAdapter(adapter);
        business_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String Categoryname;
            String Businesstype;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 Categoryname= (String) business_category.getSelectedItem();
                       Categoryname.toString();
                switch (Categoryname) {
                    case "Legal Services": ArrayAdapter<CharSequence> adapter23 = ArrayAdapter.createFromResource(getContext(),
                            R.array.LegalServices, android.R.layout.simple_spinner_item);
                        adapter23.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter23.notifyDataSetChanged();
                        business_type.setAdapter(adapter23);
                      Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Haulage":
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                                R.array.Haulage, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter.notifyDataSetChanged();
                        business_type.setAdapter(adapter);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Agents": ArrayAdapter<CharSequence> adapter22 = ArrayAdapter.createFromResource(getContext(),
                            R.array.Agents, android.R.layout.simple_spinner_item);
                        adapter22.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter22.notifyDataSetChanged();
                        business_type.setAdapter(adapter22);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Taxi / Cabs": ArrayAdapter<CharSequence> adapter21 = ArrayAdapter.createFromResource(getContext(),
                            R.array.TaxiCabs, android.R.layout.simple_spinner_item);
                        adapter21.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter21.notifyDataSetChanged();
                        business_type.setAdapter(adapter21);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Tech Market":
                        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                                R.array.TechMarket, android.R.layout.simple_spinner_item);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter1.notifyDataSetChanged();
                        business_type.setAdapter(adapter1);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Clothing and Bags": ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                            R.array.ClothingBags, android.R.layout.simple_spinner_item);
                        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter2.notifyDataSetChanged();
                        business_type.setAdapter(adapter2);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Supplies and Distributors": ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),
                            R.array.SuppliesDistributors, android.R.layout.simple_spinner_item);
                        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter3.notifyDataSetChanged();
                        business_type.setAdapter(adapter3);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Private Tutors & Tutorial Centers": ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(getContext(),
                            R.array.PrivateTutors, android.R.layout.simple_spinner_item);
                        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter4.notifyDataSetChanged();
                        business_type.setAdapter(adapter4);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Technicians & Craftman": ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(getContext(),
                            R.array.TechniciansCraftman, android.R.layout.simple_spinner_item);
                        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter5.notifyDataSetChanged();
                        business_type.setAdapter(adapter5);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Entertainment & Fashion": ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(getContext(),
                            R.array.EntertainmentFashion, android.R.layout.simple_spinner_item);
                        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter6.notifyDataSetChanged();
                        business_type.setAdapter(adapter6);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Events Services": ArrayAdapter<CharSequence> adapter7 = ArrayAdapter.createFromResource(getContext(),
                            R.array.EventsServices, android.R.layout.simple_spinner_item);
                        adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter7.notifyDataSetChanged();
                        business_type.setAdapter(adapter7);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Medical Services": ArrayAdapter<CharSequence> adapter8 = ArrayAdapter.createFromResource(getContext(),
                            R.array.MedicalServices, android.R.layout.simple_spinner_item);
                        adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter8.notifyDataSetChanged();
                        business_type.setAdapter(adapter8);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "General Businesses": ArrayAdapter<CharSequence> adapter9 = ArrayAdapter.createFromResource(getContext(),
                            R.array.GeneralBusinesses, android.R.layout.simple_spinner_item);
                        adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter9.notifyDataSetChanged();
                        business_type.setAdapter(adapter9);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Rental Services & Laundry": ArrayAdapter<CharSequence> adapter10 = ArrayAdapter.createFromResource(getContext(),
                            R.array.RentalServicesLaundry, android.R.layout.simple_spinner_item);
                        adapter10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter10.notifyDataSetChanged();
                        business_type.setAdapter(adapter10);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Agriculture": ArrayAdapter<CharSequence> adapter11 = ArrayAdapter.createFromResource(getContext(),
                            R.array.Agriculture, android.R.layout.simple_spinner_item);
                        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter11.notifyDataSetChanged();
                        business_type.setAdapter(adapter11);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Hotel & Suites": ArrayAdapter<CharSequence> adapter12 = ArrayAdapter.createFromResource(getContext(),
                            R.array.HotelSuites, android.R.layout.simple_spinner_item);
                        adapter12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter12.notifyDataSetChanged();
                        business_type.setAdapter(adapter12);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Other Professionals": ArrayAdapter<CharSequence> adapter13 = ArrayAdapter.createFromResource(getContext(),
                            R.array.OtherProfessionals, android.R.layout.simple_spinner_item);
                        adapter13.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter13.notifyDataSetChanged();
                        business_type.setAdapter(adapter13);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Project Managers": ArrayAdapter<CharSequence> adapter14 = ArrayAdapter.createFromResource(getContext(),
                            R.array.ProjectManagers, android.R.layout.simple_spinner_item);
                        adapter14.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter14.notifyDataSetChanged();
                        business_type.setAdapter(adapter14);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Companies": ArrayAdapter<CharSequence> adapter15 = ArrayAdapter.createFromResource(getContext(),
                            R.array.Companies, android.R.layout.simple_spinner_item);
                        adapter15.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter15.notifyDataSetChanged();
                        business_type.setAdapter(adapter15);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Engineers & Technologists": ArrayAdapter<CharSequence> adapter16 = ArrayAdapter.createFromResource(getContext(),
                            R.array.EngineersTechnologists, android.R.layout.simple_spinner_item);
                        adapter16.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter16.notifyDataSetChanged();
                        business_type.setAdapter(adapter16);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Available Vehicle Drivers": ArrayAdapter<CharSequence> adapter17 = ArrayAdapter.createFromResource(getContext(),
                            R.array.AvailableVehicleDrivers, android.R.layout.simple_spinner_item);
                        adapter17.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter17.notifyDataSetChanged();
                        business_type.setAdapter(adapter17);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Contractors": ArrayAdapter<CharSequence> adapter18 = ArrayAdapter.createFromResource(getContext(),
                            R.array.Contractors, android.R.layout.simple_spinner_item);
                        adapter18.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter18.notifyDataSetChanged();
                        business_type.setAdapter(adapter18);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Architects": ArrayAdapter<CharSequence> adapter19 = ArrayAdapter.createFromResource(getContext(),
                            R.array.Architects, android.R.layout.simple_spinner_item);
                        adapter19.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter19.notifyDataSetChanged();
                        business_type.setAdapter(adapter19);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;
                    case "Business Consultants": ArrayAdapter<CharSequence> adapter20 = ArrayAdapter.createFromResource(getContext(),
                            R.array.BusinessConsultants, android.R.layout.simple_spinner_item);
                        adapter20.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        adapter20.notifyDataSetChanged();
                        business_type.setAdapter(adapter20);
                        Businesstype =   business_type.getSelectedItem().toString();
                        break;

                }}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        getLocationFromAddress(business_address.getText().toString());
String url = "http://192.168.8.101/metrokontact/app/loginregister.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("android","add_business");
                postData.put("business_name",business_name.getText().toString());
                postData.put("business_address",business_address.getText().toString());
                postData.put("phone1",phone1.getText().toString());
                postData.put("phone2",phone2.getText().toString());
                postData.put("state","");
                postData.put("lga", "");
                postData.put("lat", String.valueOf(latitude));
                postData.put("lon", String.valueOf(longitude));

                return postData;
            }
        };
        String url1 = "http://192.168.8.112/metrokontact/app/states.php";
        StringRequest request = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<States> data = new JsonConverter<States>().toArrayList(response, States.class);
                Log.d("States",String.valueOf(data));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
        return rootView;
}




    public void getLocationFromAddress(String strAddress)
    {
        String title = business_name.getText().toString();
        //Create coder with Activity context - this
        Geocoder coder = new Geocoder(getContext());
        List<Address> address;

        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress,5);

            //check for null
            if (address == null) {
                return;
            }

            //Lets take first possibility from the all possibilities.
            Address location=address.get(0);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Intent i = new Intent(getContext(), MapActivity.class);
            i.putExtra("business_title",title);
            i.putExtra("Address", latLng);
            i.putExtra("details", phone1.getText().toString() + ":" + phone2.getText().toString());
            i.putExtra("Services_Render", business_services_render.getText().toString());
            //Put marker on map on that LatLng
            Marker srchMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.logo)));

            //Animate and Zoon on that map location
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
