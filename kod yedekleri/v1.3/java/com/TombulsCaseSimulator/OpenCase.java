package com.TombulsCaseSimulator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class OpenCase extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static TextView txtWallet = null;

    int _caseId = 0,_caseType = 0, _onceCase = 0, _priceCase = 0, _priceKey = 0, pixels = 0, _caseSpecial = 0;

    Typeface _font = null;

    boolean _configSound = true, _onceCases = false;

    Button _objectButton, _objectBackButton;
    LinearLayout _objectMore;

    LinearLayout _objectGainItem,  _objectItemPanel;

    RelativeLayout _objectOpenScrool;

    ScrollView _objectItemList, _objectCaseList;

    DataBase _db = new DataBase(this);
    InterstitialAd adsPopUp;
    RewardedVideoAd adsVideo = null;

    ToolClass toolBox = null;



    int _commercial = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_case);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolBox = new ToolClass(this);

        //region Pop Up Ads

        adsPopUp = new InterstitialAd(this);

        adsPopUp.setAdUnitId(getString(R.string.AD_INTERSTITIAL));

        AdRequest adRequest = new AdRequest.Builder().build();

        adsPopUp.loadAd(adRequest);

        //region

        AdView _adView = (AdView)findViewById(R.id.bannerOpenCase);

        toolBox.buildAdsBanner(_adView);

        pixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        _objectButton = (Button)findViewById(R.id.Function);
        _objectBackButton = (Button)findViewById(R.id.Back);
        txtWallet = (TextView) findViewById(R.id.wallet);

        txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));

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

        _objectGainItem = (LinearLayout)findViewById(R.id.gainItem);

        _objectItemPanel = (LinearLayout)findViewById(R.id.caseAndKey);

        _objectOpenScrool = (RelativeLayout) findViewById(R.id.scroolItemFather);

        _objectItemList = (ScrollView)findViewById(R.id.itemListFather);
        _objectCaseList = (ScrollView)findViewById(R.id.caseListFather);

        listingCases();


        _objectButton.setText(getString(R.string.chooseCase));

        _objectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                openCase();


            }
        });
        _objectButton.setClickable(false);
        _objectButton.setAlpha(0.7f);

        _objectBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                listingCases();
            }
        });

        _objectBackButton.setText(getString(R.string.back));

        BitmapDrawable _background = new BitmapDrawable(toolBox.getBitmap("ui/background.png", 16, 16));
        _background.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);


        RelativeLayout _father = (RelativeLayout)findViewById(R.id.menu1);
        _father.setBackground(_background);

        _objectItemPanel.setBackground(_background);

        _father = (RelativeLayout)findViewById(R.id.menu2);
        _father.setBackground(_background);

        _font = Typeface.createFromAsset(getAssets(), "fonts/MyriadProBold.ttf");

        buildStyle();

        buildNavBar();

        ImageView _onBackPressed = (ImageView)findViewById(R.id.backMenu);

        _onBackPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolBox.playSound(R.raw.soundclick);
                onBackPressed();
            }
        });
    }
    public void buildStyle(){
        Button a = (Button)findViewById(R.id.SellButton), b = (Button)findViewById(R.id.addButton);
        TextView c = (TextView)findViewById(R.id.caseText), d = (TextView)findViewById(R.id.keyText), e = (TextView)findViewById(R.id.gainTitle),f  = (TextView)findViewById(R.id.wallet), g = (TextView)findViewById(R.id.wallet);

        a.setTypeface(_font);
        b.setTypeface(_font);
        c.setTypeface(_font);
        d.setTypeface(_font);
        e.setTypeface(_font);
        f.setTypeface(_font);
        g.setTypeface(_font);

        _objectButton.setTypeface(_font);
        _objectBackButton.setTypeface(_font);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void listingItem(int ID){
        if(ID == _onceCase){
            _objectCaseList.setVisibility(View.INVISIBLE);
            _objectItemList.setVisibility(View.VISIBLE);
            return;
        }


        _priceCase = Integer.valueOf(_db.getCaseInfo(ID, "Price"));
        _priceKey = Integer.valueOf(_db.getCaseInfo(ID, "KeyPrice"));

        _objectButton.setText(getString(R.string.open_case)+" - "+((float)(_priceCase + _priceKey)/100)+" $");
        _objectButton.setClickable(true);
        _objectButton.setAlpha(1);

        LinearLayout _bigFather = (LinearLayout) findViewById(R.id.itemListChild);
        _bigFather.removeAllViews();

        _objectItemList.setVisibility(View.VISIBLE);
        _objectCaseList.setVisibility(View.INVISIBLE);

        Cursor _cr = _db.getItemList(ID);

        int intIndex = 5;

        LinearLayout _father = null;

        while (_cr.moveToNext()){

            if(intIndex == 5){
                LinearLayout _ly = new LinearLayout(this);

                _ly.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f));;
                _ly.setOrientation(LinearLayout.HORIZONTAL);
                _father = _ly;
                _bigFather.addView(_father);
                intIndex = 0;
            }

            intIndex++;

            int _color = _cr.getInt(_cr.getColumnIndex("Color"));


            _father.addView(addItemList(_cr.getString(_cr.getColumnIndex("ID")), _cr.getString(_cr.getColumnIndex("Skin")), _cr.getString(_cr.getColumnIndex("Name")),_color,0));
        }

        if (_father.getChildCount() == 5){
            LinearLayout _ly = new LinearLayout(this);
            _ly.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f));;
            _ly.setOrientation(LinearLayout.HORIZONTAL);
            _father = _ly;
            _bigFather.addView(_father);
        }

        _father.addView(addItemList("special", getString(R.string.rare), getString(R.string._specialItem),6, _caseSpecial));

        int add = (5-_father.getChildCount())%5;

        while (add != 0){
            LinearLayout aa = addItemList("","","",0,0);
            aa.setVisibility(View.INVISIBLE);
            _father.addView(aa);
            add--;
        }


        _onceCase = ID;

        _objectButton.setClickable(true);
        _objectButton.setAlpha(1);

        if(adsPopUp.isLoaded()){
            adsPopUp.show();
        }
    }

    public LinearLayout  addItemList(String ID, String Weapon, String Skin, int Color, int Special){
        try{
            LinearLayout _child = new LinearLayout(getApplicationContext());
            _child.setOrientation(LinearLayout.VERTICAL);
            _child.setTag(ID);


            ImageView _skin = new ImageView(getApplicationContext());

            if(ID == "special"){
                _skin.setImageBitmap(toolBox.getBitmap("special/special"+Special+".png",70, 50));
            }else{
                _skin.setImageBitmap(toolBox.getBitmap("item/"+ID+".png", 70, 50)); // TODO STİCKER DESTEKLEMİYO WEAPON -> ITEM OLCAK
            }

            TextView _weaponName = new TextView(getApplicationContext());
            _weaponName.setTypeface(_font);
            _weaponName.setText(Weapon);
            _weaponName.setTextSize(8);
            _weaponName.setBackgroundColor(toolBox.getColorValue(Color));
            _weaponName.setTextColor(getResources().getColor(R.color.textcolor));

            TextView _skinName = new TextView(getApplicationContext());
            _skinName.setTypeface(_font);
            _skinName.setText(Skin);
            _skinName.setTextSize(8);
            _skinName.setBackgroundColor(toolBox.getColorValue(Color));
            _skinName.setTextColor(getResources().getColor(R.color.textcolor));


            _child.addView(_skin);
            _skin.setBackgroundResource(R.mipmap.gun_background);
            _skin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 50*pixels));

            _child.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f));

            _child.addView(_weaponName);
            _child.addView(_skinName);

           return _child;
        }catch (Exception ex){
            Log.e("ADD ERROR", ID+" - "+ex);
            return null;
        }
    }

    public void listingCases(){
        if(_onceCases){
            _objectCaseList.setVisibility(View.VISIBLE);
            _objectItemList.setVisibility(View.INVISIBLE);
            return;
        }

        try {


            LinearLayout _bigfather = (LinearLayout) findViewById(R.id.caseListChild);

            LinearLayout _father = null;

            Cursor _cr =_db.getCaseList(0);

            int intIndex = 5;

            while (_cr.moveToNext()){

                if(intIndex == 5){
                    intIndex =0;

                    LinearLayout _ly = new LinearLayout(this);

                    _ly.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f));;
                    _ly.setOrientation(LinearLayout.HORIZONTAL);
                    _father = _ly;
                    _bigfather.addView(_ly);
                }

                intIndex++;

                final int _id = _cr.getInt(_cr.getColumnIndex("ID"));
                LinearLayout _child = new LinearLayout(getApplicationContext());
                _child.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams _size = new LinearLayout.LayoutParams(pixels*65, pixels*70,1f);
                _child.setLayoutParams(_size);
                _child.setTag(_id);


                _child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        toolBox.playSound(R.raw.soundclick);

                        ImageView _oCaseImage =  (ImageView)findViewById(R.id.caseImage);
                        ImageView _oKeyImage = (ImageView)findViewById(R.id.keyImage);
                        TextView _oCaseName = (TextView)findViewById(R.id.caseText);
                        _oCaseName.setTypeface(_font);
                        TextView _oKeyName =(TextView)findViewById(R.id.keyText);
                        _oKeyName.setTypeface(_font);

                        _oCaseImage.setImageBitmap(toolBox.getBitmap("case/case"+_id+".png", 0, 80));

                        _oKeyImage.setImageBitmap(toolBox.getBitmap("key/key"+_id+".png", 0,80));


                        _priceCase = Integer.parseInt(_db.getCaseInfo(_id,"Price"));
                        _priceKey  = Integer.parseInt(_db.getCaseInfo(_id,"KeyPrice"));

                        _oCaseName.setText(_db.getCaseInfo(_id,"Name") + " - "+ (float)_priceCase/100 + " $");

                        _oKeyName.setText(_db.getCaseInfo(_id, "KeyName") + " - "+ (float)_priceKey/100 + " $");

                        LinearLayout _oCase = (LinearLayout)findViewById(R.id.caseFather);
                        _oCase.setTag(_id);

                        LinearLayout _oKey = (LinearLayout)findViewById(R.id.keyFather);
                        _oKey.setTag(_id);

                        LinearLayout _re = (LinearLayout)findViewById(R.id.caseAndKey);

                        _re.bringToFront();

                        _oCase.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toolBox.playSound(R.raw.soundclick);
                                _objectItemList.setVisibility(View.INVISIBLE);
                                listingCases();
                            }
                        });

                        _oKey.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toolBox.playSound(R.raw.soundclick);
                                listingCases();
                            }
                        });

                        _caseId = _id;
                        _caseSpecial = Integer.valueOf(_db.getCaseInfo(_caseId, "Special"));
                        _caseType = Integer.valueOf(_db.getCaseInfo(_caseId, "Type"));
                        listingItem(_id);
                    }
                });

                ImageView _oSkin = new ImageView(getApplicationContext());
                TextView _oCaseName = new TextView(getApplicationContext());
                _oCaseName.setTypeface(_font);

                _oSkin.setImageBitmap(toolBox.getBitmap("case/case"+_id+".png", 70, 50));

                _oCaseName.setText(_cr.getString(_cr.getColumnIndex("Name")));
                _oCaseName.setTextSize(8);
                _oCaseName.setTextColor(getResources().getColor(R.color.textcolor));

                _child.addView(_oSkin);
                _child.addView(_oCaseName);
                _father.addView(_child);

            }

            int add = 5-_cr.getCount()%5;

            while (add != 0){
                LinearLayout _child = new LinearLayout(this);
                LinearLayout.LayoutParams _size = new LinearLayout.LayoutParams(pixels*65, pixels*70,1f);
                _child.setLayoutParams(_size);
                _child.setVisibility(View.INVISIBLE);
                _child.setClickable(false);
                _father.addView(_child);
                add--;
            }

            _father.bringToFront();
            _onceCases = true;

            toolBox.playSound(R.raw.caseopen);
        }catch (Exception ex){
            Log.e("Listing Case Error", String.valueOf(ex));
            return;
        }
    }

    public void gainItem(int ID,int Stattrak, int Quality, int Color){
        try{
            txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));

            _objectButton.setVisibility(View.INVISIBLE);
            Cursor _cr = _db.getItemInfoCursor(ID);

            String Name = "", Skin = "", PriceQuery = null;


            if(Stattrak != 0){
                Name += "StatTrak™ ";
            }

            int _sound = 0;

            while (_cr.moveToNext()){

                Name += _cr.getString(_cr.getColumnIndex("Name"));
                Skin += _cr.getString(_cr.getColumnIndex("Skin"));
                PriceQuery = _cr.getString(_cr.getColumnIndex("PriceQuery"));
            }
            switch (Color){
                case 2:
                    _sound = R.raw.droprare;
                    break;
                case 3:
                    _sound = R.raw.dropmythical;
                    break;
                case 4:
                    _sound = R.raw.droplegendary;
                    break;
                case 5:
                    _sound = R.raw.dropancient;
                    break;
                case 6:
                    _sound = R.raw.dropuniq;
                    break;
            }

            int[] _check = _db.avaibleQuality(ID,Quality,Stattrak);

            Quality = _check[1];
            Stattrak = _check[2];

            int Price = toolBox.getPriceFromString(Quality,PriceQuery,Stattrak);

            toolBox.playSound(_sound);

            TextView _objectTitle = (TextView)findViewById(R.id.gainTitle);
            final ImageView _objectItemSkin = (ImageView)findViewById(R.id.gainImage);
            final ImageView _objectSkinBack = (ImageView)findViewById(R.id.gainImageBack);
            TextView _objectQualityAndPrice = (TextView)findViewById(R.id.gainQuality);
            _objectQualityAndPrice.setTypeface(_font);
            TextView _objectWeaponAndSkin = (TextView)findViewById(R.id.gainWeapon);
            _objectWeaponAndSkin.setTypeface(_font);

            _objectTitle.setText(getString(R.string.gain));

         //   InputStream _file; //= getAssets().open("weapon/weapon"+ID+"-min.png");
            //Drawable draw = Drawable.createFromStream(_file, null);

            // _objectItemSkin.setImageDrawable(draw);

            _objectItemSkin.setImageBitmap(toolBox.getBitmap("item/"+ID+".png", 140,140)); // TODO Boyutu düzelt

            final RotateAnimation _anim = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            _anim.setDuration(5000);
            _anim.setRepeatCount(Animation.INFINITE);
            _anim.setInterpolator(new LinearInterpolator());

            if(Stattrak != 0){
              ImageView _img = (ImageView)findViewById(R.id.gainImageStattrak);
                _img.setVisibility(View.VISIBLE);
                _img.bringToFront();
            }

            Drawable _drawable = new BitmapDrawable(getResources(),toolBox.getBitmap("ui/quality"+Color+".png",380, 180));
            _objectSkinBack.setBackground(_drawable);



            _objectSkinBack.startAnimation(_anim);

            final float _price = (float)Price/100;

            int _tColor = Color;

            Color = toolBox.getColorValue(Color);

            _objectQualityAndPrice.setText(toolBox.getQualityString(Quality) + " | "+_price+" $");
            _objectQualityAndPrice.setBackgroundColor(Color);
            _objectTitle.setBackgroundColor(Color);


            _objectWeaponAndSkin.setText(Name + " | "+Skin);
            _objectWeaponAndSkin.setBackgroundColor(Color);

            Button _sellButton = (Button)findViewById(R.id.SellButton);
            Button _addButton = (Button)findViewById(R.id.addButton);

            _sellButton.setText(getString(R.string.sell)+" - "+_price+" $");

            final int _fPrice = Price;
            _sellButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _db.setWallet(_fPrice);

                    toolBox.playSound(R.raw.soundadd);

                    txtWallet.setText(String.valueOf((float)_db.getWallet()/100)+" $");

                    Toast.makeText(getApplicationContext(), _price + " $ " + getString(R.string.addwallet), Toast.LENGTH_SHORT).show();
                    _objectOpenScrool.setVisibility(View.INVISIBLE);
                    _objectOpenScrool.setVisibility(View.INVISIBLE);
                    _objectBackButton.setVisibility(View.VISIBLE);
                    _objectButton.setVisibility(View.VISIBLE);
                    _objectGainItem.setVisibility(View.INVISIBLE);
                    _objectItemList.setVisibility(View.VISIBLE);
                    _objectItemPanel.setVisibility(View.VISIBLE);

                    _objectItemSkin.setImageBitmap(null);
                    _objectSkinBack.setImageBitmap(null);
                    _anim.cancel();
                }
            });

            final int fID = ID, fQuality = Quality, fStattrak = Stattrak, fColor = _tColor;
            _addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _db.addItem(fID, fQuality, fStattrak/5, fColor);

                    toolBox.playSound(R.raw.soundadd);

                    Toast.makeText(getApplicationContext(),getString(R.string.addingenventer), Toast.LENGTH_SHORT).show();

                    _objectOpenScrool.setVisibility(View.INVISIBLE);
                    _objectOpenScrool.setVisibility(View.INVISIBLE);
                    _objectBackButton.setVisibility(View.VISIBLE);
                    _objectButton.setVisibility(View.VISIBLE);
                    _objectGainItem.setVisibility(View.INVISIBLE);
                    _objectItemList.setVisibility(View.VISIBLE);
                    _objectItemPanel.setVisibility(View.VISIBLE);

                    _objectItemSkin.setImageBitmap(null);
                    _objectSkinBack.setImageBitmap(null);
                    _anim.cancel();

                }
            });

        }catch (Exception ex){
            Log.e("GAIN ITEM", String.valueOf(ex));
            return;
        }
    }


    int[] winItem = null;

   List<Integer> _itemList = new ArrayList<Integer>();

    public void openCase(){
        if(_commercial == 5){
            Intent _showAds = new Intent(this, ADS.class);
            startActivity(_showAds);
            return;
        }

        _itemList.clear();

        _objectItemPanel.setVisibility(View.INVISIBLE);

        _objectButton.setText(R.string.case_opening);
        _objectButton.setClickable(false);
        _objectButton.setAlpha(0.7f);

        _objectOpenScrool.setVisibility(View.VISIBLE);

        _objectOpenScrool.removeAllViews();

        final ScroolClass _scroolClass = new ScroolClass(getApplicationContext());

        _objectOpenScrool.addView(_scroolClass.Scrool());
        _objectOpenScrool.setVisibility(View.VISIBLE);
        _objectOpenScrool.bringToFront();

        for(int i = 0;i < 27; i++) {
            // Renk  - Renk KOdu ( 00000- 11111 ..) - ID -  Quality - Stattrak
            int _itemColor = toolBox.chanceColor();
            int _itemId = 0;
            if(_itemColor != 6){
                _itemId = _db.getRandomId(_itemColor,_caseId);
            }

            int _itemStatTrak = toolBox.chanceStattrak();

            if(_itemStatTrak == 1){
                if(!_db.checkStattrak(_itemId) && _itemColor != 6){
                    _itemStatTrak = 0;
                }
            }

            Cursor _cr = _db.getItemInfoCursor(_itemId);

            String _tempName = "", _tempSkin = "", _itemName = "", _itemSkin = "";

            while(_cr.moveToNext()){
                _tempName = _cr.getString(_cr.getColumnIndex("Name"));
                _tempSkin = _cr.getString(_cr.getColumnIndex("Skin"));
            }

            if(_itemStatTrak != 0){
                _itemName +=  "StatTrak™ ";
            }

            if (_itemId == 0) {
                _itemName = getResources().getString(R.string.rare);
                _itemSkin += getResources().getString(R.string._specialItem);
            }else{
                _itemName += _tempName;
                _itemSkin += _tempSkin;
            }

            String imageUrl = "";

            if(_itemId != 0){
                imageUrl = "item/" + _itemId + ".png";
            }else{
                imageUrl = "special/special" + _caseSpecial + ".png";
            }
            _scroolClass.addItem(imageUrl, _itemName, _itemSkin, _itemColor,true);

            _itemList.add(_itemId);
            _itemList.add(_itemStatTrak);
            _itemList.add(_itemColor);
        }


        _scroolClass.startScrool();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int _winItemId = _itemList.get(_scroolClass._index*3),_winItemQuality =toolBox.chanceQuality(), _winItemStattrak =_itemList.get(_scroolClass._index*3+1);

                if(_winItemId == 0){
                    _winItemId = _db.getRandomId(6,_caseId);
                }

                if(!_db.checkStattrak(_winItemId)){
                    _winItemStattrak = 0;
                }

                int[] _check = _db.avaibleQuality(_winItemId,_winItemQuality,_winItemStattrak);

                final int  _finalId = _winItemId, _finalQuality = _check[1], _finalStattrak = _check[2], finalColor = _itemList.get(_scroolClass._index*3+2);

                toolBox.playSound(R.raw.caseopen);

                if(toolBox.gainXP(1)){
                    Intent _levelUp = new Intent(getApplicationContext(),LevelUp.class);
                    startActivity(_levelUp);
                }

                _objectButton.setText(getString(R.string.open_case) +" - "+ ((_priceCase+_priceKey)/100)+" $");
                _objectButton.setClickable(true);
                _objectButton.setAlpha(1);

                LinearLayout _show = (LinearLayout)findViewById(R.id.gainItem);

                _show.setVisibility(View.VISIBLE);
                _show.bringToFront();
              _objectCaseList.setVisibility(View.INVISIBLE);
            _objectItemList.setVisibility(View.INVISIBLE);
             _objectOpenScrool.setVisibility(View.INVISIBLE);

                gainItem(_finalId, _finalStattrak,_finalQuality, finalColor);
            }
        },7000);
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
