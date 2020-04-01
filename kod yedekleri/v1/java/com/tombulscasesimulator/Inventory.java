package com.tombulscasesimulator;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

public class Inventory extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static TextView txtWallet = null;
    DataBase _db = new DataBase(this);

    ToolClass toolBox = null;

    int _page = 0, _maxPage = 0, _pageQuality = 0, _pageStattrak = 0, _pageType = 0;

    LinearLayout _objectItemInfoPanel = null;

    InterstitialAd adsPopUp = null;
    RewardedVideoAd adsVideo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolBox = new ToolClass(this);

        //region Pop Up Ads

        adsPopUp = new InterstitialAd(this);

        adsPopUp.setAdUnitId(getString(R.string.AD_INTERSTITIAL));

        AdRequest adRequest = new AdRequest.Builder().build();

        adsPopUp.loadAd(adRequest);

        //endregion

        //region ADS BANNER
        AdView _adView = (AdView)findViewById(R.id.bannerEnventory);

        toolBox.buildAdsBanner(_adView);
        //endregion

        txtWallet = (TextView)findViewById(R.id.level);

        txtWallet.setText(getString(R.string.cash)+toolBox.generateMoneyString(_db.getWallet()));

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


        _objectItemInfoPanel = (LinearLayout)findViewById(R.id.itemInfo);
        buildPage();


        final TextView _objectTotalValue = (TextView)findViewById(R.id.toplamDeger), _objectNick =  (TextView)findViewById(R.id.userName),  _objectXP =  (TextView)findViewById(R.id.currentXP), _objectNeedXP =  (TextView)findViewById(R.id.totalXP);

        _objectTotalValue.setText(getString(R.string.totalValue)+String.valueOf(((float)_db.getInventoryValue()/100)+" $"));
        _objectNick.setText(_db.getConfig("UserName"));

        _objectNick.setOnClickListener(new View.OnClickListener() {
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

                        _objectNick.setText(_db.getConfig("UserName"));

                        father.setVisibility(View.INVISIBLE);
                        toolBox.playSound(R.raw.soundclick);
                    }
                });
            }
        });




        _objectXP.setText(getString(R.string.yourXP)+_db.getConfig("XP"));
        _objectNeedXP.setText(getString(R.string.needXP));

        final ImageView _objectAvatar = (ImageView)findViewById(R.id.avatar), _objectRank = (ImageView)findViewById(R.id.rank);



        _objectAvatar.setImageBitmap(toolBox.getBitmap(_db.getAvatarUrl(Integer.parseInt(_db.getConfig("Avatar"))), 0,0));// TODO size


        _objectAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChangeAvatar.class));
            }
        });


        _objectRank.setImageBitmap(toolBox.getBitmap("rank/rank"+_db.getConfig("Level")+".png", 0,0));


        ProgressBar _objectLevelBar = (ProgressBar)findViewById(R.id.levelBar);

        _objectLevelBar.setProgress(Integer.parseInt(_db.getConfig("XP")));
        _objectLevelBar.setMax(20);

        _maxPage = (_db.getInventorySize() / 16)+1; // TODO +1 2.1 de 2 dediği için

        if(_maxPage == 0){
            ImageView _objectNextPage = (ImageView)findViewById(R.id.nextButton);
            _objectNextPage.setVisibility(View.INVISIBLE);
            ImageView _objectBackPage = (ImageView)findViewById(R.id.backButton);
            _objectBackPage.setVisibility(View.INVISIBLE);
        }

        LinearLayout _object = (LinearLayout)findViewById(R.id.object1);

        BitmapDrawable _background = new BitmapDrawable(toolBox.getBitmap("ui/background.png", 16, 16));
        _background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        _object.setBackground(_background);

        ImageView _objectNextPage = (ImageView)findViewById(R.id.nextButton);
        _objectNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                if(_page == _maxPage){
                    return;
                }
                _page++;
                buildPage();


                TextView _no =  (TextView)findViewById(R.id.page);

                _no.setText(String.valueOf(_page+1)+"/"+(_maxPage+1));
            }
        });
        ImageView _objectBackPage = (ImageView)findViewById(R.id.backButton);
        _objectBackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                if(_page == 0){
                    return;
                }

                _page--;

                buildPage();


                TextView _no =  (TextView)findViewById(R.id.page);

                _no.setText(String.valueOf(_page+1)+"/"+(_maxPage+1));
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

        if(adsPopUp.isLoaded()){
            adsPopUp.show();
            _db.setWallet(2000);
            txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
        }
    }


    int intMaxPage = 0;

    public void buildPage(){
        // ID, STATTRAK, TYPE, QUALİTY, ITEM ID,CASES,COLOR
        try {
            Cursor cursorInventory = _db.getInventory(12,-1,-1,-1,_page,-1); //detaylı arama ekle todo

            intMaxPage = _db.getInventorySize()/12;

            final GridLayout _father = (GridLayout)findViewById(R.id.itemList);
            _father.removeAllViews();

            int pageValue = 0;

            while (cursorInventory.moveToNext()){
                final int intInventoryId = cursorInventory.getInt(0);
                final int intItemId = cursorInventory.getInt(cursorInventory.getColumnIndex("ItemId"));
                int intQuality = cursorInventory.getInt(cursorInventory.getColumnIndex("Quality"));
                int intStattrak = cursorInventory.getInt(cursorInventory.getColumnIndex("Stattrak"));
                int intSouvenir = cursorInventory.getInt(cursorInventory.getColumnIndex("Souvenir"));
                final int intItemColor = cursorInventory.getInt(cursorInventory.getColumnIndex("Color"));

                final int intPrice = Integer.parseInt(_db.getItemInfo(intItemId,"PriceQuery").split("_")[intQuality+((intStattrak+intSouvenir)*5)]);

                pageValue += intPrice;

                String stringName = "", stringSkin = "", stringQuality = toolBox.getQualityString(intQuality);

                if(intStattrak == 1 || intSouvenir == 1){
                    if(intStattrak == 1){
                        stringName += getString(R.string.stattrak) + " ";
                    }else{
                        stringName += getString(R.string.souvenir) + " ";
                    }

                    ImageView img = (ImageView)findViewById(R.id.gainImageStattrak);
                    img.setVisibility(View.VISIBLE);
                    img.bringToFront();
                }

                stringName += _db.getItemInfo(intItemId,"Name");
                stringSkin += _db.getItemInfo(intItemId,"Skin");

                final LinearLayout _child = new LinearLayout(this);
                _child.setOrientation(LinearLayout.VERTICAL);
                _child.setTag(intItemId);

                ImageView imgViewSkin = new ImageView(this);
                imgViewSkin.setImageBitmap(toolBox.getBitmap("item/"+intItemId+".png",70*toolBox._pixelSize,50*toolBox._pixelSize));
                imgViewSkin.setBackgroundResource(R.mipmap.gun_background);
                imgViewSkin.setLayoutParams(new LinearLayout.LayoutParams(70*toolBox._pixelSize,50*toolBox._pixelSize));

                TextView txtViewQuality = toolBox.customTextView(stringQuality,intItemColor,8,true);//todo relative layout ypaıp fiyat ekle
                TextView txtViewName = toolBox.customTextView(stringName,intItemColor,8,false);
                TextView txtViewSkin = toolBox.customTextView(stringSkin,intItemColor,8,false);

                _child.addView(txtViewQuality);
                _child.addView(imgViewSkin);
                _child.addView(txtViewName);
                _child.addView(txtViewSkin);

                _father.addView(_child);


                final String fName =  stringName, fSkin = stringSkin, fQuality = stringQuality;
                _child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolBox.playSound(R.raw.soundclick);

                        TextView oTextViewQuality = (TextView)findViewById(R.id.infoQuality);
                        oTextViewQuality.setText(fQuality);
                        oTextViewQuality.setBackgroundColor(toolBox.getColorValue(intItemColor));

                        final ImageView oImageViewSkin = (ImageView)findViewById(R.id.gainImage);
                        oImageViewSkin.setImageBitmap(toolBox.getBitmap("item/"+intItemId+".png",180*toolBox._pixelSize,180*toolBox._pixelSize)); //todo arkaplanda haraket ve boyu rklencrek

                        ImageView _ivbackground = (ImageView)findViewById(R.id.gainImageBack);
                        _ivbackground.setImageBitmap(toolBox.getBitmap("ui/quality"+intItemColor+".png",180*toolBox._pixelSize,180*toolBox._pixelSize));

                        RotateAnimation _anim = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        _anim.setDuration(5000);
                        _anim.setRepeatCount(Animation.INFINITE);
                        _anim.setInterpolator(new LinearInterpolator());

                        _ivbackground.startAnimation(_anim);


                        TextView oTextViewName = (TextView)findViewById(R.id.infoName);
                        oTextViewName.setText(fName);
                        oTextViewName.setBackgroundColor(toolBox.getColorValue(intItemColor));

                        TextView oTextViewSkin = (TextView)findViewById(R.id.infoSkin);
                        oTextViewSkin.setText(fSkin);
                        oTextViewSkin.setBackgroundColor(toolBox.getColorValue(intItemColor));

                        Button oButtonSell = (Button)findViewById(R.id.buttonSell);
                        oButtonSell.setText(getString(R.string.sell)+" - "+toolBox.generateMoneyString(intPrice));

                        Button oButtonCancel = (Button)findViewById(R.id.buttonCancel);

                        //region Clickler

                        oButtonSell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toolBox.playSound(R.raw.soundclick);

                                _father.removeView(_child);

                                toolBox.sellItem(intInventoryId,intPrice);

                                txtWallet.setText(toolBox.generateMoneyString(intPrice));

                                _objectItemInfoPanel.setVisibility(View.INVISIBLE);
                            }
                        });

                        oButtonCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toolBox.playSound(R.raw.soundclick);

                                _objectItemInfoPanel.setVisibility(View.INVISIBLE);
                            }
                        });

                        //endregion

                        toolBox.playSound(R.raw.soundclick);
                        _objectItemInfoPanel.bringToFront();
                        _objectItemInfoPanel.setVisibility(View.VISIBLE);
                    }
                });

                TextView _pageValue = (TextView)findViewById(R.id.pageValue);

                _pageValue.setText(getString(R.string.totalPrice)+" "+ String.valueOf((float)pageValue/100) + " $");
            }

        }catch (Exception ex){
            Log.e("BUILD PAGE", String.valueOf(ex));
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent _intent = null;

        if (id == R.id.nav_mainmenu) {
            _intent = new Intent(this, MainActivity.class);
            startActivity(_intent);
        } else if (id == R.id.nav_inventory) {


        } else if (id == R.id.nav_open_case) {
            _intent = new Intent(this, OpenCase.class);
            startActivity(_intent);

        } else if (id == R.id.nav_dice) {
            _intent = new Intent(this, Dice.class);
            startActivity(_intent);

        } else if (id == R.id.nav_flip_coin) {
            _intent = new Intent(this, FlipCoin.class);
            startActivity(_intent);

        } else if (id == R.id.nav_config) {
            _intent = new Intent(this, Settings.class);
            startActivity(_intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
