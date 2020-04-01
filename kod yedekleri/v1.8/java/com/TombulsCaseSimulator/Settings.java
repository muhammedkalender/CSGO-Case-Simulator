package com.TombulsCaseSimulator;

import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

public class Settings extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static TextView txtWallet = null;
    DataBase _db = new DataBase(this);
    ToolClass toolBox = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolBox = new ToolClass(this);

        buildNavBar();

        ImageView _onBackPressed = (ImageView)findViewById(R.id.backMenu);

        _onBackPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Switch swSound = (Switch)findViewById(R.id.swSound);

        final DataBase _db = new DataBase(getApplicationContext());

        if(Integer.parseInt(_db.getConfig("SOUND")) != 1){
            swSound.setChecked(false);
        }

        swSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    _db.setConfig("SOUND", "1");
                    swSound.setChecked(true);
                }else{
                    _db.setConfig("SOUND", "0");
                    swSound.setChecked(false);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        startActivity(toolBox.navigationSelect(item.getItemId()));
        finish();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void buildNavBar(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        ToolClass toolBox = new ToolClass(this);
        ImageView ivAvatar = (ImageView) headerView.findViewById(R.id.navAvatar);

        TextView txtUserName = (TextView)headerView.findViewById(R.id.navUserName); //toolBox.customTextView(_db.getConfig("UserName"),0,10,false);
        TextView txtCash = (TextView)headerView.findViewById(R.id.navCash); //toolBox.customTextView(toolBox.generateMoneyString(_db.getWallet()),0,10,false);
        TextView txtLevel = (TextView)headerView.findViewById(R.id.navLevel); //toolBox.customTextView(_db.getConfig("Level"), 0,10,false);

        txtUserName.setText(_db.getConfig("UserName"));
        txtCash.setText(toolBox.generateMoneyString(_db.getWallet()));
        txtLevel.setText("Level :"+_db.getConfig("Level"));

        ivAvatar.setImageBitmap(toolBox.getBitmap(_db.getAvatarUrl(Integer.parseInt(_db.getConfig("Avatar"))),0,0));

        ProgressBar pbXP = (ProgressBar)headerView.findViewById(R.id.progressBar4);

        pbXP.setMax(20);
        pbXP.setProgress(Integer.parseInt(_db.getConfig("XP")));
    }
}
