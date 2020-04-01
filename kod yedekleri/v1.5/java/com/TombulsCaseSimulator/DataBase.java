package com.TombulsCaseSimulator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by WORQSTATION on 8.02.2017.
 */

public class DataBase extends SQLiteOpenHelper {

    //region Query - Class

    public DataBase(Context context) {
        super(context, "CaseSimulator", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void databaseUpdate(int Version, String Query){
        if(Integer.parseInt(getConfig("Version")) >= Version){
            return;
        }

        SQLiteDatabase _db = this.getWritableDatabase();

        for(String _query:Query.split(";")){
            try {
                _db.execSQL(_query);
            }catch (Exception ex){
                Log.e("SQL UPDATE ERROR", ex.toString());
            }
        }

        setConfig("Version", String.valueOf(Version));
    }

    public void addQuery(String _QUERY){
        try {
            SQLiteDatabase _db = this.getWritableDatabase();

            _db.execSQL(_QUERY);
        }catch (Exception ex){
            Log.e("ADD QUERY", String.valueOf(ex));
        }
    }

    //endregion

    //region Jackpot

    public void addJackpotItem(int ID,int Quality, int Stattrak, boolean Bot){
        SQLiteDatabase _db = this.getWritableDatabase();

        if(Bot){
            _db.execSQL("INSERT INTO jackpot (ID, Bot, Quality, Stattrak) VALUES (" + ID + ",1," + Quality + "," + Stattrak + ")");
        }else {
            _db.execSQL("INSERT INTO jackpot (ID, Bot, Quality, Stattrak) VALUES (" + ID + ",0," + Quality + "," + Stattrak + ")");
        }
    }

    public void removeJackpotItem(int InventoryId){
        SQLiteDatabase _db  = this.getWritableDatabase();

        _db.execSQL("DELETE FROM jackpot WHERE ID = "+InventoryId);
    }

    public boolean checkJackpotItem(int InventoryId){
        SQLiteDatabase _db = this.getReadableDatabase();

        Cursor _cr = _db.rawQuery("SELECT * FROM jackpot WHERE ID = "+InventoryId, null);

        if(_cr.getCount() == 0){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getJackpotItem(){
        SQLiteDatabase _db = this.getReadableDatabase();

        Cursor _cr = _db.rawQuery("SELECT ID FROM jackpot WHERE Bot = 0", null);

        return _cr;
    }

    public Cursor getJackpotWinningItem(){
        SQLiteDatabase _db = this.getReadableDatabase();

        Cursor _cr = _db.rawQuery("SELECT ID,Quality,Stattrak FROM jackpot WHERE BOT = 1", null);

        return _cr;
    }

    public  void clearJackpot(){
        SQLiteDatabase _db = this.getWritableDatabase();

        _db.execSQL("DELETE FROM jackpot");
    }

    //endregion

    //region Trade Up

    public void addTradeUp(int InventoryId,int Case, int Quality, int Stattrak, int Color){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("INSERT INTO tradeup (InventoryId, Cases, Quality, Stattrak, Color) VALUES ("+InventoryId+",'"+Case+"',"+Quality+","+Stattrak+","+Color+")");
    }

    public void removeTradeUp(int InventoryId){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM tradeup WHERE InventoryId = "+InventoryId);
    }

    public boolean checkTradeUpItem(int InventoryId){
        SQLiteDatabase _db = this.getReadableDatabase();

        Cursor _cr = _db.rawQuery("SELECT * FROM tradeup WHERE InventoryId = "+InventoryId, null);

        if(_cr.getCount() == 0){
            return false;
        }else{
            return true;
        }
    }

    public void clearTradeUp(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tradeup");
    }

    public int getTradeUpQuality(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor _cr = db.rawQuery("SELECT Quality FROM tradeup ORDER BY RANDOM() LIMIT 1", null);

        while (_cr.moveToNext()){
            return _cr.getInt(0);
        }

        return 0;
    }

    public int getTradeUpCase(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor _cr = db.rawQuery("SELECT Cases FROM tradeup ORDER BY RANDOM() LIMIT 1", null);

        while (_cr.moveToNext()){
            return _cr.getInt(0);
        }

        return 0;
    }

    public Cursor getTradeUpItems(){
        SQLiteDatabase db =this.getReadableDatabase();

        Cursor _cr = db.rawQuery("SELECT InventoryId FROM tradeup", null);

        return _cr;
    }

    //endregion

    //region Inventory

    public boolean checkStattrak(int ItemId){
        SQLiteDatabase _db = this.getReadableDatabase();

        Cursor _cr = _db.rawQuery("SELECT stattrak FROM items WHERE ID = "+ItemId,null);

        while (_cr.moveToNext()){
            if(_cr.getInt(0) ==1 ){
                return true;
            }
        }

        return false;
    }

    public String getInventoryItemInfo(int ID, String Tag){
        SQLiteDatabase _db = this.getReadableDatabase();

        Cursor _cr = _db.rawQuery("SELECT "+Tag+" FROM inventory WHERE ID = "+ID, null);

        while (_cr.moveToNext()){
            return _cr.getString(0);
        }

        return "";
    }

    public boolean addItem(int _ID, int _QUALTY, int _STATTRAK, int Color){
        try {

            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL("INSERT INTO inventory (ItemId, Quality, Stattrak, Color) VALUES ("+_ID+","+_QUALTY+","+_STATTRAK+","+Color+")");
            return true;
        }catch (Exception ex){
            Log.e("ADD ITEM", String.valueOf(ex));
            return false;
        }
    }

    public Cursor getInventory(int _Count, int _TYPE, int _Quality, int _STATTRAK, int _Page, int Color){
        try {  String _query = "SELECT * FROM inventory";

            boolean _addOptions = false;

            if(_TYPE != -1){
                _query += " WHERE ";
                _query += " type = "+_TYPE;
                _addOptions = true;
            }

            if(_Quality != -1){
                if(_addOptions){
                    _query += " AND ";
                }else{
                    _query += " WHERE ";
                    _addOptions = true;
                }
                _query += " quality = "+_Quality;
            }

            if(_STATTRAK != -1){
                if(_addOptions){
                    _query += " AND ";
                }else{
                    _query += " WHERE ";
                    _addOptions = true;
                }

                _query += " stattrak = " +  _STATTRAK;
            }

            if(Color != -1){
                if(_addOptions){
                    _query += " AND ";
                }else{
                    _query += " WHERE ";
                }

                _query += " Color = '" + Color+"'";
            }

            _query += " ORDER BY Color DESC Limit "+_Count*_Page+","+_Count;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor _cr = db.rawQuery(_query, null);

            return  _cr;
        }catch (Exception ex){
            Log.e("GET EKVENTORY", String.valueOf(ex));
            return null;
        }
    }

    public int getInventoryValue(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor _cr = db.rawQuery("SELECT Quality, ItemId, Stattrak FROM inventory", null);

        int Price = 0;

        while (_cr.moveToNext()){

            Cursor _subCursor = db.rawQuery("SELECT PriceQuery FROM items WHERE ID = "+_cr.getString(1), null);

            while (_subCursor.moveToNext()){
                Price += Integer.valueOf(_subCursor.getString(0).split("_")[_cr.getInt(0)+(_cr.getInt(2)*5)]);
            }
        }

        return  Price;
    }

    public int getInventorySize(){
        SQLiteDatabase _db = this.getReadableDatabase();

        return _db.rawQuery("SELECT ID FROM inventory", null).getCount();
    }

    public void removeInventory(int ID){
        SQLiteDatabase _db = this.getWritableDatabase();

        _db.execSQL("DELETE FROM inventory WHERE ID = "+ID);
    }

    //endregion

    //region Get List - Info

    public Cursor getItemList(int CaseId){
        try {
            String _Query = "SELECT ID, Skin, Name, Color FROM items WHERE Special = 0 AND Cases Like '["+CaseId+"]' ORDER BY Color ASC";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor _cr =  db.rawQuery(_Query, null);
            return  _cr;}catch (Exception ex){
            Log.e("GET CASE ITEMS", String.valueOf(ex));
            return null;
        }


        // ID, SKIN, WEAPONNAME, COLOR
    }

    public Cursor getCaseList(int CaseType){
        try {
            String _Query = "SELECT ID, Name,KeyName, Price, KeyPrice, Special, Type FROM cases";

            if(CaseType != 0){
                _Query += " WHERE Type = "+CaseType;
            }

            SQLiteDatabase db = this.getReadableDatabase();

            _Query+=" ORDER BY ID DESC";

            Cursor _cr =  db.rawQuery(_Query, null);
            return  _cr;
        }catch (Exception ex){
            Log.e("GET CASE LIST", String.valueOf(ex));
            return null;
        }


        // ID, NAME, KEYNAME, PRICE, KEYPRICE, SPECIAL, TYPE
    }

    public String getCaseInfo(int CaseId, String Tag){
        try {
            String _Query = "SELECT "+Tag+" FROM cases WHERE ID = "+CaseId;

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cr = db.rawQuery(_Query,null);
            while (cr.moveToNext()){
                return cr.getString(0);
            }
            return "";
        }catch (Exception ex){
            Log.e("GET CASE INFO", String.valueOf(ex));
            return"";
        }
    }

    public Cursor getItemsId(int CaseId, int Color){
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            String _Query = "SELECT ID, Stattrak FROM items WHERE COLOR = "+Color+" AND Cases Like '["+CaseId+"]'";

            Cursor _cr = db.rawQuery(_Query, null);
            return _cr;}catch (Exception ex){
            Log.e("GET ITEM ID", String.valueOf(ex));
            return null;
        }
    }

    public String getItemInfo(int ItemId, String _TAG){
        try {
            String _Query = "SELECT "+_TAG+" FROM items WHERE ID = "+ItemId;

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor _cr =  db.rawQuery(_Query, null);

            String result = "";

            while (_cr.moveToNext()){
                result = _cr.getString(0);
            }

            return  result;
        }catch (Exception ex){
            Log.e("GET ITEM INFO", String.valueOf(ex));
            return "";
        }
    }

    public Cursor getItemInfoCursor(int ItemId){
        try {
            String _Query = "SELECT ID, Skin, Name, PriceQuery, Color, Cases FROM items WHERE ID = "+ItemId;

            SQLiteDatabase db = this.getReadableDatabase();

            Cursor _cr =  db.rawQuery(_Query, null);

            return  _cr;
        }catch (Exception ex){
            Log.e("GET ITEM INFO", String.valueOf(ex));
            return null;
        }
    }


    //endregion

    //region Random

    // Query Id, Item Id, Quality, StatSou
    public int getRandomId(int ItemColor, int CaseId){

        SQLiteDatabase _db =this.getReadableDatabase();

        String stringQuery = "SELECT ID FROM items";

        boolean _once = false;

        if(ItemColor == 6){
            stringQuery += " WHERE Special = 1";
            _once = true;
        }else{
            if(ItemColor != -1){
            stringQuery += " WHERE Special = 0 AND Color = "+ItemColor;
                _once = true;
            }
        }

        if(CaseId != -1){
            if(_once){
            stringQuery += " AND Cases Like '%["+CaseId+"]%'";
            }else{
                stringQuery += " WHERE Cases Like '%["+CaseId+"]%'";
            }
        }

        stringQuery += " ORDER BY RANDOM() LIMIT 1";

        Cursor _cr = _db.rawQuery(stringQuery,null);

        while (_cr.moveToNext()){
            return _cr.getInt(_cr.getColumnIndex("ID"));
        }

        return 0;
    }


    // Quality ,Stattrak - Souvenir
    public int[] avaibleQuality(int ID, int Quality, int StatSou){;
        String priceQuery = getItemInfo(ID,"PriceQuery");

        String arrayPriceList[] = priceQuery.split("_");
        int intTry = 0, intQuality = Quality, intStatSou = StatSou;

        while (Integer.parseInt(priceQuery.split("_")[Quality+(StatSou*5)]) < 0 || Integer.parseInt(priceQuery.split("_")[Quality+(StatSou*5)]) == 0){

            if(intTry == 10){
                return new int[]{ID,Quality,StatSou};
            }

            if(Quality == 5){
                Quality = 0;
            }

            if(intTry == 5){
                if(StatSou == 0){
                    StatSou = 1;
                }else{
                    StatSou = 0;
                }
            }

            Quality++;
        }

        return new int[]{ID,Quality,StatSou};
    }
   //endregion

    //region Config

    public String getConfig(String Tag){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cr = db.rawQuery("SELECT Value FROM config WHERE Tag = '"+Tag+"'", null);

            while (cr.moveToNext()){
                db.close();

                return cr.getString(0);
            }}catch (Exception ex){

            return "0";
        }

        return "0";

    }

    public  void setPlayerInfo(String Tag, String Value){
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL("UPDATE player SET '"+Tag+"' = '"+Value+"'");

            db.close();
        }catch (Exception ex){
            Log.e("SET WALLER", String.valueOf(ex));
        }
    }

    public  void setConfig(String Tag, String Value){
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            db.execSQL("UPDATE config SET Value = '"+Value+"' WHERE TAG = '"+Tag+"'");

            db.close();
        }catch (Exception ex){
            Log.e("SET WALLER", String.valueOf(ex));
        }
    }

