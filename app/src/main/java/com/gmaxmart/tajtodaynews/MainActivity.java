package com.gmaxmart.tajtodaynews;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    FragmentManager myFragmentManager;
    NavigationView navigationView;
    FragmentTransaction myFragmentTransaction;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setTitle("Taj Today News");
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        myFragmentManager = getSupportFragmentManager();
        myFragmentTransaction = myFragmentManager.beginTransaction();
        myFragmentTransaction.replace(R.id.containerView, new HomeFragment()).commit();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem selectedMenuItem) {
                drawer.closeDrawers();
                if (selectedMenuItem.getItemId() == R.id.nav_home) {
                    FragmentTransaction fragmentTransaction = myFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new HomeFragment()).commit();
                  }
                else if(selectedMenuItem.getItemId() == R.id.nav_saved){
                Intent i=new Intent(MainActivity.this,Saved_VAlue.class);
                startActivity(i);
                }
                else if(selectedMenuItem.getItemId() == R.id.nav_about){
                    Intent i=new Intent(MainActivity.this,About.class);
                    startActivity(i);
                }
                else if(selectedMenuItem.getItemId() == R.id.nav_video){
                    Intent i=new Intent(MainActivity.this,VideoYou.class);
                    startActivity(i);
                }
                else if(selectedMenuItem.getItemId() == R.id.nav_contact){
                    Intent i=new Intent(MainActivity.this,Contact.class);
                    startActivity(i);
                }
                else if (selectedMenuItem.getItemId() == R.id.nav_subs) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCC53PEU0PZC7OoElDA45TpA"));
                    startActivity(browserIntent);
                }
                else if (selectedMenuItem.getItemId() == R.id.nav_facebook) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/modiagainpm.org/"));
                    startActivity(browserIntent);
                }
                else if(selectedMenuItem.getItemId() == R.id.nav_share){
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Download Taj Today News app from playstore free"+"\n"+"https://play.google.com/store/apps/details?id=com.gmaxmart.tajtodaynews");
                    startActivity(Intent.createChooser(shareIntent, "Share Via..."));
                }

                return false;

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}