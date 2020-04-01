package com.TombulsCaseSimulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class LevelUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_up);


        final ToolClass _toolBox = new ToolClass(getApplicationContext());
        DataBase _db = new DataBase(getApplicationContext());

        int _level = Integer.parseInt(_db.getConfig("Level"));
        int _prize = _level*1000;
        _toolBox.playSound(R.raw.soundlevelup);
        ImageView _iv = (ImageView)findViewById(R.id.imageView4);

        if(_level > 18){
            _level = 18;
        }
        _iv.setImageBitmap(_toolBox.getBitmap("rank/rank"+_level+".png",0,0));

        TextView txtLevelUp = (TextView)findViewById(R.id.txtLevelUp),_txtPrize = (TextView)findViewById(R.id.txtPrize);

        Button _btn = (Button)findViewById(R.id.okBtn);
        _db.setWallet(_prize);



        txtLevelUp.setText(getString(R.string.newlevel)+" "+_level);
        _txtPrize.setText(getString(R.string.earnprize)+" "+_toolBox.generateMoneyString(_prize));

        _btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _toolBox.playSound(R.raw.soundclick);
                onBackPressed();
            }
        });


        AlphaAnimation  blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(1000); // duration - half a second
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);
        _iv.startAnimation(blinkanimation);

        _txtPrize.startAnimation(blinkanimation);
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();

    }
}
