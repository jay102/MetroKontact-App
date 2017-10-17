package com.greenmousetech.MetroKontact.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.greenmousetech.MetroKontact.R;

import static com.greenmousetech.MetroKontact.LoginRegister.LoginActivity.USERNAME;

/**
 * Created by Green Mouse Acer on 07/09/2017.
 */

public class dashboard_fragment extends Fragment {
    final String TAG = this.getClass().getSimpleName();
    TextView reg_biz, act_biz, reg_biz_date, no_activated_biz;
    SharedPreferences loginPreferences;
    public dashboard_fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dashboard_fragment, container, false);

        loginPreferences = this.getActivity().getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        String user = loginPreferences.getString(USERNAME,"");
        String Regbiz = loginPreferences.getString("RegBiz","");
        String RegBizDate = loginPreferences.getString("RegBizDate","");
        String ActBiz = loginPreferences.getString("ActBiz","");

        no_activated_biz = (TextView) rootView.findViewById(R.id.no_activated_biz);
        reg_biz = (TextView) rootView.findViewById(R.id.registered_biz_no);
        act_biz = (TextView) rootView.findViewById(R.id.activate_business_no);
        reg_biz_date = (TextView) rootView.findViewById(R.id.reg_biz_date);
        reg_biz.setText(Regbiz);
        act_biz.setText(ActBiz);
        reg_biz_date.setText(RegBizDate);
if(RegBizDate.equals("null")){
    reg_biz_date.setText("No Registered Business Date Set Yet");
}
if(!ActBiz.substring(0).equals("0")){
    no_activated_biz.setText("Updated");
}

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();


    }
}