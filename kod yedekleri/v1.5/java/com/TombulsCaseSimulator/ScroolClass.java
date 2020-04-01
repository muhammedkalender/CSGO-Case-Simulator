package com.TombulsCaseSimulator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
    int _index = 0;

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

    public void addItem(String ImageURL, String Area1, String Area2, int Color, boolean Background){
        LinearLayout _father = new LinearLayout(context);

        _father.setOrientation(LinearLayout.VERTICAL);

        ImageView _ivAvatar = new ImageView(context);

        if(Background){
            _ivAvatar.setBackgroundResource(R.mipmap.gun_background);
        }

        _ivAvatar.setLayoutParams(new LinearLayout.LayoutParams(120*toolBox._pixelSize,120*toolBox._pixelSize));
        _size = 120*toolBox._pixelSize;

        _ivAvatar.setImageBitmap(toolBox.getBitmap(ImageURL,40*toolBox._pixelSize,45*toolBox._pixelSize));

        TextView _txtNick = toolBox.customTextView(Area1, Color,12,false);
        TextView _txtChance = toolBox.customTextView(Area2,Color,12,false);

        _father.addView(_ivAvatar);
        _father.addView(_txtNick);
        _father.addView(_txtChance);
        _father.setLayoutParams(new LinearLayout.LayoutParams(120*toolBox._pixelSize, ViewGroup.LayoutParams.WRAP_CONTENT,1f));
        _child.addView(_father);
    }

    int intLastScrool = 0;

    public void startScrool(){
        final ToolClass toolBox = new ToolClass(context);


        _scrool.setHorizontalScrollBarEnabled(false);

        new CountDownTimer(5000,50){

            @Override
            public void onTick(long millisUntilFinished) {
                int intScroll = _scrool.getScrollX(), intSpeed = (int) ((float)millisUntilFinished/13000*_size);
                if(_size*22.5 < _scrool.getScrollX() + intSpeed){
                    return;
                }

                _scrool.scrollTo(intScroll + intSpeed,0);

                if( _scrool.getScrollX() - intLastScrool > _size){
                    intLastScrool = _scrool.getScrollX();
                    toolBox.playSound(R.raw.casescrooling);
                }

            }

            @Override
            public void onFinish() {
                _index = (int) (((_scrool.getScrollX()+_size*0.5)/_size+1));
            }
        }.start();

    }

}
