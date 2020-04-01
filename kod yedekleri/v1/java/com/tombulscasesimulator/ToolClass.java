package com.tombulscasesimulator;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.InputStream;
import java.util.Random;

/**
 * Created by WORQSTATION on 19.02.2017.
 */

public class ToolClass {

    public Context context = null;

    int _pixelSize = 1;

    boolean _configSound = true;

    Typeface _font = null;

    public ToolClass(Context _context){
        context = _context;
        _pixelSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        _font = Typeface.createFromAsset(context.getAssets(), "fonts/MyriadProBold.ttf");

        DataBase _db = new DataBase(context);

        if(Integer.parseInt(_db.getConfig("SOUND")) != 1){
            _configSound = false;
        }
    }

    public Bitmap getBitmap(String URL, int Width, int Height){
        try{
            InputStream _file = context.getAssets().open(URL);
            Bitmap bitmap = BitmapFactory.decodeStream(_file);

            _file.close();

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            Width = Width*_pixelSize;
            Height = Height*_pixelSize;

            if(Width == 0){
                return bitmap.createBitmap(bitmap,0,0,width,height, null,false);
            }

            float scaleWidth = ((float) Width) / width;
            float scaleHeight = ((float) Height) / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            return bitmap.createBitmap(bitmap, 0, 0, width, height,matrix,false);
        }catch (Exception ex){
            Log.e("Resim Hatası", String.valueOf(ex));
            return null;
        }
    }

    public TextView customTextView(String Text, int Color ,int TextSize, boolean Center){
        TextView _txtView = new TextView(context);

        if(Color != -1){
            _txtView.setBackgroundColor(getColorValue(Color));
        }

        if (Center){
            _txtView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        }

        _txtView.setText(Text);
        _txtView.setTextColor(context.getResources().getColor(R.color.textcolor));
        _txtView.setTextSize(TextSize);
        _txtView.setTypeface(_font);

        return _txtView;
    }

    public Button customButton(String Text, int TextSize){
        Button _this = new Button(context);

        _this.setBackground(context.getResources().getDrawable(R.drawable.buttonshape));
        _this.setText(Text);
        _this.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
        _this.setTextColor(context.getResources().getColor(R.color.textcolor));
        _this.setTextSize(TextSize);
        _this.setTypeface(_font);

        return _this;
    }

    public String getQualityString(int ID){
        String result = "";
        switch (ID){
            case 0:
                result = context.getString(R.string._quality0);
                break;
            case 1:
                result = context.getString(R.string._quality1);
                break;
            case 2:
                result = context.getString(R.string._quality2);
                break;
            case 3:
                result = context.getString(R.string._quality3);
                break;
            case 4:
                result = context.getString(R.string._quality4);
                break;
        }

        return result;
    }

    public int getColorValue(int ID){
        int result = 2;

        switch (ID){
            case 0:
                result = context.getResources().getColor(R.color.common1);
                break;
            case 1:
                result = context.getResources().getColor(R.color.uncommon);
                break;
            case 2:
                result = context.getResources().getColor(R.color.rare);
                break;
            case 3:
                result = context.getResources().getColor(R.color.mythical);
                break;
            case 4:
                result = context.getResources().getColor(R.color.legendary);
                break;
            case 5:
                result = context.getResources().getColor(R.color.ancient);
                break;
            case 6:
                result = context.getResources().getColor(R.color.uniq);
                break;
        }

        return result;
    }

    public String generateMoneyString(int Value){
        String result = "";

        result = String.valueOf((float)Value/100)+ " $";

        return result;
    }

    public int getPriceFromString(int Quality, String Price, int Stattrak){
        if(Stattrak > 1){
            Stattrak = 1;
        }
        return Integer.parseInt(Price.split("_")[Quality+(Stattrak*5)]);
    }

    public void sellItem(int EnventoryID, int Price){
        DataBase _db = new DataBase(context);

        _db.removeInventory(EnventoryID);

        _db.setWallet(Price);

        Toast.makeText(context, String.valueOf((float)Price/100) + " $ " + context.getString(R.string.addwallet), Toast.LENGTH_SHORT).show();
    }

    public void playSound(int ID){
        if(!_configSound){
            return;
        }

        try{
            final MediaPlayer _playSound = MediaPlayer.create(context, ID);

            _playSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });

