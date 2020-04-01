package com.TombulsCaseSimulator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.logging.Handler;

/**
 * Created by WORQSTATION on 21.03.2017.
 */

public class ScroolClass {

    Context context =null;

    ToolClass toolBox = null;

    public ScroolClass(Context _context){
        context = _context;
        toolBox = new ToolClass(context);
    }

    LinearLayout _child = null;
    HorizontalScrollView _scrool = null;
    RelativeLayout _father = null;

    int _size = 0;

    public RelativeLayout Scrool(){
        _father = new RelativeLayout(context);

        ImageView _ivCursor = new ImageView(context);
        _ivCursor.setBackgroundResource(R.mipmap.rulette_up);
        _ivCursor.setClickable(true);

        HorizontalScrollView _subChild = new HorizontalScrollView(context);

        _subChild.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        _subChild.setBackgroundResource(R.mipmap.rulette_down);

        _child = new LinearLayout(context);

        _child.setOrientation(LinearLayout.HORIZONTAL);

        _father.addView(_ivCursor);
        _father.addView(_subChild);

        _scrool=_subChild;
        _subChild.addView(_child);
        _ivCursor.bringToFront();


        return _father;
    }

    public void addObject(LinearLayout _item){
        _child.addView(_item);
    }


    int intIndex = 0;
    int intScrool = 0;

    public void addItem(String ImageURL, String Area1, String Area2, int Color, boolean Background){
        LinearLayout _father = new LinearLayout(context);

        _father.setOrientation(LinearLayout.VERTICAL);

        ImageView _ivAvatar = new ImageView(context);

        if(Background){
            _ivAvatar.setBackgroundResource(R.mipmap.gun_background);
        }

        _ivAvatar.setLayoutParams(new LinearLayout.LayoutParams(100*toolBox._pixelSize,120*toolBox._pixelSize));
        _size = 100*toolBox._pixelSize;

        _ivAvatar.setImageBitmap(toolBox.getBitmap(ImageURL,50*toolBox._pixelSize,55*toolBox._pixelSize));

        TextView _txtNick = toolBox.customTextView(Area1, Color,12,false);
        TextView _txtChance = toolBox.customTextView(Area2,Color,12,false);

        _father.addView(_ivAvatar);
        _father.addView(_txtNick);
        _father.addView(_txtChance);
        _child.addView(_father);
    }

    public void startScrool(){
        final int intDistance = new Random().nextInt((int) (_size*0.7))+(28*_size)/50;

        final int[] index = {0};

        final int _lastScrool = new Random().nextInt((int) (_size*0.4))-10;

        final ToolClass toolBox = new ToolClass(context);

        new CountDownTimer(5000,100){

            @Override
            public void onTick(long millisUntilFinished) {
                if(index[0] < 10){
                    _scrool.setScrollX(_scrool.getScrollX()+(int)(_size * 1.4));
                    index[0]++;
                    toolBox.playSound(R.raw.casescrooling);
                    Log.e("11", String.valueOf(_scrool.getScrollX()));
                }else if(index[0] < 20){
                    _scrool.setScrollX(_scrool.getScrollX()+(int)(_size *0.8));
                    index[0]++;
                    Log.e("11", String.valueOf(_scrool.getScrollX()));
                    if(index[0]%2 == 0){toolBox.playSound(R.raw.casescrooling);}
                }else if(index[0] < 30){
                    _scrool.setScrollX(_scrool.getScrollX()+(int)(_size * 0.6));
                    index[0]++;
                    Log.e("11", String.valueOf(_scrool.getScrollX()));
                    if(index[0]%3 == 0){toolBox.playSound(R.raw.casescrooling);}
                }else if(index[0] < 40){
                    _scrool.setScrollX(_scrool.getScrollX()+(int)(_size * 0.3));
                    index[0]++;
                    Log.e("11", String.valueOf(_scrool.getScrollX()));
                    if(index[0]%5 == 0){toolBox.playSound(R.raw.casescrooling);}
                }else if(index[0] < 47){
                    _scrool.setScrollX(_scrool.getScrollX()+(int)(_size * 0.1));
                    index[0]++;
                    Log.e("11", String.valueOf(_scrool.getScrollX()));
                }else{
                    // last
                    _scrool.setScrollX(_scrool.getScrollX()+(_lastScrool));
                    index[0]++;
                    toolBox.playSound(R.raw.casescrooling);
                }
            }

            @Override
            public void onFinish() {
                _scrool.setSmoothScrollingEnabled(false);
                _scrool.setScrollX(_scrool.getScrollX() - 28*_size);

                for (int i = 0; i < 28; i++){
                    _child.removeViewAt(0);
                }

                _scrool.setSmoothScrollingEnabled(true);
            }
        }.start();

    }

}
