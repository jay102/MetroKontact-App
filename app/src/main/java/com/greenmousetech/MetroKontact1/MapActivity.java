package com.greenmousetech.MetroKontact1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

/**
 * Created by Green Mouse Acer on 30/08/2017.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener, GoogleMap.OnMyLocationChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {


    private static final String TAG = MapActivity.class.getSimpleName();
    private static final float METERS_100 = 100;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    ArrayList<LatLng> coordinates;
    AlertDialog alertDialog;
    SharedPreferences mapdetails;
    public JSONArray result;
    ArrayList<LatLng> locations;
    List<Marker> places = new ArrayList<>();
    String name = "";
    String website = "";
    String services = "";
    String phone = "";
    String phone2 = "";
    String address = "";
    JSONObject j = null;
    String childname;
    String emptyHeader;
    HashMap<String, MarkerHolder> markerHolderMap;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;

    private PlaceDetectionClient mPlaceDetectionClient;
    private static final String LOG_TAG = "Google Places Autocomplete";
    //AutoComplete service
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCw-cLns3PcQlZXG9jr982XaUWKceDOSXo";
    static final float COORDINATE_OFFSET = 0.00000f;
    HashMap<String, String> markerLocation;
    ImageView back;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    GPSTracker gps;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         gps = new GPSTracker(this);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.map);
        locations = new ArrayList();
        alertDialog = new AlertDialog.Builder(MapActivity.this).create();
        alertDialog.setTitle("Map");

         Bundle extras = getIntent().getExtras();
        if(extras!=null){
            childname = extras.getString("childname");
        }
        if(extras!=null){
            emptyHeader = extras.getString("groupHead");

        }

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, android.R.layout.simple_list_item_1));
        autoCompView.setOnItemClickListener(this);

        markerLocation = new HashMap<>();
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    void setupmarker(){
         markerHolderMap = new HashMap<>();
        String link = "https://metrokontact.com/app/getSubCategory.php";
        StringRequest request = new StringRequest(Request.Method.POST, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();
                locations.clear();

                try {
                    j = new JSONObject(response);
                    result = j.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject json = result.getJSONObject(i);
                        website = json.getString("website");
                        services = json.getString("services_render");
                        phone = json.getString("phone1");
                        phone2 = json.getString("phone2");
                        name = json.getString("business_name");
                        address = json.getString("business_address");
                        String loc = json.getString("latlng");
                        String[] parts = loc.split(",");
                        String lat = parts[0];
                        String lon = parts[1];
                        float latInt = Float.valueOf(lat).floatValue();
                        float longInt = Float.valueOf(lon).floatValue();
                        locations.add(new LatLng(latInt,longInt));
                        Log.d("MAPACTIVITY",website);
                        Log.d("MAPACTIVITY",services);
                        Log.d("MAPACTIVITY",phone);
                        for (LatLng location : locations) {
                            Marker marker =  mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.latitude,location.longitude))
                                    .title(name)
                                    .snippet(services)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bizlogo)));
                            places.add(marker);
                            MarkerHolder mHolder = new MarkerHolder(name,address, services, website, phone,phone2);
                            markerHolderMap.put(marker.getId(), mHolder); //Add info to HashMap
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()))
                                    .radius(3000));
                            coordinateForMarker(latInt,longInt);
                            float[] distance = new float[2];
                            Location.distanceBetween(marker.getPosition().latitude,marker.getPosition().longitude,
                                    circle.getCenter().latitude,circle.getCenter().longitude,distance);
                            if(distance[0] > circle.getRadius()){
                              //  Toast.makeText(getApplicationContext(),"No Business around",Toast.LENGTH_LONG).show();
                            }else if(distance[0] < circle.getRadius()){
                                   CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(location.latitude, location.longitude)).zoom(19).build();
                                     mMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(cameraPosition));

                            }

                        }

                        Log.d("this happened",name);
                        Log.d("this happened",website);
                        Log.d("this happened",phone);
                        Log.d("this happened",services);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(),"Couldn't Load Business, Turn On Mobile Data Or Connect To WIFI", Toast.LENGTH_LONG).show();
                }
            }
        }){

            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("type",childname);
                return postData;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }
    void groupHeaderMarker(){
        Log.d("groupHeaderHappened",emptyHeader);
        markerHolderMap = new HashMap<>();
        String link = "https://metrokontact.com/app/getCategory.php";
        StringRequest request = new StringRequest(Request.Method.POST, link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.trim();
                locations.clear();

                try {
                    j = new JSONObject(response);
                    result = j.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject json = result.getJSONObject(i);
                        website = json.getString("website");
                        services = json.getString("services_render");
                        phone = json.getString("phone1");
                        phone2 = json.getString("phone2");
                        name = json.getString("business_name");
                        address = json.getString("business_address");
                        String loc = json.getString("latlng");
                        String[] parts = loc.split(",");
                        String lat = parts[0];
                        String lon = parts[1];
                        float latInt = Float.valueOf(lat).floatValue();
                        float longInt = Float.valueOf(lon).floatValue();
                        locations.add(new LatLng(latInt,longInt));
                        Log.d("MAPACTIVITY",website);
                        Log.d("MAPACTIVITY",services);
                        Log.d("MAPACTIVITY",phone);
                        for (LatLng location : locations) {
                            Marker marker =  mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(location.latitude,location.longitude))
                                    .title(name)
                                    .snippet(services)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bizlogo)));
                            places.add(marker);
                            MarkerHolder mHolder = new MarkerHolder(name,address, services, website, phone,phone2);
                            markerHolderMap.put(marker.getId(), mHolder); //Add info to HashMap
                            Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()))
                                    .radius(3000));
                            coordinateForMarker(latInt,longInt);
                            float[] distance = new float[2];
                            Location.distanceBetween(marker.getPosition().latitude,marker.getPosition().longitude,
                                    circle.getCenter().latitude,circle.getCenter().longitude,distance);
                            if(distance[0] > circle.getRadius()){
                                //  Toast.makeText(getApplicationContext(),"No Business around",Toast.LENGTH_LONG).show();
                            }else if(distance[0] < circle.getRadius()){
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(location.latitude, location.longitude)).zoom(19).build();
                                mMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));

                            }

                        }

                        Log.d("this happened",name);
                        Log.d("this happened",website);
                        Log.d("this happened",phone);
                        Log.d("this happened",services);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof NoConnectionError){
                    Toast.makeText(getApplicationContext(),"Couldn't Load Business, Turn On Mobile Data Or Connect To WIFI", Toast.LENGTH_LONG).show();
                }
            }
        }){

            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postData = new HashMap<>();
                postData.put("cat",emptyHeader);
                return postData;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }



    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        if(notNull(childname)){
            setupmarker();
        }else if (notNull(emptyHeader)){
            groupHeaderMarker();}else{
            setupMap();
        }

 mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoContents(Marker arg0) {

        View v = getLayoutInflater().inflate(R.layout.map_info_window, null);

        TextView business_name = (TextView) v.findViewById(R.id.info_business_name);

        TextView business_address = (TextView) v.findViewById(R.id.info_address);

        TextView services = (TextView) v.findViewById(R.id.info_services_render);

        TextView website = (TextView) v.findViewById(R.id.info_website);

        TextView phone = (TextView) v.findViewById(R.id.info_phone);

        //These are standard, just uses the Title and Snippet
        business_name.setText(arg0.getTitle());

        services.setText(arg0.getSnippet());
        business_address.setText(address);
        //Now get the extra info you need from the HashMap
        //Store it in a MarkerHolder Object
        MarkerHolder mHolder = markerHolderMap.get(arg0.getId()); //use the ID to get the info

        website.setText(mHolder.info_website);

        phone.setText(mHolder.info_phone +" "+ mHolder.info_phone2);

        return v;

    }
});


    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation == null) {
                                gps.showSettingsAlert();
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }


    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mLastKnownLocation = null;
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;

        try {
            //Get latLng from String
            address = coder.getFromLocationName(str, 5);

            //Lets take first possibility from the all possibilities.
            Address location = address.get(0);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if(address==null){
                alertDialog.setMessage("Address not Located on Map");
                alertDialog.show();
            }else{
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, DEFAULT_ZOOM));
                 hidekeyboard(MapActivity.this);
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void onMyLocationChange(Location l) {
        Location target = new Location("target");
        for (LatLng location : coordinates) {
            target.setLatitude(location.latitude);
            target.setLongitude(location.longitude);
            if (l.distanceTo(target) < METERS_100) {
                Toast.makeText(MapActivity.this, "No business Located around", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {

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




    public static void displayPromptForEnablingGPS(
            final Activity activity)
    {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service to find current location.  Click OK to go to"
                + " location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }


    void setupMap(){
        if (mLastKnownLocation == null) {
           getLocationPermission();
        } else {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).zoom(16).build();
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

    }
  public static void hidekeyboard(Activity activity){

      InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
      View view = activity.getCurrentFocus();
       if(view == null){
           view = new View(activity);
       }
       imm.hideSoftInputFromWindow(view.getWindowToken(),0);
      view.clearFocus();
  }

  boolean notNull(Object... args){
      for(Object arg : args){
          if(arg == null){
              return false;
          }
      }
      return  true;
  }

    public class MarkerHolder {
        public String info_bizName;
        public String info_bizAdress;
        public String info_services;
        public String info_website;
        public String info_phone;
        public String info_phone2;

        public MarkerHolder(String bn,String ba, String sr, String we, String ph, String ph2) {
            info_bizName = bn;
            info_bizAdress = ba;
            info_services  = sr;
            info_website = we;
            info_phone = ph;
            info_phone2 = ph2;
        }
    }

    // Check if any marker is displayed on given coordinate. If yes then decide
// another appropriate coordinate to display this marker. It returns an
// array with latitude(at index 0) and longitude(at index 1).
    private String[] coordinateForMarker(float latitude, float longitude) {

        String[] location = new String[2];

        for (int i = 0; i <= 15; i++) {

            if (mapAlreadyHasMarkerForLocation((latitude + i
                    * COORDINATE_OFFSET)
                    + "," + (longitude + i * COORDINATE_OFFSET))) {

                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
                if (i == 0)
                    continue;

                if (mapAlreadyHasMarkerForLocation((latitude - i
                        * COORDINATE_OFFSET)
                        + "," + (longitude - i * COORDINATE_OFFSET))) {

                    continue;

                } else {
                    location[0] = latitude - (i * COORDINATE_OFFSET) + "";
                    location[1] = longitude - (i * COORDINATE_OFFSET) + "";
                    break;
                }

            } else {
                location[0] = latitude + (i * COORDINATE_OFFSET) + "";
                location[1] = longitude + (i * COORDINATE_OFFSET) + "";
                break;
            }
        }

        return location;
    }

    // Return whether marker with same location is already on map
    private boolean mapAlreadyHasMarkerForLocation(String location) {
        return (markerLocation.containsValue(location));
    }

}