            _playSound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }catch (Exception ex){
            Log.e("Sound Error", String.valueOf(ex));
            return;
        }
    }

    public boolean gainXP(int XP){
        DataBase _db = new DataBase(context);

        int xp = Integer.parseInt(_db.getConfig("XP"));

        xp += XP;

        _db.setConfig("XP", String.valueOf(xp));

        int level = Integer.parseInt(_db.getConfig("Level"));

        if(xp >= (20)){
            _db.setConfig("Level", String.valueOf(level+1));
            _db.setConfig("XP", "0");
            return true;
        }


        return false;
    }

    public void buildAdsBanner(AdView adView){
        MobileAds.initialize(context, context.getString(R.string.AD_BANNER));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public Intent navigationSelect(int id){
        Intent _intent = null;
        switch (id){
            case R.id.nav_mainmenu:
               _intent = new Intent(context, MainActivity.class);
            break;

            case R.id.nav_inventory:
                _intent = new Intent(context, Inventory.class);
            break;

            case R.id.nav_open_case:
                _intent = new Intent(context, OpenCase.class);
            break;

            case R.id.nav_trade_up:
                _intent = new Intent(context, TradeUp.class);
                break;

            case R.id.nav_jackpot:
                _intent = new Intent(context,Jackpot.class);
                break;

            case R.id.nav_dice:
                _intent = new Intent(context, Dice.class);
            break;

            case R.id.nav_flip_coin:
                _intent = new Intent(context, FlipCoin.class);
            break;

            case R.id.nav_mine:
                _intent = new Intent(context, MineSweeper.class);
            break;

            case R.id.nav_config:
                _intent = new Intent(context, Settings.class);
            break;
        }

        return _intent;
    }

    public static int _inventoryPage = 0;
    public static int _inventoryPageMax = 0;
    public static int [] _selectedItemsArray[] = null; //0 invventory index,1 item fiyatı, 2 item ıdi, 3 itemin nesne adı
    public static int _inventoryIndex = 0; // Kaçıncı sayfa
    public static int _itemPrice = 0;

    public LinearLayout inventoryLayout(){
        final LinearLayout father = new LinearLayout(context);
        LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        father.setLayoutParams(_params);

        father.setOrientation(LinearLayout.VERTICAL);

        final GridLayout _selectedItems = new GridLayout(context);
        _selectedItems.setRowCount(5);

        DataBase _db = new DataBase(context);

        //region Envanterdeki itemlerin olacağı bölüm

        LinearLayout _inventoryItemsFather = new LinearLayout(context);
        _inventoryItemsFather.setOrientation(LinearLayout.HORIZONTAL);

        final GridLayout _inventoryItems = new GridLayout(context);
        _inventoryItems.setRowCount(5);

        final ImageView _backButton = new ImageView(context), _nextButton = new ImageView(context);

        _backButton.setImageResource(R.mipmap.back);
        _nextButton.setImageResource(R.mipmap.forwad);

        _backButton.setVisibility(View.INVISIBLE);

        final TextView _totalPrice = customTextView(context.getString(R.string.totalPrice)+generateMoneyString(_itemPrice),0,12,true);
        _totalPrice.setTextColor(context.getResources().getColor(R.color.textcolor));

        _inventoryPageMax = _db.getInventorySize()/9;

        _backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_inventoryPage == 0){
                    return;
                }

                if(_inventoryPage-1 == 0){
                    _backButton.setVisibility(View.INVISIBLE);
                }

                if(_inventoryPage != _inventoryPageMax){
                    _nextButton.setVisibility(View.VISIBLE);
                }

                _inventoryPage--;



                buildInventorPage(_inventoryItems, _selectedItems, _totalPrice);
            }
        });

        _nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_inventoryPage == _inventoryPageMax){
                    return;
                }

                _backButton.setVisibility(View.VISIBLE);

                if(_inventoryPage+1 == _inventoryPageMax){
                    _nextButton.setVisibility(View.INVISIBLE);
                }

                _inventoryPage++;

                buildInventorPage(_inventoryItems, _selectedItems, _totalPrice);
            }
        });

        _inventoryItemsFather.addView(_backButton);
        _inventoryItemsFather.addView(_inventoryItems);
        _inventoryItemsFather.addView(_nextButton);

        father.addView(_inventoryItemsFather);

        //endregion

        //region Menünün kontrolü seçilenlerin fiyatı vs..

        LinearLayout _itemsInfoFather = new LinearLayout(context);
        _inventoryItemsFather.setOrientation(LinearLayout.HORIZONTAL);

        Button _cancelButton = customButton(context.getString(R.string.cancel), 18), _okButton = customButton(context.getString(R.string.okey), 18);



        _cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                father.setVisibility(View.INVISIBLE);

                _selectedItemsArray = null;
                _inventoryPage = 0;
                _inventoryIndex = 0;
                _itemPrice = 0;
            }
        });

        _okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                father.setVisibility(View.INVISIBLE);
                //TODO Ana menüden buraya sıfırlama çağrısı gelcek
            }
        });

        _totalPrice.setText(generateMoneyString(0));

        _itemsInfoFather.addView(_cancelButton);
        _itemsInfoFather.addView(_okButton);
        father.addView(_totalPrice);
        //endregion


        buildInventorPage(_inventoryItems, _selectedItems,_totalPrice);

        return father;

    }



    public void buildInventorPage(final GridLayout _layout, final GridLayout _selectedItems, TextView _priceTextView){
        _layout.removeAllViews();

        DataBase _db = new DataBase(context);

        final Cursor _cr = _db.getInventory(9,0,0,0,_inventoryPage,0);

        while(_cr.moveToNext()){
            final LinearLayout _items = new LinearLayout(context);
            _items.setOrientation(LinearLayout.HORIZONTAL);

            final int intItemPrice = getPriceFromString(_cr.getInt(3), _db.getItemInfo(_cr.getInt(0), "PriceQuery"), _cr.getInt(1));

            TextView _itemQulity = customTextView(getQualityString(_cr.getInt(3)),0,16,false);
            final TextView _oitemPrice  = customTextView(generateMoneyString(intItemPrice),0,16,false);
            TextView _itemName = customTextView(_db.getItemInfo(_cr.getInt(0), "Name"),_cr.getInt(6),16,false);
            TextView _itemSkin  = customTextView(_db.getItemInfo(_cr.getInt(0), "Skin"),_cr.getInt(6),16,false);

            ImageView _itemImage = new ImageView(context);

            RelativeLayout _itemImageArea = new RelativeLayout(context);
            _itemImageArea.addView(_itemImage);
            _itemImageArea.addView(_oitemPrice);

            final int _inventoryId = _cr.getInt(0);

            _items.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  String _bool = _items.getTag().toString();

                    if(_bool == "0"){
                        // Seçildi

                        _items.setTag("1");

                        _layout.removeView(_items);
                        _selectedItems.addView(_items);

                        // İnventory ID, Price, Id, Object Id, aktif
                        _selectedItemsArray[_inventoryIndex++] = new int[]{_inventoryIndex, intItemPrice, _cr.getInt(0), _items.getId(), 1};
                        _itemPrice += intItemPrice;
                        Log.e("11",_items.getParent().getClass().getName());
                    }else{
                        _items.setTag("0");

                        _layout.addView(_items);
                        _selectedItems.removeView(_items);

                        _selectedItemsArray[_inventoryIndex][5] = 0;
                        _itemPrice -= intItemPrice;
                    }
                }
            });

            _layout.addView(_items);
        }
    }

    public int chanceStattrak(){
        int _chance = new Random().nextInt(101);

        if(_chance <= 6){
            return 1;
        }else{
            return 0;
        }
    }

    int chanceColor = 0;

    public int chanceColor(){
                int _chance = new Random().nextInt(1001);

                if(_chance <= 788){
                    return chanceColor = 2;
                }else if(_chance <= 950){
                    return chanceColor =  3;
                }else if(_chance <= 975){
                    return chanceColor = 4;
                }else if(_chance <= 991){
                    return chanceColor = 5;
                }else{
                    return chanceColor = 6;
                }
    }

    public int chanceQuality(){
        final int[] result = {0};

        int _chance = new Random().nextInt(101);
                if(_chance <= 45){
                    return 0;
                }else if(_chance <= 57){
                    return  1;
                }else if(_chance <= 79){
                    return 2;
                }else if(_chance <= 92){
                    return 3;
                }else{
                    result[0] =4;
                    return 4;
                }
            }


}