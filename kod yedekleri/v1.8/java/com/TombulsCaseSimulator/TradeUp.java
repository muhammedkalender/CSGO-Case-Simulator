package com.TombulsCaseSimulator;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.GridLayout;
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

public class TradeUp extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ToolClass toolBox = null;
    DataBase _db = null;

    TextView oTxtWallet = null, oTxtNoItem = null;

    GridLayout  oGridLayoutInventory = null;

    Button oButtonAddInventory = null, oButtonSell = null, oButtonCancel = null, oButtonPlayGame = null;

    ImageView oImageViewBack = null, oImageViewNext = null, oImageViewGainSkin = null, oImageViewGainSkinBackground = null, oImageViewGainStattrak = null;

    TextView oTxtItemCount = null, oTxtGainQualityAndPrice = null, oTxtGainNameAndSkin = null;

    LinearLayout oGridLayoutOptions = null,oLLInventoryFather = null, oLLGainItem = null, oLLArea1 = null, oLLArea2 = null, oLLArea3 = null, oLLArea4 = null, oLLArea5 = null;

    int intSelectCount = 0, intPage = 0, intMaxPage = 0, intOption = 0, intStattrakorSouvenir = 0;

    InterstitialAd adsPopUp;
    RewardedVideoAd adsVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_up);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolBox = new ToolClass(this);
        _db = new DataBase(this);

        //region Pop Up Ads

        adsPopUp = new InterstitialAd(this);

        adsPopUp.setAdUnitId(getString(R.string.AD_INTERSTITIAL));

        AdRequest adRequest = new AdRequest.Builder().build();

        adsPopUp.loadAd(adRequest);

        //endregion

        //region Video Ads

        adsVideo = MobileAds.getRewardedVideoAdInstance(this);

        adsVideo.loadAd(getString(R.string.AD_REWARDED_VIDEO), new AdRequest.Builder().build());

        //endregion

        //region Üst Menü

        oTxtWallet = (TextView)findViewById(R.id.wallet);

        ImageView _onBackPressed = (ImageView)findViewById(R.id.backMenu);

        _onBackPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                onBackPressed();
            }
        });

        oTxtWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adsVideo.isLoaded()){
                    adsVideo.show();
                }
            }
        });

        //endregion

        //region Tanımlamalar

        oImageViewBack = (ImageView)findViewById(R.id.oImageViewBack);
        oImageViewNext = (ImageView)findViewById(R.id.oImageViewNext);
        oImageViewGainSkin = (ImageView)findViewById(R.id.oImageView);
        oImageViewGainSkinBackground = (ImageView)findViewById(R.id.oImageViewBackground);
        oImageViewGainStattrak = (ImageView)findViewById(R.id.oImageViewStattrak);

        oTxtItemCount = (TextView)findViewById(R.id.oTextViewSelectedCount);
        oTxtGainNameAndSkin = (TextView)findViewById(R.id.oTxtNameSkin);
        oTxtGainQualityAndPrice = (TextView)findViewById(R.id.oTxtQualityPrice);
        oTxtNoItem = (TextView)findViewById(R.id.oTxtNoItem);

        oGridLayoutInventory = (GridLayout)findViewById(R.id.oGVInventory);
        oGridLayoutOptions = (LinearLayout)findViewById(R.id.oGLOptions);

        oButtonAddInventory = (Button)findViewById(R.id.oButtonAdd);
        oButtonCancel = (Button)findViewById(R.id.oButtonCancel);
        oButtonPlayGame = (Button)findViewById(R.id.oButtonPlay);
        oButtonSell = (Button)findViewById(R.id.oButtonSell);

        oLLInventoryFather = (LinearLayout)findViewById(R.id.oLLInventoryFather);
        oLLGainItem = (LinearLayout)findViewById(R.id.gainItem);
        oLLArea1 = (LinearLayout)findViewById(R.id.area1);
        oLLArea2 = (LinearLayout)findViewById(R.id.area2);
        oLLArea3 = (LinearLayout)findViewById(R.id.area3);
        oLLArea4 = (LinearLayout)findViewById(R.id.area4);
        oLLArea5 = (LinearLayout)findViewById(R.id.area5);

        //endregion

        //region Tıklamalar

        oImageViewNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(intPage == intMaxPage){
                    return;
                }

                toolBox.playSound(R.raw.soundclick);

                intPage++;

                buildInventory(intOption,intStattrakorSouvenir,true);


            }
        });

        oImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intPage == 0){
                    return;
                }

                toolBox.playSound(R.raw.soundclick);

                intPage--;

                buildInventory(intOption, intStattrakorSouvenir,true);
            }
        });

        oButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oLLInventoryFather.setVisibility(View.INVISIBLE);
                oGridLayoutOptions.setVisibility(View.VISIBLE);
                intSelectCount = 0;
                intPage = 0;
                intMaxPage = 0;
                intOption = 0;
                intStattrakorSouvenir = 0;
                _db.clearTradeUp();
                oTxtNoItem.setVisibility(View.INVISIBLE);
            }
        });

        oButtonPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame();
            }
        });

        //endregion

        oButtonPlayGame.setClickable(false);

        oTxtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));

        oTxtWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buildOptions();

        buildNavBar();
    }

    //region Build Options

    public void buildOptions(){
        int i = 0;

        int intIndex = 0;

        while (i != 5){
            String stringName = getString(getResources().getIdentifier("_color"+i,"string",getPackageName()));

            ImageView imageViewImage = new ImageView(this);
            imageViewImage.setImageBitmap(toolBox.getBitmap("ui/option"+i+".png",75,45));

            ImageView imageViewStattrakImage = new ImageView(this);
            imageViewStattrakImage.setImageBitmap(toolBox.getBitmap("ui/optionS"+i+".png",75,45));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100*toolBox._pixelSize, 85*toolBox._pixelSize, 1f);

            LinearLayout linearLayoutNormal = new LinearLayout(this);
            linearLayoutNormal.setOrientation(LinearLayout.VERTICAL);
            linearLayoutNormal.setLayoutParams(layoutParams);

            LinearLayout linearLayoutStattrak = new LinearLayout(this);
            linearLayoutStattrak.setOrientation(LinearLayout.VERTICAL);
            linearLayoutStattrak.setLayoutParams(layoutParams);

            TextView textViewNormal = toolBox.customTextView(stringName, i,12,false);
            TextView textViewStattrak = toolBox.customTextView(stringName,i,12,false);

            linearLayoutNormal.addView(imageViewImage);
            linearLayoutNormal.addView(textViewNormal);

            linearLayoutStattrak.addView(imageViewStattrakImage);
            linearLayoutStattrak.addView(textViewStattrak);

            final int intColor = i;
            linearLayoutNormal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //// TODO: 2.04.2017

                    toolBox.playSound(R.raw.soundclick);

                    intOption = intColor;
                    intStattrakorSouvenir = 0;
                    buildInventory(intOption, 0, false);
                }
            });

            linearLayoutStattrak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2.04.2017

                    toolBox.playSound(R.raw.soundclick);

                    intOption = intColor;
                    intStattrakorSouvenir = 1;
                    buildInventory(intOption, 1,false);
                }
            });

            if(intIndex < 4){
                oLLArea3.addView(linearLayoutNormal);
            }else if(intIndex < 8){
                oLLArea4.addView(linearLayoutNormal);
            }else{
                oLLArea5.addView(linearLayoutNormal);
            }

            intIndex++;

            if(intIndex < 4){
                oLLArea3.addView(linearLayoutStattrak);
            }else if(intIndex < 8){
                oLLArea4.addView(linearLayoutStattrak);
            }else{
                oLLArea5.addView(linearLayoutStattrak);
            }

            intIndex++;

            i++;
        }
    }

    //endregion

    //region Build Inventory

    public void buildInventory(int Color, int Stattrak, boolean pageChance){
        oGridLayoutOptions.setVisibility(View.INVISIBLE);

        Cursor _cr = _db.getInventory(6,-1,-1,Stattrak,intPage,Color);

        intMaxPage = (_cr.getCount()/6);

        if(_cr.getCount() == 0){
            oTxtNoItem.setVisibility(View.VISIBLE);
            oTxtNoItem.bringToFront();
            return;
        }else{
            oTxtNoItem.setVisibility(View.INVISIBLE);
        }

        if(!pageChance){
            oLLArea1.removeAllViews();
            oLLArea2.removeAllViews();
            _db.clearTradeUp();
        }

        oGridLayoutInventory.removeAllViews();

        int intIndex = 0;
        while (_cr.moveToNext()){
            final int intInventoryId = _cr.getInt(0);
            final int intItemId = _cr.getInt(_cr.getColumnIndex("ItemId"));
            final int intItemStattrak = _cr.getInt(_cr.getColumnIndex("Stattrak"));
            final int intItemQuality = _cr.getInt(_cr.getColumnIndex("Quality"));
            int intItemPrice = toolBox.getPriceFromString(intItemQuality,_db.getItemInfo(intItemId,"PriceQuery"),intItemQuality);
            final int intItemColor = _cr.getInt(_cr.getColumnIndex("Color"));
            final int intItemCase = Integer.parseInt(_db.getItemInfo(intItemId,"Cases").replace("[","").replace("]",""));

            final LinearLayout _child = new LinearLayout(this);
            _child.setOrientation(LinearLayout.VERTICAL);
            _child.setTag(0+","+intInventoryId);

            final RelativeLayout _bigChild = new RelativeLayout(this);

            final TextView txtChecked = new TextView(getApplicationContext());
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

            if(_db.checkTradeUpItem(intInventoryId)){
                txtChecked.setVisibility(View.VISIBLE);
                _bigChild.setClickable(false);
                _child.setTag("1,"+_child.getTag().toString().split(",")[1]);
            }else{
                txtChecked.setVisibility(View.INVISIBLE);
                _bigChild.setClickable(true);
                _child.setTag("0,"+_child.getTag().toString().split(",")[1]);
            }

            TextView _txtQuality = toolBox.customTextView(toolBox.getQualityString(intItemQuality),-1,12,true);

            RelativeLayout _subChild = new RelativeLayout(this);

            final ImageView _ivSkin = new ImageView(this);
            _ivSkin.setImageBitmap(toolBox.getBitmap("item/"+intItemId+".png", 90,55));

            final Drawable _drawable = new BitmapDrawable(getResources(),toolBox.getBitmap("ui/weapon_back.jpg",100,80));
            _ivSkin.setBackground(_drawable);

            TextView _txtPrice = toolBox.customTextView(toolBox.generateMoneyString(intItemPrice),-1,12,false);
            _txtPrice.setTextColor(getResources().getColor(R.color.textColorDark));

            _subChild.addView(_ivSkin);
            _subChild.addView(_txtPrice);

            TextView _txtSkin = toolBox.customTextView(_db.getItemInfo(intItemId, "Skin"),intItemColor,12,false);
            TextView _txtName = toolBox.customTextView(_db.getItemInfo(intItemId, "Name"), intItemColor,12,false);

            final LinearLayout _selecChild = new LinearLayout(getApplicationContext());
            _selecChild.setOrientation(LinearLayout.VERTICAL);

            ImageView _subSkin = new ImageView(getApplicationContext());
            _subSkin.setBackgroundResource(R.mipmap.gun_background);

            _subSkin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 70*toolBox._pixelSize));

            TextView _skin = toolBox.customTextView(_db.getItemInfo(intItemId,"Skin"),intItemColor,10,false);

            TextView _name = toolBox.customTextView(_db.getItemInfo(intItemId,"Name"), intItemColor,10,false);

            _subSkin.setImageBitmap(toolBox.getBitmap("item/"+intItemId+".png",0,0));

            _selecChild.setLayoutParams(new LinearLayout.LayoutParams(75*toolBox._pixelSize,95*toolBox._pixelSize,1f));

            _selecChild.addView(_subSkin);
            _selecChild.addView(_skin);
            _selecChild.addView(_name);


            final  int f = intIndex;
            _selecChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _db.removeTradeUp(intInventoryId);
                    txtChecked.setVisibility(View.INVISIBLE);LinearLayout a = (LinearLayout)_selecChild.getParent();

                    a.removeView(_selecChild);
                    intSelectCount--;
                    oTxtItemCount.setText(getString(R.string.selectedItemCount)+intSelectCount+"/10");
                }
            });

            _child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolBox.playSound(R.raw.soundclick);
                    _child.setClickable(false);
                    if(Integer.parseInt(_child.getTag().toString().split(",")[0]) == 0){
                        // Seçildi
                        if(intSelectCount == 10){
                            //Max seçilebilcek
                            //Toast.makeText(Jackpot.this, getString(R.string.maxPot10), Toast.LENGTH_SHORT).show();
                            _child.setClickable(true);
                            return;
                        }

                        if(oLLArea1.getChildCount() < 5){
                            oLLArea1.addView(_selecChild);
                        }else{
                            oLLArea2.addView(_selecChild);
                        }

                        intSelectCount++;

                        oTxtItemCount.setText(getString(R.string.selectedItemCount)+intSelectCount+"/10");

                        txtChecked.setVisibility(View.VISIBLE);

                        int itemId = Integer.parseInt(_child.getTag().toString().split(",")[1]);

                        _db.addTradeUp(intInventoryId,intItemCase,intItemQuality,intItemStattrak,intItemColor);

                        _child.setTag("1,"+_child.getTag().toString().split(",")[1]);

                    }else{
                        // seçim kaldırıldı

                        intSelectCount--;
                        txtChecked.setVisibility(View.INVISIBLE);
                        _bigChild.setClickable(true);
                        _db.removeTradeUp(intInventoryId);
                        _child.setTag("0,"+_child.getTag().toString().split(",")[1]);

                        LinearLayout a = (LinearLayout)_selecChild.getParent();

                        a.removeView(_selecChild);

                        oTxtItemCount.setText(getString(R.string.selectedItemCount)+intSelectCount+"/10");
                    }
                    _child.setClickable(true);

                    if(intSelectCount != 10){
                        oButtonPlayGame.setClickable(false);
                        oButtonPlayGame.setAlpha(0.7f);
                    }else{
                        oButtonPlayGame.setClickable(true);
                        oButtonPlayGame.setAlpha(1);
                    }
                }
            });

            _child.addView(_txtQuality);
            _child.addView(_subChild);
            _child.addView(_txtSkin);
            _child.addView(_txtName);


            _bigChild.addView(_child);
            _bigChild.addView(txtChecked);
            intIndex++;
            oGridLayoutInventory.addView(_bigChild);
        }

        oLLInventoryFather.setVisibility(View.VISIBLE);
        oLLInventoryFather.bringToFront();

    }

    //endregion

    private void playGame(){

        _db.setStatic("TRADE_UP",1);

        if(intStattrakorSouvenir != 0){
            _db.setStatic("TRADE_UP_TYPE_"+intOption+"_STATTRAK",1);
        }else{
        _db.setStatic("TRADE_UP_TYPE_"+intOption,1);}

        int intTradeUpColor = intOption+1;
        int intTradeUpCase = _db.getTradeUpCase();
        int intTradeUpQuality = _db.getTradeUpQuality();

        final int intGainId = _db.getRandomId(intTradeUpColor, intTradeUpCase);

        int[] _check = _db.avaibleQuality(intGainId,intTradeUpQuality,intStattrakorSouvenir);

        intTradeUpQuality = _check[1];
        final int intGainItemStattrakorSouvenir = _check[2];

        final int intItemPrice = toolBox.getPriceFromString(intTradeUpQuality, _db.getItemInfo(intGainId,"PriceQuery"), intGainItemStattrakorSouvenir);

        LinearLayout _father = new LinearLayout(this);
        _father.bringToFront();

        oImageViewGainSkin.setImageBitmap(toolBox.getBitmap("item/"+intGainId+".png",120,90));

        oImageViewGainSkinBackground.setImageBitmap(toolBox.getBitmap("ui/quality"+intTradeUpColor+".png",0,0));

        if(intGainItemStattrakorSouvenir == 1){
            oImageViewGainStattrak.setVisibility(View.VISIBLE);
        }else{
            oImageViewGainStattrak.setVisibility(View.INVISIBLE);
        }

        oTxtGainQualityAndPrice.setText(toolBox.getQualityString(intTradeUpQuality)+" - "+toolBox.generateMoneyString(intItemPrice));

        oTxtGainNameAndSkin.setText(_db.getItemInfo(intGainId,"Name")+" | "+_db.getItemInfo(intGainId,"Skin"));

        RotateAnimation _anim = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        _anim.setDuration(5000);
        _anim.setRepeatCount(Animation.INFINITE);
        _anim.setInterpolator(new LinearInterpolator());

        oImageViewGainSkinBackground.startAnimation(_anim);

        if(toolBox.gainXP(((intTradeUpColor+1)*100)+(intStattrakorSouvenir*(intTradeUpColor+1)*100))){
            startActivity(new Intent(this, LevelUp.class));
        }

        oLLGainItem.setVisibility(View.VISIBLE);
        oLLGainItem.bringToFront();

        switch (intTradeUpColor){
            case 2:
                toolBox.playSound(R.raw.droprare);
                break;
            case 3:
                toolBox.playSound(R.raw.dropmythical);
                break;
            case 4:
                toolBox.playSound(R.raw.droplegendary);
                break;
            case 5:
                toolBox.playSound(R.raw.dropancient);
                break;
            case 6:
                toolBox.playSound(R.raw.dropuniq);
                break;
        }

        oButtonSell.setText(getString(R.string.sell)+" - "+toolBox.generateMoneyString(intItemPrice));

        oButtonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);

                _db.setWallet(intItemPrice);

                Toast.makeText(getApplicationContext(), toolBox.generateMoneyString(intItemPrice) + " $ " + getString(R.string.addwallet), Toast.LENGTH_SHORT).show();

                oTxtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));

                oLLArea1.removeAllViews();
                oLLArea2.removeAllViews();
                oGridLayoutOptions.setVisibility(View.VISIBLE);
                oGridLayoutInventory.removeAllViews();
                oLLInventoryFather.setVisibility(View.INVISIBLE);

                oLLGainItem.setVisibility(View.INVISIBLE);


                Cursor _cr = _db.getTradeUpItems();
                while(_cr.moveToNext()){
                    _db.removeInventory(_cr.getInt(_cr.getColumnIndex("InventoryId")));
                }

                _db.clearTradeUp();

                intSelectCount = 0;
                intPage = 0;
                intMaxPage = 0;
                intOption = 0;
                intStattrakorSouvenir = 0;


            }
        });

        final int finalIntTradeUpQuality = intTradeUpQuality;
        oButtonAddInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);

                if(_db.addItem(intGainId, finalIntTradeUpQuality,intGainItemStattrakorSouvenir,intOption)){
                    Cursor _cr = _db.getTradeUpItems();
                    while(_cr.moveToNext()){
                        _db.removeInventory(_cr.getInt(_cr.getColumnIndex("InventoryId")));
                    }
                }else{
                    //todo hata
                }

                oLLArea1.removeAllViews();
                oLLArea2.removeAllViews();
                oGridLayoutOptions.setVisibility(View.VISIBLE);
                oGridLayoutInventory.removeAllViews();
                oLLInventoryFather.setVisibility(View.INVISIBLE);

                oLLGainItem.setVisibility(View.INVISIBLE);

                _db.clearTradeUp();

                Toast.makeText(getApplicationContext(),getString(R.string.addingenventer), Toast.LENGTH_SHORT).show();

                intSelectCount = 0;
                intPage = 0;
                intMaxPage = 0;
                intOption = 0;
                intStattrakorSouvenir = 0;

            }
        });

        oTxtItemCount.setText(getString(R.string.selectedItemCount)+intSelectCount+"/10");

        if(adsPopUp.isLoaded()){
        adsPopUp.show();
        }
    }

    //region Çöp
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

    //endregion
}
