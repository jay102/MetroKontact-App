package com.greenmousetech.MetroKontact.UserInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.greenmousetech.MetroKontact.MainActivity;
import com.greenmousetech.MetroKontact.MapActivity;
import com.greenmousetech.MetroKontact.R;
import com.greenmousetech.MetroKontact.SessionManagement;

import static com.greenmousetech.MetroKontact.LoginRegister.LoginActivity.USERNAME;

public class DashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private boolean viewIsAtHome;
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SharedPreferences loginPreferences = getSharedPreferences(USERNAME, Context.MODE_PRIVATE);
        String fullname = loginPreferences.getString("PersonalName","");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView userEmail = (TextView) header.findViewById(R.id.user_email);
        navigationView.setNavigationItemSelectedListener(this);
        displayView(R.id.dashboard);
        userEmail.setText(fullname);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } if (!viewIsAtHome) {
            displayView(R.id.dashboard);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.map) {
            Intent intent = new Intent(DashBoard.this, MapActivity.class);
            startActivity(intent);

            return true;
        }else if(id == R.id.homepage){
            Intent intent = new Intent(DashBoard.this, MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displayView(item.getItemId());
        return true;
}

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.dashboard:
                fragment = new dashboard_fragment();
                title  = "DashBoard";
                viewIsAtHome = true;
                break;
            case R.id.add_business:
                fragment = new add_business_fragment();
                title = "Add Business";
                viewIsAtHome = false;
                break;
            case R.id.logout:
                Toast.makeText(getApplicationContext(), "Logging Out Successfully..", Toast.LENGTH_LONG).show();
                session.logoutUser();
                break;
            case R.id.change_password:
                fragment = new change_password_fragment();
                viewIsAtHome = false;
                break;
            case R.id.delete_business:
                fragment = new delete_business_fragment();
                viewIsAtHome = false;
                break;
            case R.id.activate_business:
                fragment = new activate_business_fragment();
                viewIsAtHome = false;
                break;
            case R.id.edit_business:
                fragment = new edit_business_fragment();
                viewIsAtHome = false;
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onRestart() {
        session.checkLogin();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        session.checkLogin();
        super.onPause();
    }

    @Override
    protected void onResume() {
        session.checkLogin();
        super.onResume();
    }
}
