package com.greenmousetech.MetroKontact.UserInfo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.greenmousetech.MetroKontact.R;

/**
 * Created by Green Mouse Acer on 07/09/2017.
 */

public class dashboard_fragment extends Fragment {
    final String TAG = this.getClass().getSimpleName();
    TextView reg_biz, act_biz, reg_biz_date;
    public dashboard_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dashboard_fragment, container, false);


     reg_biz = (TextView) rootView.findViewById(R.id.registered_biz_no);
        act_biz = (TextView) rootView.findViewById(R.id.activate_business_no);
        reg_biz_date = (TextView) rootView.findViewById(R.id.reg_biz_date);


        return rootView;
    }

   }