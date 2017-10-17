package com.greenmousetech.MetroKontact;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.greenmousetech.MetroKontact.LoginRegister.LoginActivity;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    Button findCategory, about, search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

  search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public boolean onOptionsItemSelected(android.view.MenuItem item) {
                if (item != null && item.getItemId() == R.id.MyMenu) {
                    if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                        drawer.closeDrawer(Gravity.RIGHT);
                    } else {
                        drawer.openDrawer(Gravity.RIGHT);
                    }
                    return true;
                }
                return false;
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view1);
        navigationView.setNavigationItemSelectedListener(this);

        Spinner spinner = (Spinner) findViewById(R.id.category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

about = (Button) findViewById(R.id.about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(MainActivity.this, About.class);
                startActivity(Intent);
            }
        });
        findCategory = (Button) findViewById(R.id.find_category);
        findCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FindCategory.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int ItemClicked = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (ItemClicked == R.id.login) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        }else if(ItemClicked == R.id.MyMenu){drawer.openDrawer(GravityCompat.END);
            return true;}

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.contact_us) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://metrokontact.com/contact.php"));
            startActivity(browserIntent);
        } else if (id == R.id.support) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://metrokontact.com/Support.php"));
            startActivity(browserIntent);

        } else if (id == R.id.disclaimer) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://metrokontact.com/disclaimers.php"));
            startActivity(browserIntent);
        } else if (id == R.id.Fees_charge) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://metrokontact.com/fees.php"));
            startActivity(browserIntent);
        } else if (id == R.id.privacy_policy) {
            Intent intent = new Intent(MainActivity.this, privacy_policy.class);
            startActivity(intent);
        }else if (id == R.id.site_content) {
            Intent intent = new Intent(MainActivity.this, FindCategory.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }



}
