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

import java.util.ArrayList;
import java.util.List;
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

    List<String> stringArrayImageUrl = new ArrayList<String>();
    List<String> stringArrayName = new ArrayList<String>();
    List<String> stringArraySkin = new ArrayList<String>();
    List<Integer> intArrayColor = new ArrayList<Integer>();

    int intBigIndex = 0;

    public void addItem(String ImageURL, String Area1, String Area2, int Color, boolean Background){
        stringArrayImageUrl.add(ImageURL);
        stringArrayName.add(Area1);
        stringArraySkin.add(Area2);
        intArrayColor.add(Color);
    }


    public void addtoScrool(String ImageURL, String Area1, String Area2, int Color, boolean Background){
        LinearLayout _father = new LinearLayout(context);

        _father.setOrientation(LinearLayout.VERTICAL);

        ImageView _ivAvatar = new ImageView(context);

        if(Background){
            _ivAvatar.setBackgroundResource(R.mipmap.gun_background);
        }

        _ivAvatar.setLayoutParams(new LinearLayout.LayoutParams(100*toolBox._pixelSize,80*toolBox._pixelSize));
        _size = 110*toolBox._pixelSize;

        _ivAvatar.setImageBitmap(toolBox.getBitmap(ImageURL,40*toolBox._pixelSize,45*toolBox._pixelSize));

        TextView _txtNick = toolBox.customTextView(Area1, Color,12,false);
        TextView _txtChance = toolBox.customTextView(Area2,Color,12,false);

        _father.addView(_ivAvatar);
        _father.addView(_txtNick);
        _father.addView(_txtChance);

        LinearLayout.LayoutParams layoutFather =  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f);

        layoutFather.setMargins(5*toolBox._pixelSize,5*toolBox._pixelSize,5*toolBox._pixelSize,5*toolBox._pixelSize);



        _father.setLayoutParams(layoutFather);
        _child.addView(_father);
    }


    public void startScrool(){
        final ToolClass toolBox = new ToolClass(context);


        _scrool.setHorizontalScrollBarEnabled(false);

        _child.removeAllViews();
        for(int index = 0; index < 7; index++){
            addtoScrool(stringArrayImageUrl.get(index),stringArrayName.get(index), stringArraySkin.get(index),intArrayColor.get(index),true);
        }

        intBigIndex = 7;

        new CountDownTimer(5000,10){

            @Override
            public void onTick(long millisUntilFinished) {


                int intSpeed = (int) ((float)millisUntilFinished/20000*_size);

                _child.setX(_child.getX() - intSpeed);

                if(_child.getX()*-1 > _size){
                    if(stringArrayImageUrl.size() < intBigIndex+2){
                        onFinish();
                    }
                    _child.removeViewAt(0);
                    _child.setX(_child.getX()+_size);

                    addtoScrool(stringArrayImageUrl.get(intBigIndex),stringArrayName.get(intBigIndex), stringArraySkin.get(intBigIndex),intArrayColor.get(intBigIndex),true);
                    intBigIndex++;
                    toolBox.playSound(R.raw.casescrool);
                }
            }

            @Override
            public void onFinish() {
                _index = intBigIndex-5;

               float floatX = _child.getX();

                if(floatX%_size < 5*toolBox._pixelSize){
                    _child.setX(_child.getX()+toolBox._pixelSize*5);
                }else if(floatX%_size > _size-toolBox._pixelSize*5){
                    _child.setX(_child.getX()-toolBox._pixelSize*5);
                }

                if(_child.getX() > (_size/2)*-1){
                    _index--;
                }

                intBigIndex = 0;

                stringArrayImageUrl.clear();
                stringArrayName.clear();
                stringArraySkin.clear();
                intArrayColor.clear();
            }
        }.start();
    }

}
