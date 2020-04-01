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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import java.util.Random;

public class FlipCoin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static TextView txtWallet = null;

    int choose = 0;
    Button btnFunction  = null;
    TextView txtValue = null;

    RadioButton _t = null, _ct = null;

    boolean win = false;

    DataBase _db = new DataBase(this);
    LinearLayout _walletButton = null;

    InterstitialAd adsPopUp = null;
    RewardedVideoAd adsVideo = null;

    ToolClass toolBox = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flip_coin);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolBox = new ToolClass(this);

        //region Pop Up Ads

        adsPopUp = new InterstitialAd(this);

        adsPopUp.setAdUnitId(getString(R.string.AD_INTERSTITIAL));

        AdRequest adRequest = new AdRequest.Builder().build();

        adsPopUp.loadAd(adRequest);

        //endregion

        //region Reklam

        AdView _adView = (AdView)findViewById(R.id.bannerFlipCoin);

        toolBox.buildAdsBanner(_adView);

        //endregion

        _ct = (RadioButton) findViewById(R.id.rbCT);
        _t = (RadioButton) findViewById(R.id.rbT);

        _ct.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setBackgroundResource(R.mipmap.ghct);
                    choose = 1;
                }else{
                    buttonView.setBackgroundResource(R.mipmap.hct);
                }
            }
        });

        _t.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setBackgroundResource(R.mipmap.ght);
                    choose = 2;
                }else{
                    buttonView.setBackgroundResource(R.mipmap.ht);
                }
            }
        });

        btnFunction = (Button)findViewById(R.id.btnMain);
        txtValue = (TextView)findViewById(R.id.filipValue);

        btnFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                btnFunction.setClickable(false);
btnFunction.setAlpha(0.7f);
                playFlip();
            }
        });

        buildNavBar();

        txtWallet = (TextView)findViewById(R.id.wallet);

        txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));

        //region Video Ads

        adsVideo = MobileAds.getRewardedVideoAdInstance(this);

        adsVideo.loadAd(getString(R.string.AD_REWARDED_VIDEO), new AdRequest.Builder().build());

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
        //endregion

        ImageView _onBackPressed = (ImageView)findViewById(R.id.backMenu);

        _onBackPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                onBackPressed();
            }
        });
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

        ivAvatar.setImageBitmap(toolBox.getBitmap("avatar/avatar"+_db.getConfig("Avatar")+".png",0,0));

        ProgressBar pbXP = (ProgressBar)headerView.findViewById(R.id.progressBar4);

        pbXP.setMax(20);
        pbXP.setProgress(Integer.parseInt(_db.getConfig("XP")));
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        startActivity(toolBox.navigationSelect(id));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void playFlip(){
        btnFunction.setText(getString(R.string.flipping));

        if(choose == 0){
            Toast.makeText(this, getString(R.string.choosesidee1), Toast.LENGTH_SHORT).show();
            btnFunction.setText(getString(R.string.start));
            btnFunction.setClickable(true);
            btnFunction.setAlpha(1);
            return;
        }

        if(txtValue.getText().toString().equals("")){
            Toast.makeText(this, getString(R.string.minvalue), Toast.LENGTH_SHORT).show();
            return;
        }

        final int value = Integer.valueOf(txtValue.getText().toString());

        if(value == 0){
            Toast.makeText(this, getString(R.string.minvalue), Toast.LENGTH_SHORT).show();
            return;
        }

        if(value > _db.getWallet()){
            toolBox.playSound(R.raw.soundnomoney);
            Toast.makeText(this, getString(R.string.wallet), Toast.LENGTH_SHORT).show();
            btnFunction.setText(getString(R.string.start));
            btnFunction.setClickable(true);
            btnFunction.setAlpha(1);
            return;
        }



        _ct.setClickable(false);
        _t.setClickable(false);

        new CountDownTimer(2000,250){

            @Override
            public void onTick(long millisUntilFinished) {
                boolean result = new Random().nextBoolean();

                if(result){
                    // CT
                    _ct.setBackgroundResource(R.mipmap.ht1);
                    _t.setBackgroundResource(R.mipmap.hct1);
                }else{
                    // T
                    _ct.setBackgroundResource(R.mipmap.hct);
                    _t.setBackgroundResource(R.mipmap.ht);
                }
            }

            @Override
            public void onFinish() {
                boolean result = new Random().nextBoolean();

                win = result;
                _ct.setClickable(true);
                _t.setClickable(true);

                String _winString = "";

                boolean _result = false;

                if(win && choose == 1){
                    //WIN
                    _winString = "CT";
                    _result = true;
                }else if(!win && choose == 2){
                    //WIN
                    _winString = "T";
                    _result = true;
                }

                int  _subValue = value;

                int _color = 0;


                if(_result){
                    toolBox.playSound(R.raw.soundlevelup);
                    _subValue *=  2;
                    _db.setWallet(_subValue);
                    _color = R.color.win;

                }else{
                    toolBox.playSound(R.raw.droprare);
                    _db.setWallet(_subValue*(-1));
                    _color = R.color.lose;
                }



                btnFunction.setClickable(true);
btnFunction.setAlpha(1);
                TextView wallet = (TextView)findViewById(R.id.wallet);

                wallet.setText(toolBox.generateMoneyString(_db.getWallet()));

                btnFunction.setText(getString(R.string.start));

                if(choose == 1){
                    _t.setBackgroundResource(R.mipmap.ht);
                    _ct.setBackgroundResource(R.mipmap.ghct);
                }else{
                    _t.setBackgroundResource(R.mipmap.ght);
                    _ct.setBackgroundResource(R.mipmap.hct);
                }

                if(toolBox.gainXP(1)){
                    Intent _levelUp = new Intent(getApplicationContext(),LevelUp.class);
                    startActivity(_levelUp);
                }

                if(adsPopUp.isLoaded()){
                    adsPopUp.show();
                    _db.setWallet(2000);
                }
            }
        }.start();

    }
}
