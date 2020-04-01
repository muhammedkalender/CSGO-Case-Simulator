package com.TombulsCaseSimulator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
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

public class Dice extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int _selected = 0;
    public static TextView txtWallet = null;
    LinearLayout _walletButton = null;
    DataBase _db = new DataBase(this);
    ToolClass toolBox = null;

    InterstitialAd adsPopUp = null;
    RewardedVideoAd adsVideo = null;

    int intMaxPot = 0, intMaxGain = 0;

    boolean boolGame = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolBox = new ToolClass(this);

        adsPopUp = new InterstitialAd(this);

        adsPopUp.setAdUnitId(getString(R.string.AD_INTERSTITIAL));

        AdRequest adRequest = new AdRequest.Builder().build();

        adsPopUp.loadAd(adRequest);

        AdView _adView = (AdView)findViewById(R.id.bannerDice);

        toolBox.buildAdsBanner(_adView);

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

        final RadioButton _rd1 = (RadioButton)findViewById(R.id.rd1);
        final RadioButton _rd2 = (RadioButton)findViewById(R.id.rd2);
        final RadioButton _rd3 = (RadioButton)findViewById(R.id.rd3);
        final RadioButton _rd4 = (RadioButton)findViewById(R.id.rd4);
        final RadioButton _rd5 = (RadioButton)findViewById(R.id.rd5);
        final RadioButton _rd6 = (RadioButton)findViewById(R.id.rd6);

        _rd1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if(isChecked){
                    buttonView.setButtonDrawable(R.mipmap.checked);
                    toolBox.playSound(R.raw.soundclick);
                    _selected = 1;
                }else{
                    buttonView.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });

        _rd2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setButtonDrawable(R.mipmap.checked);
                    toolBox.playSound(R.raw.soundclick);
                    _selected = 2;
                }else{
                    buttonView.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });


        _rd3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setButtonDrawable(R.mipmap.checked);
                    toolBox.playSound(R.raw.soundclick);
                    _selected = 3;
                }else{
                    buttonView.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });

        _rd4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setButtonDrawable(R.mipmap.checked);
                    toolBox.playSound(R.raw.soundclick);
                    _selected = 4;
                }else{
                    buttonView.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });

        _rd5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setButtonDrawable(R.mipmap.checked);
                    toolBox.playSound(R.raw.soundclick);
                    _selected = 5;
                }else{
                    buttonView.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });
        _rd6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    buttonView.setButtonDrawable(R.mipmap.checked);
                    toolBox.playSound(R.raw.soundclick);
                    _selected = 6;
                }else{
                    buttonView.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });

        final Button _play = (Button)findViewById(R.id.okButton);




        _play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                Button _button = (Button)findViewById(R.id.okButton);

                _button.setClickable(false);
                _button.setAlpha(0.7f);
                playDice();


            }
        });

        buildNavBar();

        ImageView _onBackPressed = (ImageView)findViewById(R.id.backMenu);

        _onBackPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                onBackPressed();
            }
        });

        intMaxGain = _db.getStatic("DICE_MAX_GAIN");
        intMaxPot = _db.getStatic("DICE_MAX_POT");
    }

    public void playDice(){

        TextView _txtValue = (TextView)findViewById(R.id.miktar);

        if(_txtValue.getText().toString().equals("")){
            Toast.makeText(this, getString(R.string.minvalue), Toast.LENGTH_SHORT).show();
            return;
        }

        final int[] value = {Integer.parseInt(String.valueOf(_txtValue.getText()))};

        if(value[0] == 0){
            Toast.makeText(this, getString(R.string.minvalue), Toast.LENGTH_SHORT).show();
            return;
        }

        final int[] currentDice = { 1 };

        if(value[0] > _db.getWallet()){
            toolBox.playSound(R.raw.soundnomoney);
            Toast.makeText(this, getString(R.string.wallet), Toast.LENGTH_SHORT).show();
            Button _button = (Button)findViewById(R.id.okButton);

            _button.setClickable(true);
            _button.setAlpha(1);
            return;
        }


        final ToolClass toolBox = new ToolClass(getApplicationContext());

        final  Bitmap[] _imageList = {toolBox.getBitmap("ui/dice1.png", 0,0),toolBox.getBitmap("ui/dice1.png", 0,0),toolBox.getBitmap("ui/dice2.png", 0,0),toolBox.getBitmap("ui/dice3.png", 0,0),toolBox.getBitmap("ui/dice4.png", 0,0),toolBox.getBitmap("ui/dice5.png", 0,0),toolBox.getBitmap("ui/dice6.png", 0,0)};




        final ImageView _image = (ImageView)findViewById(R.id.imageView2);

        if (_txtValue.getText().length() == 0 ||_selected == 0){
            Toast.makeText(this, getString(R.string.mindice),Toast.LENGTH_SHORT).show();
            Button _button = (Button)findViewById(R.id.okButton);

            _button.setClickable(true);
            _button.setAlpha(1);
            return;
        }


        final AlphaAnimation blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(500); // duration - half a second
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(1); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);
        boolGame = false;
        new CountDownTimer(4000,500){

            @Override
            public void onTick(long millisUntilFinished) {
                Random _rand = new Random();

                currentDice[0] = _rand.nextInt(6)+1;

                _image.setImageBitmap(_imageList[currentDice[0]]);

                _image.startAnimation(blinkanimation);
            }

            @Override
            public void onFinish() {
                _db.setStatic("DICE_GAME", 1);

                _db.setStatic("DICE_CHOOSE_"+currentDice[0],1);

                if(value[0] > intMaxPot){
                    _db.setStatic("DICE_MAX_POT",value[0]);
                    intMaxPot = value[0];
                }

                int color = 0, result = 0, intXP = 50;

                if(_selected == currentDice[0]){
                    toolBox.playSound(R.raw.soundlevelup);
                    color = R.color.win;
                    value[0] *= 2;
                    _db.setWallet(value[0]);
                    txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                    intXP = 150;

                    _db.setStatic("DICE_WIN", 1);
                    _db.setStatic("DICE_POT_WIN",value[0]);

                    if(value[0]/2 > intMaxGain){
                        _db.setStatic("DICE_MAX_GAIN", value[0]);
                        intMaxGain = value[0]/2;
                    }
                }else{
                    toolBox.playSound(R.raw.droprare);
                    color = R.color.lose;
                    _db.setWallet(value[0] *(-1));
                    txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));

                    _db.setStatic("DICE_LOSE", 1);
                    _db.setStatic("DICE_POT_LOSE",value[0]);
                }

                LinearLayout _result = (LinearLayout)findViewById(R.id.resultList);

                LinearLayout child = new LinearLayout(getApplicationContext());

                child.setOrientation(LinearLayout.HORIZONTAL);

                TextView _play = toolBox.customTextView(String.valueOf(_selected),-1,12,true);
                TextView _win = toolBox.customTextView(String.valueOf(currentDice[0]), -1,12,true);
                TextView _value = toolBox.customTextView(String.valueOf(value[0]), -1,12,true);


                _play.setBackgroundColor(getResources().getColor(color));
                _win.setBackgroundColor(getResources().getColor(color));
                _value.setBackgroundColor(getResources().getColor(color));



                LinearLayout.LayoutParams _param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);

                _play.setLayoutParams(_param);
                _win.setLayoutParams(_param);
                _value.setLayoutParams(_param);


                child.addView(_play);
                child.addView(_win);
                child.addView(_value);

                _result.addView(child);

                Button _button = (Button)findViewById(R.id.okButton);

                _button.setClickable(true);
                _button.setAlpha(1);
                _image.startAnimation(blinkanimation);

                if(toolBox.gainXP(intXP)){
                    startActivity(new Intent(getApplicationContext(), LevelUp.class));
                }

                if(adsPopUp.isLoaded()){
                    adsPopUp.show();
                }

                boolGame = true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
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

        pbXP.setMax(5000);
        pbXP.setProgress(Integer.parseInt(_db.getConfig("XP")));
    }
}
