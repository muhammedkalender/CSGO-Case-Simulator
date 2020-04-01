package com.TombulsCaseSimulator;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import java.util.Random;

public class Crash extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ToolClass toolBox = null;
    DataBase _db = null;

    TextView txtWallet = null, oTxtCrush = null, oTxtValue = null;

    Button oButtonPlayGame = null;

    InterstitialAd adsPopUp = null;
    RewardedVideoAd adsVideo = null;

    int intPlayValue = 0;

    int intMultipler = 0, intWidthdraw = 0, intMultiplerNow = 101;

    int intMaxPot = 0, intMaxGain = 0, intMaxRatio = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //region Tanımlamalar

        toolBox = new ToolClass(this);
        _db = new DataBase(this);

        txtWallet = (TextView)findViewById(R.id.wallet);
        oTxtCrush = (TextView)findViewById(R.id.oTxtCrush);

        oTxtValue = (TextView)findViewById(R.id.oTxtValue);

        oButtonPlayGame = (Button)findViewById(R.id.oButtonPlay);

        txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));

        //endregion

        //region ADS POP_UP

        adsPopUp = new InterstitialAd(this);

        adsPopUp.setAdUnitId(getString(R.string.AD_INTERSTITIAL));

        AdRequest adRequest = new AdRequest.Builder().build();

        adsPopUp.loadAd(adRequest);

        //endregion

        //region ADS VIDEO

        adsVideo = MobileAds.getRewardedVideoAdInstance(this);

        adsVideo.loadAd(getString(R.string.AD_REWARDED_VIDEO), new AdRequest.Builder().build());

        txtWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adsVideo.isLoaded()){
                    adsVideo.show();
                    _db.setWallet(R.integer.gainvalue);
                    txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                    Toast.makeText(Crash.this, getString(R.string.yougain), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //endregion

        //region ADS BANNER

        AdView _adView = (AdView)findViewById(R.id.adsBannerCrush);

        toolBox.buildAdsBanner(_adView);

        //endregion

        //region Tıklamalar

        oButtonPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame();
            }
        });

        //todo geri gitme tuşu

        //endregion

        buildNavBar();

        txtWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adsVideo.isLoaded()){
                    adsVideo.show();
                    _db.setWallet(2000);
                    txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                }
            }
        });

        ImageView _ib = (ImageView)findViewById(R.id.backMenu);

        _ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        intMaxGain = _db.getStatic("CRASH_MAX_GAIN");
        intMaxPot = _db.getStatic("CRASH_MAX_POT");
        intMaxRatio = _db.getStatic("CRASH_MAX_RATIO");

    }

    public void playGame(){
        
        if(oTxtValue.getText().toString().equals("")){
            Toast.makeText(this, getString(R.string.minvalue), Toast.LENGTH_SHORT).show();
            return;
        }
        
        intPlayValue = Integer.parseInt(oTxtValue.getText().toString());

        if(intPlayValue == 0){
            Toast.makeText(this, getString(R.string.minvalue), Toast.LENGTH_SHORT).show();
            return;
        }

        if(intPlayValue > _db.getWallet()){
            Toast.makeText(this, getString(R.string.wallet),Toast.LENGTH_SHORT).show();
            return;
        }

        oTxtCrush.setTextColor(getResources().getColor(R.color.textcolor));

        oTxtCrush.setText(getString(R.string.widthdraw));

        int intChance = new Random().nextInt(1001);

        if (intChance < 10){
            intMultipler = new Random().nextInt(900)+100;
        }else if(intChance < 50){
            intMultipler = new Random().nextInt(50)+50;
        }else if(intChance < 150){
            intMultipler = new Random().nextInt(20)+30;
        }else if(intChance < 400){
            intMultipler = new Random().nextInt(10)+20;
        }else if(intChance < 800){
            intMultipler = new Random().nextInt(10)+10;
        }else{
            intMultipler = 0;
        }

        oButtonPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intWidthdraw = intMultiplerNow;

                oButtonPlayGame.setText(getString(R.string.widthdraw));

                oButtonPlayGame.setAlpha(0.7f);
            }
        });


        new CountDownTimer(((intMultipler/4))*200, 200){

            @Override
            public void onTick(long millisUntilFinished) {
              intMultiplerNow += 4;

                oTxtCrush.setText(String.valueOf((double)intMultiplerNow/100));
            }

            @Override
            public void onFinish() {
                intMultiplerNow = intMultipler+100;
                oTxtCrush.setText(String.valueOf((double)(intMultiplerNow)/100));


                int intXP = 50;

                if(intPlayValue > intMaxPot){
                    _db.setStatic("CRASH_MAX_POT",intPlayValue);
                }

                _db.setStatic("CRASH_GAME",1);

                if(intWidthdraw != 0){
                    _db.setStatic("CRASH_WIN", 1);
                    _db.setStatic("CRASH_POT_WIN", (intWidthdraw*intPlayValue));

                    if(intWidthdraw > intMaxGain){
                        _db.setStatic("CRASH_MAX_GAIN",(intWidthdraw*intPlayValue));
                    }

                    if(intWidthdraw > intMaxRatio){
                        _db.setStatic("CRASH_MAX_RATIO", intWidthdraw);
                    }

                    oTxtCrush.setTextColor(getResources().getColor(R.color.win));
                    _db.setWallet((intWidthdraw*intPlayValue)/100);
                    txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                    intXP = 150;
                }else{
                    _db.setStatic("CRASH_LOSE", 1);
                    _db.setStatic("CRASH_POT_LOSE", intPlayValue);
                    oTxtCrush.setTextColor(getResources().getColor(R.color.lose));
                    _db.setWallet(-1*intPlayValue);
                    txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                }

                intMultiplerNow = 100;
                intWidthdraw = 0;

                oButtonPlayGame.setText(getString(R.string.play));
                oButtonPlayGame.setAlpha(1);

                oButtonPlayGame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playGame();
                    }
                });

                if(toolBox.gainXP(intXP)){
                    startActivity(new Intent(getApplicationContext(), LevelUp.class));
                }

            }
        }.start();


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
        // Handle navigation view item clicks here.

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

        pbXP.setMax(5000);
        pbXP.setProgress(Integer.parseInt(_db.getConfig("XP")));
    }
}
