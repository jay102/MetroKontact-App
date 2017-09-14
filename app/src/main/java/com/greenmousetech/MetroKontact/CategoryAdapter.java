package com.greenmousetech.MetroKontact;

/**
 * Created by Green Mouse Acer on 14/08/2017.
 */
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
            case "Legal Services": categoryPhoto.setImageResource(R.drawable.ic_legal);
             break;
            case "Haulage": categoryPhoto.setImageResource(R.drawable.ic_truck);
                break;
            case "Agents": categoryPhoto.setImageResource(R.drawable.ic_male);
                break;
            case "Taxi / Cabs": categoryPhoto.setImageResource(R.drawable.ic_taxi);
                break;
            case "Tech Market": categoryPhoto.setImageResource(R.drawable.ic_network);
                break;
            case "Clothing and Bags": categoryPhoto.setImageResource(R.drawable.ic_briefcase);
                break;
            case "Supplies and Distributors": categoryPhoto.setImageResource(R.drawable.ic_truck);
                break;
            case "Private Tutors & Tutorial Centers": categoryPhoto.setImageResource(R.drawable.ic_eye);
                break;
            case "Technicians & Craftman": categoryPhoto.setImageResource(R.drawable.ic_home);
                break;
            case "Entertainment & Fashion": categoryPhoto.setImageResource(R.drawable.ic_briefcase);
                break;
            case "Events Services": categoryPhoto.setImageResource(R.drawable.ic_briefcase);
                break;
            case "Medical Services": categoryPhoto.setImageResource(R.drawable.ic_eye);
                break;
            case "General Businesses": categoryPhoto.setImageResource(R.drawable.ic_home);
                break;
            case "Rental Services & Laundry": categoryPhoto.setImageResource(R.drawable.ic_truck);
                break;
            case "Agriculture": categoryPhoto.setImageResource(R.drawable.ic_tree);
                break;
            case "Hotel & Suites": categoryPhoto.setImageResource(R.drawable.ic_hotel);
                break;
            case "Other Professionals": categoryPhoto.setImageResource(R.drawable.ic_home);
                break;
            case "Project Managers": categoryPhoto.setImageResource(R.drawable.ic_send);
                break;
            case "Companies": categoryPhoto.setImageResource(R.drawable.ic_briefcase);
                break;
            case "Engineers & Technologists": categoryPhoto.setImageResource(R.drawable.ic_users);
                break;
            case "Available Vehicle Drivers": categoryPhoto.setImageResource(R.drawable.ic_car);
                break;
            case "Contractors": categoryPhoto.setImageResource(R.drawable.ic_male);
                break;
            case "Architects": categoryPhoto.setImageResource(R.drawable.ic_gears);
                break;
            case "Business Consultants": categoryPhoto.setImageResource(R.drawable.ic_users);
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