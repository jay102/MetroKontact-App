package com.greenmousetech.MetroKontact1;

/**
 * Created by Green Mouse Acer on 14/08/2017.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class CategoryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;

    public CategoryAdapter(Context context, List<String> expandableListTitle,
                           HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.category_list_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.category_name);
        expandedListTextView.setText(expandedListText);

        Character c = expandedListText.charAt(0);
        String a = Character.toString(c);

        ImageView categoryPhoto = (ImageView) convertView
                .findViewById(R.id.category_photo);
        //listSymbolItemTextView.setText(a);
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount()
    {
        return this.expandableListTitle.size();

    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.category_list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.category_name);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);

        ImageView categoryPhoto = (ImageView) convertView
                .findViewById(R.id.category_photo);
        listTitleTextView.setTypeface(null, Typeface.BOLD);

        switch (listTitle){
            case "LEGAL SERVICES": categoryPhoto.setImageResource(R.drawable.ic_legal);
             break;
            case "HAULAGE": categoryPhoto.setImageResource(R.drawable.ic_truck);
                break;
            case "AGENTS": categoryPhoto.setImageResource(R.drawable.ic_male);
                break;
            case "TAXI / CABS": categoryPhoto.setImageResource(R.drawable.ic_taxi);
                break;
            case "TECH MARKET": categoryPhoto.setImageResource(R.drawable.ic_network);
                break;
            case "CLOTHING AND BAGS": categoryPhoto.setImageResource(R.drawable.ic_briefcase);
                break;
            case "SUPPLIES AND DISTRIBUTORS": categoryPhoto.setImageResource(R.drawable.ic_truck);
                break;
            case "PRIVATE TUTORS & TUTORIAL CENTERS": categoryPhoto.setImageResource(R.drawable.ic_eye);
                break;
            case "TECHNICIANS & CRAFTMAN": categoryPhoto.setImageResource(R.drawable.ic_home);
                break;
            case "ENTERTAINMENT & FASHION": categoryPhoto.setImageResource(R.drawable.ic_briefcase);
                break;
            case "EVENTS SERVICES": categoryPhoto.setImageResource(R.drawable.ic_briefcase);
                break;
            case "MEDICAL SERVICES": categoryPhoto.setImageResource(R.drawable.ic_eye);
                break;
            case "GENERAL BUSINESSES": categoryPhoto.setImageResource(R.drawable.ic_home);
                break;
            case "RENTAL SERVICES & LAUNDRY": categoryPhoto.setImageResource(R.drawable.ic_truck);
                break;
            case "AGRICULTURE": categoryPhoto.setImageResource(R.drawable.ic_tree);
                break;
            case "HOTEL & SUITES": categoryPhoto.setImageResource(R.drawable.ic_hotel);
                break;
            case "OTHER PROFESSIONALS": categoryPhoto.setImageResource(R.drawable.ic_home);
                break;
            case "PROJECT MANAGERS": categoryPhoto.setImageResource(R.drawable.ic_send);
                break;
            case "COMPANIES": categoryPhoto.setImageResource(R.drawable.ic_briefcase);
                break;
            case "ENGINEERS & TECHNOLOGISTS": categoryPhoto.setImageResource(R.drawable.ic_users);
                break;
            case "AVAILABLE VEHICLE DRIVERS": categoryPhoto.setImageResource(R.drawable.ic_car);
                break;
            case "CONTRACTORS": categoryPhoto.setImageResource(R.drawable.ic_male);
                break;
            case "ARCHITECTS": categoryPhoto.setImageResource(R.drawable.ic_gears);
                break;
            case "BUSINESS CONSULTANTS": categoryPhoto.setImageResource(R.drawable.ic_users);
                break;
        }




        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

}