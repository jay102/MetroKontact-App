package com.greenmousetech.MetroKontact1.UserInfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.greenmousetech.MetroKontact1.R;

/**
 * Created by Green Mouse Acer on 06/10/2017.
 */

public class purchase_activationcode_fragment extends Fragment {
    Button payOnline;

    public purchase_activationcode_fragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.purchase_payment_fragment, container, false);

        payOnline = (Button) rootView.findViewById(R.id.pay_online);
        payOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://metrokontact.com/member/buycode.php"));
                startActivity(browserIntent);
            }
        });
        return rootView;
    }
}
