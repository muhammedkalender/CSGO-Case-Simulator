package com.tombulscasesimulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class ADS extends AppCompatActivity implements RewardedVideoAdListener {
    public static int _reward = 0, _activity = 0;
    RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        final String AD_UNIT_ID = getString(R.string.AD_REWARDED_VIDEO);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        if (!mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.loadAd(AD_UNIT_ID, new AdRequest.Builder().build()); //.build());
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        _reward = 2000;

        close();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        _reward = 4000;

        close();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        _reward = 1000;


        Log.e("ADS ERROR", String.valueOf(i));

        close();
    }
    private void close(){

        try {
            DataBase _db = new DataBase(this);
            ToolClass toolBox = new ToolClass(this);

            _db.setWallet(_reward);

            switch (_activity){
                case 0:
                    // Main
                    MainActivity.txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                    break;
                case 1:
                    //ınventory
                    Inventory.txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                    break;
                case 2:
                    // Open Case
                    OpenCase.txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                    break;
                case 3:
                    Dice.txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                    break;
                case 4:
                    //Flip Coin
                    FlipCoin.txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                    break;
                case 5:
                    //settings
                    //settings.txtWallet.setText(toolBox.generateMoneyString(_db.getWallet()));
                    break;

            }


            this.finish();}
        catch (Exception ex){
            this.finish();
        }
    }
}