    public  void setWallet(int Price){
        try {
            String _Query = "UPDATE player SET wallet =  wallet + "+ Price;

            SQLiteDatabase db = this.getWritableDatabase();
            if(Price < 0 ){
                _Query = "UPDATE player SET wallet =  wallet + "+ Price;
            }

            db.execSQL(_Query);
            db.close();}catch (Exception ex){
            Log.e("SET WALLER", String.valueOf(ex));
        }
    }


    public int getWallet(){
        try {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cr = db.rawQuery("SELECT wallet FROM player", null);

            while (cr.moveToNext()){
                return cr.getInt(0);
            }
        }catch (Exception ex){
            Log.e("GET WALLER", String.valueOf(ex));
            return 0;
        }
        return 0;
    }
    //endregion

    //region Avatar

    public Cursor getAvatars(int Count, int Page){
        SQLiteDatabase db =  this.getReadableDatabase();

        return db.rawQuery("SELECT * FROM avatars Limit "+Count*Page+","+Count,null);
    }

    public String getAvatarUrl(int ID){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor _cr =db.rawQuery("SELECT image FROM avatars WHERE ID = "+ID,null);

        while (_cr.moveToNext()){
            return _cr.getString(0);
        }

        return "avatar/avatar1.png";
    }

    public int getAvatarCount(){
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT ID FROM avatars",null).getCount();
    }
    //endregion

    public int getStatic(String Tag){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor _cr = db.rawQuery("SELECT VALUE FROM statics WHERE Tag = '"+Tag+"'", null);

        while(_cr.moveToNext()){
            return _cr.getInt(0);
        }

        return 0;
    }

    public void setStatic(String Tag, int Value){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE statics SET VALUE = VALUE +"+Value+" WHERE TAG = '"+Tag+"'");
    }
}
