package com.greenmousetech.MetroKontact;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindCategory extends AppCompatActivity {
   ImageView back;
    SharedPreferences mapdetails;
    public static final String DETAILS = "details";
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;

    public String groupHead;
     public String childname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_category);

     mGeoDataClient = Places.getGeoDataClient(this, null);
      mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);


        back = (ImageView) findViewById(R.id.arrow_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = CategoryObjects.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CategoryAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);



        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
               if(expandableListTitle.get(groupPosition)==null) {
                   Intent intent = new Intent(FindCategory.this, MapActivity.class);
                   startActivity(intent);

               }
                groupHead = expandableListTitle.get(groupPosition);

            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                int count = parent.getExpandableListAdapter().getChildrenCount(groupPosition);
                if(count==0){
                    Intent intent = new Intent(FindCategory.this,MapActivity.class);
                    startActivity(intent);

                }

                return false;
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        final int groupPosition, final int childPosition, long id) {
              childname = expandableListDetail.get(
                        expandableListTitle.get(groupPosition)).get(
                        childPosition);
                mapdetails = getSharedPreferences(DETAILS, Context.MODE_PRIVATE);
                mapdetails.edit().putString("childname", childname).commit();
                Intent i = new Intent(FindCategory.this, MapActivity.class);
                i.putExtra("childname",childname);
                startActivity(i);
                return false;
            }
        });
    }



    }

