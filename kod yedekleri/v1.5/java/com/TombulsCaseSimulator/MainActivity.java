package com.TombulsCaseSimulator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DataBase _db = new DataBase(this);
    ToolClass toolBox = null;
    public static TextView txtWallet = null;

    RewardedVideoAd adsVideo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences _first = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if(_first.getInt("FirstOpen", 1) == 1){
            FirstOpening();
            SharedPreferences.Editor _editor = _first.edit();
            _editor.putInt("FirstOpen", 0);
            _editor.commit();
        }

        toolBox = new ToolClass(this);

        buildNavBar();

        Button _gI = (Button)findViewById(R.id.goInventory),
                _gO = (Button)findViewById(R.id.goOpenCase),
                _gD = (Button)findViewById(R.id.goDice),
                _gF = (Button)findViewById(R.id.goFlipCoin),
                _gS = (Button)findViewById(R.id.goSettings),
                _gJ = (Button)findViewById(R.id.goJackpot),
                _gTU = (Button)findViewById(R.id.goTradeUp),
                _gC = (Button)findViewById(R.id.goCrush),
                _gMS = (Button)findViewById(R.id.goMineSweeper);

        final ImageView _ivAvatar2 = (ImageView)findViewById(R.id.ivAvatar2);

        _ivAvatar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _intent = new Intent(getApplicationContext(), ChangeAvatar.class);

                startActivity(_intent);
            }
        });

        _ivAvatar2.setImageBitmap(toolBox.getBitmap(_db.getAvatarUrl(Integer.parseInt(_db.getConfig("Avatar"))),0,0));


        final TextView _txtUserName = (TextView)findViewById(R.id.txtUserName), _txtLevel = (TextView)findViewById(R.id.txtLevel), _txtInventoryValue = (TextView)findViewById(R.id.txtInventory);
        txtWallet =(TextView)findViewById(R.id.txtWallet);

        //region ADS VIDEO

        adsVideo = MobileAds.getRewardedVideoAdInstance(this);

        adsVideo.loadAd(getString(R.string.AD_REWARDED_VIDEO), new AdRequest.Builder().build());

        //endregion

        _txtUserName.setText(_db.getConfig("UserName"));

        _txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout father = (LinearLayout)findViewById(R.id.fatherChanceUserName);
                toolBox.playSound(R.raw.soundclick);
                father.setVisibility(View.VISIBLE);

                final TextView _username = (TextView)findViewById(R.id.enterUsername);

                _username.setText(_db.getConfig("UserName"));

                Button _cancel = (Button)findViewById(R.id.buttonChanceUserNameCancel), _ok = (Button)findViewById(R.id.buttonChangeUserName);


                _cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        father.setVisibility(View.INVISIBLE);
                        toolBox.playSound(R.raw.soundclick);
                    }
                });

                _ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _db.setConfig("UserName", String.valueOf(_username.getText()));

                        Toast.makeText(getApplicationContext(), getString(R.string.chancedUserName) + _db.getConfig("UserName"), Toast.LENGTH_SHORT).show();

                        _txtUserName.setText(_db.getConfig("UserName"));

                        father.setVisibility(View.INVISIBLE);
                        toolBox.playSound(R.raw.soundclick);
                    }
                });
            }
        });


        _txtLevel.setText(getString(R.string.textLevel)+" "+_db.getConfig("Level"));
        txtWallet.setText(getString(R.string.cash)+" "+ toolBox.generateMoneyString(_db.getWallet()));
        _txtInventoryValue.setText(getString(R.string.textInventoryValue)+" "+toolBox.generateMoneyString(_db.getInventoryValue()));

        ImageView _ivLevel = (ImageView)findViewById(R.id.imgLevel);

        int intLevel = Integer.parseInt(_db.getConfig("Level"));

        if(intLevel >= 40){
            _ivLevel.setImageBitmap(toolBox.getBitmap("level/level40.png", 0,0));
        }else{
            _ivLevel.setImageBitmap(toolBox.getBitmap("level/level"+intLevel+".png", 0,0));
        }



        txtWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adsVideo.isLoaded()){
                    adsVideo.show();
                }
            }
        });

        _gI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                Intent _intent =  new Intent(getApplicationContext(), Inventory.class);
                startActivity(_intent);
            }
        });

        _gO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                Intent _intent =  new Intent(getApplicationContext(), OpenCase.class);
                startActivity(_intent);
            }
        });

        _gD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                Intent _intent =  new Intent(getApplicationContext(), Dice.class);
                startActivity(_intent);
            }
        });

        _gF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                Intent _intent =  new Intent(getApplicationContext(), FlipCoin.class);
                startActivity(_intent);
            }
        });

        _gS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                Intent _intent =  new Intent(getApplicationContext(), Settings.class);
                startActivity(_intent);
            }
        });

        _gJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                //Intent _intent =  new Intent(getApplicationContext(), Jackpot.class);
                Intent _intent =  new Intent(getApplicationContext(), Jackpot.class);
                startActivity(_intent);
            }
        });

        _gTU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                //Intent _intent =  new Intent(getApplicationContext(), Jackpot.class);
                Intent _intent =  new Intent(getApplicationContext(), TradeUp.class);
                startActivity(_intent);
            }
        });

        _gMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                //Intent _intent =  new Intent(getApplicationContext(), Jackpot.class);
                Intent _intent =  new Intent(getApplicationContext(), MineSweeper.class);
                startActivity(_intent);
            }
        });


        _gC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                Intent _intent =  new Intent(getApplicationContext(), Crash.class);
                startActivity(_intent);
            }
        });

        clearTemplate();
        checkUpdate();

        AdView _adView = (AdView)findViewById(R.id.bannerMain);

        toolBox.buildAdsBanner(_adView);
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
        finish();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void FirstOpening(){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("CaseSimulator.sql")));

            // do reading, usually loop until end of file reading
            StringBuilder sb = new StringBuilder();
            String mLine = reader.readLine();
            while (mLine != null) {
                sb.append(mLine); // process line
                mLine = reader.readLine();
            }
            reader.close();

            String[] _str = sb.toString().split(";");

            for (int i = 0; i < _str.length; i++) {
                _db.addQuery(_str[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("OPEN", String.valueOf(e));
            return;
        }
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

    public void clearTemplate(){
        _db.clearJackpot();
        _db.clearTradeUp();
    }

    public void checkUpdate(){
        if(Integer.parseInt(_db.getConfig("Version")) < 4){

            int intLevel = Integer.parseInt(_db.getConfig("Level"));
            int intXP = Integer.parseInt(_db.getConfig("XP"));

            if(intXP / 5000 < 1){
                _db.setConfig("Level","1");
            }else{
                _db.setConfig("Level", String.valueOf(((intLevel*50)+intXP/5000)+1));
            }

            _db.setConfig("XP", String.valueOf(intXP%5000));

            _db.setConfig("Version", "4");
        }

        if(Integer.parseInt(_db.getConfig("Version")) < 5){
            _db.addQuery("CREATE TABLE 'statics' ('TAG' INTEGER, 'VALUE' INTEGER);");
            _db.addQuery("INSERT INTO statics ('TAG') VALUES ('MINE_WIN'),('MINE_LOSE'),('MINE_POT_WIN'),('MINE_POT_LOSE'),('MINE_CLEAN'),('MINE_GAME_MODE_X1'),('MINE_GAME_MODE_X3'),('MINE_GAME_MODE_X5'),('MINE_GAME_MODE_24'),('MINE_MAX_POT'),('MINE_MAX_GAIN'),('TRADE_UP_TYPE_0'),('TRADE_UP_TYPE_0_STATTRAK'),('TRADE_UP_TYPE_1'),('TRADE_UP_TYPE_1_STATTRAK'),('TRADE_UP_TYPE_2'),('TRADE_UP_TYPE_2_STATTRAK'),('TRADE_UP_TYPE_3'),('TRADE_UP_TYPE_3_STATTRAK'),('TRADE_UP_TYPE_4'),('TRADE_UP_TYPE_4_STATTRAK'),('CASE_OPENING'),('CASE_1'),('CASE_2'),('CASE_3'),('CASE_4'),('CASE_5'),('CASE_7'),('CASE_10'),('CASE_11'),('CASE_17'),('CASE_18'),('CASE_19'),('CASE_29'),('CASE_38'),('CASE_48'),('CASE_50'),('CASE_80'),('CASE_111'),('CASE_112'),('CASE_141'),('CASE_144'),('CASE_172'),('CASE_179'),('CASE_207'),('CASE_WIN_0'),('CASE_WIN_1'),('CASE_WIN_2'),('CASE_WIN_3'),('CASE_WIN_4'),('CASE_WIN_5'),('CASE_WIN_SPECIAL'),('CASE_WIN_STATTRAK'),('CASE_WIN_STATTRAK_0'),('CASE_WIN_STATTRAK_1'),('CASE_WIN_STATTRAK_2'),('CASE_WIN_STATTRAK_3'),('CASE_WIN_STATTRAK_4'),('CASE_WIN_STATTRAK_5'),('CASE_WIN_STATTRAK_6'),('CRASH_GAME'),('CRASH_MAX_RATIO'),('CRASH_WIN'),('CRASH_LOSE'),('CRASH_POT_WIN'),('CRASH_POT_LOSE'),('CRASH_MAX_GAIN'),('CRASH_MAX_POT'),('DICE_GAME'),('DICE_CHOOSE_1'),('DICE_CHOOSE_2'),('DICE_CHOOSE_3'),('DICE_CHOOSE_4'),('DICE_CHOOSE_5'),('DICE_CHOOSE_6'),('DICE_WIN'),('DICE_LOSE'),('DICE_POT_WIN'),('DICE_POT_LOSE'),('DICE_MAX_POT'),('DICE_MAX_GAIN'),('FLIP_COIN_GAME'),('FLIP_COIN_CHOOSE_CT'),('FLIP_COIN_CHOOSE_T'),('FLIP_COIN_WIN'),('FLIP_COIN_LOSE'),('FLIP_COIN_POT_WIN'),('FLIP_COIN_POT_LOSE'),('FLIP_COIN_MAX_POT'),('FLIP_COIN_MAX_GAIN'),('JACKPOT_GAME'),('JACKPOT_WIN'),('JACKPOT_LOSE'),('JACKPOT_POT_WIN'),('JACKPOT_POT_LOSE'),('JACKPOT_MAX_GAIN'),('JACKPOT_MAX_POT'),('TRADE_UP')");

            _db.setConfig("Version", "5");
        }
    }
}
