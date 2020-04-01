package com.TombulsCaseSimulator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
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

public class MineSweeper extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Bitmap _mineNormal = null;
    Bitmap _mineBomb = null;
    Bitmap _mineClean = null;

    TextView _objectValue = null;
    TextView _objectPrize = null;
    TextView _objectNowPrize = null;

    Button _playButton = null;

    TextView _txtWallet = null;

    boolean game = false;

    int _mineRound = 0;
    int _multipler = 1;
    /*
                1X İçin

                - 2x2
                - 3x3 ..

                3X İçin

                - 4x2
                - 4x3

                5x İçin

                - 5x2
                - 5x3

                24X İçin

                - 1200x2

             */

    int _prize = 1;

            /*
                1X İçin

                [1 - 3] : %2
                [4 - 6] : %3
                [7 - 9] : %4
                ...........
             */

    int [] _mineMap = {};
    int _minePrize = 0;
    int _totalPrize = 0;
    int _nextPrize = 0;
    int _playValue = 0;
    int _mineCount = 1;

    boolean winorLose = false;

    DataBase _db = new DataBase(this);
    ToolClass toolBox = null;

    InterstitialAd adsPopUp = null;
    RewardedVideoAd adsVideo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_sweeper);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //region Pop Up Ads

        adsPopUp = new InterstitialAd(this);

        adsPopUp.setAdUnitId(getString(R.string.AD_INTERSTITIAL));

        AdRequest adRequest = new AdRequest.Builder().build();

        adsPopUp.loadAd(adRequest);

        //region


        //region Tanımlamalar

        toolBox = new ToolClass(getApplicationContext());

        _objectValue = (TextView)findViewById(R.id.tpValue);
        _objectNowPrize = (TextView)findViewById(R.id.twPrize);
        _objectPrize = (TextView)findViewById(R.id.twNextPrize);

        //endregion

        //region Mayın Tarlası Burda Oluşturuluyor

        _mineNormal = toolBox.getBitmap("ui/mine_normal.png", 64,64);
        _mineBomb = toolBox.getBitmap("ui/mine_bomb.png",64,64);
        _mineClean = toolBox.getBitmap("ui/mine_clean.png",64,64);

        //endregion

        //region Radio Buttonlar

        final RadioButton _rdX1 = (RadioButton)findViewById(R.id.rdx1), _rdX3 = (RadioButton)findViewById(R.id.rdx3), _rdX5 = (RadioButton)findViewById(R.id.rdx5), _rdX24 = (RadioButton)findViewById(R.id.rdx24);

        _rdX1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    _mineCount = 1;
                    _rdX1.setButtonDrawable(R.mipmap.checked);
                }else{
                    _rdX1.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });

        _rdX3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    _mineCount = 3;
                    _rdX3.setButtonDrawable(R.mipmap.checked);
                }else{
                    _rdX3.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });

        _rdX5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    _mineCount = 5;
                    _rdX5.setButtonDrawable(R.mipmap.checked);
                }else{
                    _rdX5.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });


        _rdX24.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    _mineCount = 24;
                    _rdX24.setButtonDrawable(R.mipmap.checked);
                }else{
                    _rdX24.setButtonDrawable(R.mipmap.nocheck);
                }
            }
        });

        //endregion

        _playButton = (Button)findViewById(R.id.btnPlay);

        _playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                if(game){
                    _playButton.setText(getString(R.string.play_game));
                    mineFinish();
                }else{
                    _playButton.setText(getString(R.string.widthdraw));
                    buildMineSweeper();
                    _objectNowPrize.setText(getString(R.string.prize)+toolBox.generateMoneyString(_playValue));
                    int b = ((_mineRound+1)/3)+2;
                    int a =  (_playValue/100*b)*(_multipler);
                    _objectPrize.setText(getString(R.string.prize)+toolBox.generateMoneyString(a));
                }
            }
        });

        //region Reklam

        AdView _adView = (AdView)findViewById(R.id.banner_mine);

        toolBox.buildAdsBanner(_adView);

        //endregion

        ImageView _onBackPressed = (ImageView)findViewById(R.id.backMenu);

        _onBackPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                onBackPressed();
            }
        });

        _txtWallet = (TextView)findViewById(R.id.wallet);

        _txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));

        //region ADS VIDEO

        adsVideo = MobileAds.getRewardedVideoAdInstance(this);

        adsVideo.loadAd(getString(R.string.AD_REWARDED_VIDEO), new AdRequest.Builder().build());

        _txtWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adsVideo.isLoaded()){
                    adsVideo.show();
                    _db.setWallet(2000);
                    _txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                }
            }
        });

        //endregion

        buildNavBar();

    }

    public void buildMineSweeper(){

        if(_objectValue.getText().toString().equals("")){
            Toast.makeText(this, getString(R.string.minvalue), Toast.LENGTH_SHORT).show();
            _playButton.setText(getString(R.string.play_game));
            return;
        }

        _playValue = Integer.valueOf(_objectValue.getText().toString());

        if(_playValue == 0){
            Toast.makeText(this, getString(R.string.minvalue), Toast.LENGTH_SHORT).show();
            _playButton.setText(getString(R.string.play_game));
            return;
        }

        if(_playValue > _db.getWallet()){
            toolBox.playSound(R.raw.soundnomoney);
            _playButton.setText(getString(R.string.play_game));
            Toast.makeText(this, getString(R.string.wallet), Toast.LENGTH_SHORT).show();
            return;
        }

        mineHide();

        int b = ((_mineRound+1)/3)+2;
        int _tPrize =  (_playValue/100*b)*(_multipler)+_prize;




        _objectNowPrize.setText(getString(R.string.prize)+toolBox.generateMoneyString(_totalPrize+_nextPrize));
        _objectPrize.setText(getString(R.string.next_prize)+toolBox.generateMoneyString(_tPrize));


        int _hiddenMine = _mineCount;
        _minePrize = 25-_mineCount;


       _mineMap = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

        while (_hiddenMine != 0){
            Random _rand = new Random();
            int id = _rand.nextInt(25);

            while (_mineMap[id] == 1){
                id = _rand.nextInt(25);
            }
            _mineMap[id] = 1;
            _hiddenMine--;
        }

        if(_mineCount != 24){
            _multipler = _mineCount+1;
        }else{
            _multipler = 1200;
        }

        GridLayout _father = (GridLayout)findViewById(R.id.iAmMinesFather);

        for (int i = 0; i < 25;i++){
            final ImageView _mineButton = (ImageView)(findViewById(getResources().getIdentifier("mine"+i, "id", getPackageName())));

            _mineButton.setId(getResources().getIdentifier("mine"+i, "id", getPackageName()));
            _mineButton.setImageBitmap(_mineNormal);
            _mineButton.setTag(_mineMap[i]);

            if(_mineMap[i] == 0){
                _mineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO playSound(R.raw.mineWin);
                        if(game){
                            winorLose = true;
                        _mineButton.setImageBitmap(_mineClean);
                        mineClean();
                            _mineButton.setClickable(false);
                        }
                    }
                });
            }else{
                _mineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Lose TODO
                        //TODO playSound(R.raw.mineLose);
                        if(game){
                            winorLose = false;
                            _mineButton.setImageBitmap(_mineBomb);
                            mineLose();
                            _playButton.setText(getString(R.string.play_game));
                        }
                    }
                });
            }
        }

        game = true;
    }

    public void mineClean(){
        _mineRound++;

        int a = (_mineRound/3)+2;
        int _nowPrize = (_playValue/100*a)*(_multipler);

        _totalPrize += _nowPrize;

        int b = ((_mineRound+1)/3)+2;
        _nextPrize =  (_playValue/100*b)*(_multipler);

        _objectNowPrize.setText(getString(R.string.prize)+toolBox.generateMoneyString(_totalPrize+_playValue));
        _objectPrize.setText(getString(R.string.next_prize)+toolBox.generateMoneyString(_nextPrize));

        if(_minePrize == _mineRound+1){
            // finish game TODO

            mineFinish();
        }

        winorLose = true;
    }

    public void mineLose(){
        _totalPrize = (-1*_playValue);
        game = false;
        mineFinish();
    }

    public void mineFinish(){
        _db.setWallet(_totalPrize);

        _txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
        game = false;

        mineShow();

        _nextPrize = 0;
        _totalPrize = 0;
        _mineRound = 0;
        _prize = 1;
        _minePrize = 0;

        String _text = "";
        int _color = 0;

        if(winorLose){
            _color = R.color.win;
            _text = getString(R.string.win)+toolBox.generateMoneyString(_totalPrize+_playValue);
        }else{
            _color = R.color.lose;
            _text = getString(R.string.lose)+toolBox.generateMoneyString(_playValue);
        }


        if(toolBox.gainXP(1)){
            Intent _levelUp = new Intent(getApplicationContext(),LevelUp.class);
            startActivity(_levelUp);
        }

        if(adsPopUp.isLoaded()){
            adsPopUp.show();
        }

    }

    public void mineShow(){
        for (int i = 0; i < 25;i++){
            ImageView _image = (ImageView)findViewById(getResources().getIdentifier("mine"+i, "id", getPackageName()));

            if(Integer.parseInt(_image.getTag().toString()) == 0){
                _image.setImageBitmap(_mineClean);
            }else{
                _image.setImageBitmap(_mineBomb);
            }
        }
    }

    public void mineHide(){
        for (int i = 0; i < 25;i++){
            ImageView _image = (ImageView)findViewById(getResources().getIdentifier("mine"+i, "id", getPackageName()));

            _image.setImageBitmap(_mineNormal);
            _image.setClickable(true);
        }
    }

    public void updateWallet(){

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
        int id = item.getItemId();

       startActivity(toolBox.navigationSelect(id));

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

        ivAvatar.setImageBitmap(toolBox.getBitmap("avatar/avatar"+_db.getConfig("Avatar")+".png",0,0));

        ProgressBar pbXP = (ProgressBar)headerView.findViewById(R.id.progressBar4);

        pbXP.setMax(20);
        pbXP.setProgress(Integer.parseInt(_db.getConfig("XP")));
    }
}
