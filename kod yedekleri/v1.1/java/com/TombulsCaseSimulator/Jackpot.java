package com.TombulsCaseSimulator;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import java.util.Random;

public class Jackpot extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button oButtonPlayGame = null,  oButtonAddItem = null, oButtonOk = null, oButtonCancel = null;
    ImageView oIVBack = null, oIVNext = null;
    HorizontalScrollView oScrool = null;
    TextView oTxtChance = null, oTxtItemCount = null, oTxtTotalPrize = null, oTxtSelectPot = null, oTxtSelectCount = null;
    LinearLayout oLLSelectItem = null, oLLScroolArea = null, oLLItemsArea = null;
    GridLayout oGLInventory = null;
    ToolClass toolBox = null;
    DataBase _db = new DataBase(this);
    RelativeLayout oRLScroolFather = null;
    View.OnClickListener clickPlay = null, clickConfirm = null;
    String winQuery = "";

    RelativeLayout _scrool = null;

    int intPage = 0, intMaxPage, intTotalPrize ,intItemCount, totalPot = 0, userPot = 0, intChance = 0;

    int[] itemList = null;

    InterstitialAd adsPopUp = null;
    RewardedVideoAd adsVideo = null;

    TextView txtWallet = null;

    boolean boolWin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jackpot);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        txtWallet = (TextView)findViewById(R.id.wallet) ;

        toolBox = new ToolClass(this);
        _db = new DataBase(this);

        txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));

        //region Pop Up Ads

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
                }
            }
        });

        //endregion

        oButtonOk = (Button)findViewById(R.id.btnOK);

        oButtonAddItem = (Button)findViewById(R.id.btnAddItem);
        oButtonPlayGame  = (Button)findViewById(R.id.btnStart);


        oButtonCancel = (Button)findViewById(R.id.btnCancel);

        oTxtChance = (TextView)findViewById(R.id.txtChance);
        oTxtItemCount = (TextView)findViewById(R.id.txtItemCount);
        oTxtTotalPrize = (TextView)findViewById(R.id.txtTotalPrize);
        oTxtSelectPot = (TextView)findViewById(R.id.txtPot);
        oTxtSelectCount = (TextView)findViewById(R.id.txtSLCItemCount);

        oLLSelectItem = (LinearLayout)findViewById(R.id.selectItem);
        //oLLScroolArea = (LinearLayout)findViewById(R.id.scroolArea);
        oLLItemsArea = (LinearLayout) findViewById(R.id.lyItemList);

        oIVBack = (ImageView)findViewById(R.id.btnBack);
        oIVNext = (ImageView)findViewById(R.id.btnNext);

        oRLScroolFather = (RelativeLayout)findViewById(R.id.scroolFather);

        oGLInventory = (GridLayout)findViewById(R.id.glInventory);
        //_db.Update(); //todo

        oButtonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                _db.clearJackpot();
                oLLItemsArea.removeAllViews();
                oLLSelectItem.setVisibility(View.VISIBLE);
                oLLSelectItem.bringToFront();
                intItemCount = 0;
                intPage = 0;
                oTxtSelectCount.setText("0/10");
                oTxtSelectPot.setText("0.0 $");
                buildPage();
            }
        });

        oButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                oLLSelectItem.setVisibility(View.INVISIBLE);
                intPage = 0;
                intItemCount = 0;
                userPot = intTotalPrize;
                intTotalPrize = 0;
                totalPot = 0;
                oTxtChance.setText("%100");
                oTxtItemCount.setText(oLLItemsArea.getChildCount()+"/40");
                oTxtTotalPrize.setText(toolBox.generateMoneyString(userPot));
            }
        });

        oButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                oButtonPlayGame.setAlpha(1);
                oButtonPlayGame.setClickable(true);
                oLLSelectItem.setVisibility(View.INVISIBLE);

                Cursor _cr = _db.getJackpotItem();

                while (_cr.moveToNext()){
                    int _quality= Integer.parseInt(_db.getInventoryItemInfo(_cr.getInt(0),"Quality")), _id = Integer.parseInt(_db.getInventoryItemInfo(_cr.getInt(0), "ItemId")), _stattrak = Integer.parseInt(_db.getInventoryItemInfo(_cr.getInt(0), "Stattrak"));
                   oLLItemsArea.addView(customListingItem(_id, _quality, _stattrak));
                }

                intItemCount = 0;
                userPot = intTotalPrize;
                intTotalPrize = 0;
                totalPot = 0;
                oTxtChance.setText("%100");
                oTxtItemCount.setText(oLLItemsArea.getChildCount()+"/30");
                oTxtTotalPrize.setText(toolBox.generateMoneyString(userPot));
            }
        });

        buildClick();

        oButtonPlayGame.setOnClickListener(clickPlay);

        oIVBack.setOnClickListener(goPage(false));

        oIVNext.setOnClickListener(goPage(true));

        oButtonOk.setClickable(false);
        oButtonOk.setAlpha(0.7f);
        oButtonPlayGame.setClickable(false);
        oButtonPlayGame.setAlpha(0.7f);

        ImageView _onBackPressed = (ImageView)findViewById(R.id.backMenu);

        _onBackPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                onBackPressed();
            }
        });

        buildNavBar();
    }

    public View.OnClickListener goPage(boolean Forwad){
        View.OnClickListener _return = null;

        if(Forwad){
            _return = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolBox.playSound(R.raw.soundclick);
                    if(intPage == intMaxPage){
                        return;
                    }
                    intPage++;

                    buildPage();
                }
            };
        }else{
            _return = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolBox.playSound(R.raw.soundclick);
                    if(intPage == 0){
                        return;
                    }

                    intPage--;

                    buildPage();
                }
            };
        }

        return _return;
    }

    public void buildClick(){

        //region Confirm
        clickConfirm = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                _scrool.setVisibility(View.INVISIBLE);
                oLLItemsArea.removeAllViews();
                oButtonPlayGame.setClickable(false);
                oButtonPlayGame.setText(getString(R.string.play));
                oButtonAddItem.setClickable(true);
                oButtonPlayGame.setAlpha(0.7f);
                oButtonPlayGame.setOnClickListener(clickPlay);
                oButtonAddItem.setAlpha(1);
                oTxtChance.setText("%100");
                oTxtItemCount.setText("0/40");
                oTxtTotalPrize.setText("0 $");
                oButtonAddItem.setClickable(true);
                oButtonAddItem.setAlpha(1);
            }
        };

        //endregion

        clickPlay = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oLLItemsArea.getChildCount() == 0){
                    //todo select item
                    return;
                }

                oButtonAddItem.setClickable(false);
            oButtonAddItem.setAlpha(0.7f);
                oButtonPlayGame.setClickable(false);
                oButtonPlayGame.setAlpha(0.7f);
                oButtonPlayGame.setText(getString(R.string.rolling));

                totalPot = userPot;

                int itemCount = 40 - oLLItemsArea.getChildCount();

                new CountDownTimer((itemCount+1)*100, 100){

                    @Override
                    public void onTick(long millisUntilFinished) {
                        int _color =  toolBox.chanceColor();

                        int _subColor = _color;

                        int id = _db.getRandomId(_color, -1);



                        int stattrak = toolBox.chanceStattrak();

                        int quality = toolBox.chanceQuality();

                        int[] _check = _db.avaibleQuality(id,quality,stattrak);

                        stattrak = _check[2];

                        quality = _check[1];
                        // pot miktarı vss. :D
                        oLLItemsArea.addView(customListingItem(id,quality,stattrak));

                        totalPot += toolBox.getPriceFromString(quality,_db.getItemInfo(id,"PriceQuery"),stattrak);


                        updatePotInfo();


                        _db.addJackpotItem(id, quality,stattrak,true);
                    }

                    @Override
                    public void onFinish() {
                        ScroolClass _scroolClass = new ScroolClass(getApplicationContext());

                        _scrool = _scroolClass.Scrool();
                        int index = 36;

                        int chance = new Random().nextInt(100);

                        while (index != 0){
                            int _chance = new Random().nextInt();

                            intChance = userPot/(totalPot/100);

                            if(index == 8){
                                if(new Random().nextInt(100) <= intChance){
                                    boolWin = true;
                                    _chance = 0;
                                }else{
                                    boolWin = false;
                                    _chance = 101;
                                }
                            }

                            if(_chance <= intChance){
                                _scroolClass.addItem("avatar/avatar"+_db.getConfig("Avatar")+".png", _db.getConfig("UserName")+index, "%"+intChance, 5,true);
                            }else{
                                _scroolClass.addItem("avatar/avatar0.png", "Bot", "%"+(100-intChance), 5,true);
                            }

                            index--;
                        }

                        oRLScroolFather.addView(_scrool);
                        oRLScroolFather.setVisibility(View.VISIBLE);
                        oRLScroolFather.bringToFront();


                        _scroolClass._size = 55*toolBox._pixelSize;
                        _scroolClass.startScrool();


                        //todo win oldu

                        if(boolWin){
                            //win

                            Cursor _cr = _db.getJackpotWinningItem();

                            while (_cr.moveToNext()){
                                //todo win

                                _db.addItem(_cr.getInt(0),_cr.getInt(1),_cr.getInt(2), Integer.parseInt(_db.getItemInfo(_cr.getInt(0),"Color")));
                            }

                        }else{
                            //lose todo

                            Cursor _cr = _db.getJackpotItem();

                            while (_cr.moveToNext()){
                                _db.removeInventory(_cr.getInt(0));
                            }

                        }

                        if(toolBox.gainXP(1)){
                            Intent _levelUp = new Intent(getApplicationContext(),LevelUp.class);
                            startActivity(_levelUp);
                        }

                        _db.clearJackpot();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                oButtonPlayGame.setText(getString(R.string.confirm));
                                oButtonPlayGame.setClickable(true);
                                oButtonPlayGame.setAlpha(1);
                                oButtonPlayGame.setOnClickListener(clickConfirm);
                                if(adsPopUp.isLoaded()){
                                    adsPopUp.show();
                                }
                            }
                        }, 7000);

                    }
                }.start();
            }
        };
    }

    int intBack = 0;

    private LinearLayout customListingItem(int ItemId, int Quality, int Stattrak){
        LinearLayout _child = new LinearLayout(getApplicationContext());

        if(intBack == 0){
            //Arkası şeffas
            intBack = 1;
        }else{
            //Arkası karatılı
            _child.setBackgroundColor(getResources().getColor(R.color.background)); // todo
            intBack = 0;
        }

        int _intItemId = ItemId;
        int _intItemQuality = Quality;

        _child.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        String _name = "", _skin = _db.getItemInfo(_intItemId,"Skin"), _quality = getString(getResources().getIdentifier("shortQuality"+_intItemQuality,"string",getPackageName())); // shortQuakişty + int

        if(Stattrak > 0){
            _name = getString(R.string.stattrak); //todo
        }

        _name += _db.getItemInfo(_intItemId,"Name");

        TextView _txtName = toolBox.customTextView(_name+" | "+_skin+" "+_quality,-1,12,false);

        TextView _txtPrice = toolBox.customTextView(toolBox.generateMoneyString(toolBox.getPriceFromString(Quality,_db.getItemInfo(_intItemId, "PriceQuery"),Stattrak)),0,12,false);

        _txtName.setGravity(Gravity.LEFT);
        _txtPrice.setGravity(Gravity.RIGHT);

        TextView _txtColor = new TextView(getApplicationContext());

       _txtColor.setBackgroundColor(toolBox.getColorValue(Integer.parseInt(_db.getItemInfo(_intItemId,"Color"))));

        _txtColor.setText("  ");

        _child.addView(_txtColor);
        _child.addView(_txtName);
        _child.addView(_txtPrice);

        return _child;
    }


    public void buildPage(){
        //oGLInventory

        final Cursor _cr = _db.getInventory(9,-1,-1,-1,intPage,-1);

        intMaxPage = (_db.getInventorySize()/9);

        oGLInventory.removeAllViews();

        while(_cr.moveToNext()){
            final LinearLayout _child = new LinearLayout(this);
            _child.setOrientation(LinearLayout.VERTICAL);
            _child.setTag(0+","+_cr.getInt(0));

            _child.setBackgroundColor(getResources().getColor(R.color.gainBackground));

            final RelativeLayout _bigFather = new RelativeLayout(getApplicationContext());

            final TextView txtChecked = new TextView(getApplicationContext());

            txtChecked.setVisibility(View.INVISIBLE);

            txtChecked.setTextColor(getResources().getColor(R.color.textcolor));
            txtChecked.setText(getString(R.string.selectedItem));
            txtChecked.setTextSize(16);
            txtChecked.setBackgroundColor(getResources().getColor(R.color.selected)); // todo
            txtChecked.setGravity(Gravity.CENTER);
            txtChecked.setTypeface(null, Typeface.BOLD);
            txtChecked.setWidth(100*toolBox._pixelSize);
            txtChecked.setHeight(125*toolBox._pixelSize);

            int _margin = 5*toolBox._pixelSize;


            LinearLayout.LayoutParams _param2 = new  LinearLayout.LayoutParams(100*toolBox._pixelSize, 125*toolBox._pixelSize);

            _param2.setMargins(_margin,_margin,_margin,_margin);

            _child.setLayoutParams(_param2);

            if(_db.checkJackpotItem(_cr.getInt(0))){
                //todo seçilmiş

                txtChecked.setVisibility(View.VISIBLE);
                _bigFather.setClickable(false);
                _child.setTag("1,"+_child.getTag().toString().split(",")[1]);
            }


            // Rare, Reswim - Para, skin, isim

            final int sQuality = _cr.getInt(3);
            int sStattrak = _cr.getInt(1);
            int sItemId = _cr.getInt(4);
            final int sPrice = toolBox.getPriceFromString(sQuality,_db.getItemInfo(sItemId, "PriceQuery"),sStattrak);
            final int sId =_cr.getInt(0);
            final int sColor = _cr.getInt(6);

            TextView _txtQuality = toolBox.customTextView(toolBox.getQualityString(sQuality),-1,12,true);

            RelativeLayout _subChild = new RelativeLayout(this);

            ImageView _ivSkin = new ImageView(this);
            _ivSkin.setImageBitmap(toolBox.getBitmap("item/"+sItemId+".png", 90,55));

            Drawable _drawable = new BitmapDrawable(getResources(),toolBox.getBitmap("ui/weapon_back.jpg",100,80));
            _ivSkin.setBackground(_drawable);

            TextView _txtPrice = toolBox.customTextView(toolBox.generateMoneyString(sPrice),-1,12,false);
            _txtPrice.setTextColor(getResources().getColor(R.color.textColorDark));

            _subChild.addView(_ivSkin);
            _subChild.addView(_txtPrice);

            TextView _txtSkin = toolBox.customTextView(_db.getItemInfo(sItemId, "Skin"),sColor,12,false);
            TextView _txtName = toolBox.customTextView(_db.getItemInfo(sItemId, "Name"), sColor,12,false);


            _child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolBox.playSound(R.raw.soundclick);
                    _child.setClickable(false);
                    if(Integer.parseInt(_child.getTag().toString().split(",")[0]) == 0){
                        // Seçildi
                        if(intItemCount == 10){
                            //Max seçilebilcek
                            Toast.makeText(Jackpot.this, getString(R.string.maxPot10), Toast.LENGTH_SHORT).show();
                            _child.setClickable(true);
                            return;
                        }
                        intTotalPrize += sPrice;
                        totalPot += sPrice;

                        intItemCount++;

                        oTxtSelectCount.setText(getString(R.string.selectedItemCount)+intItemCount+"/10");
                        oTxtSelectPot.setText(getString(R.string.potValue)+toolBox.generateMoneyString(intTotalPrize));

                        txtChecked.setVisibility(View.VISIBLE);

                        int itemId = Integer.parseInt(_child.getTag().toString().split(",")[1]);

                        _db.addJackpotItem(itemId,Integer.parseInt(_db.getInventoryItemInfo(itemId,"Quality")),Integer.parseInt(_db.getInventoryItemInfo(itemId,"Stattrak")),false);

                        _child.setTag("1,"+_child.getTag().toString().split(",")[1]);

                    }else{
                        // seçim kaldırıldı

                        intItemCount--;
                        intTotalPrize -= sPrice;
                        totalPot -= sPrice;
                        txtChecked.setVisibility(View.INVISIBLE);
                        _bigFather.setClickable(true);
                        _db.removeJackpotItem(Integer.parseInt(_child.getTag().toString().split(",")[1]));
                        _child.setTag("0,"+_child.getTag().toString().split(",")[1]);

                        oTxtSelectCount.setText(getString(R.string.selectedItemCount)+intItemCount+"/10");
                        oTxtSelectPot.setText(getString(R.string.potValue)+toolBox.generateMoneyString(intTotalPrize));
                    }
                    _child.setClickable(true);

                    if(intItemCount != 0){
                        oButtonOk.setClickable(true);
                        oButtonOk.setAlpha(1);
                    }else{
                        oButtonOk.setClickable(false);
                        oButtonOk.setAlpha(0.7f);
                    }
                }
            });

            _child.addView(_txtQuality);
            _child.addView(_subChild);
            _child.addView(_txtSkin);
            _child.addView(_txtName);


            _bigFather.addView(_child);
            _bigFather.addView(txtChecked);
            oGLInventory.addView(_bigFather);
        }

        //LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams()
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

        startActivity(toolBox.navigationSelect(id));
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

    public void updatePotInfo(){
        oTxtItemCount.setText(oLLItemsArea.getChildCount()+"/40");
        oTxtTotalPrize.setText(toolBox.generateMoneyString(totalPot));
         int _d = userPot/(totalPot/100);
        oTxtChance.setText("%"+String.valueOf(_d));
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
