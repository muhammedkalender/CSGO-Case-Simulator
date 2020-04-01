package com.TombulsCaseSimulator;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeAvatar extends AppCompatActivity {

    int intPage = 0, intMaxPage = 0;

    ImageView _objectAvatar = null;
    DataBase db = null;
    ImageView _objectBack = null, _objectNext = null;
    ToolClass toolBox = null;
    TextView _objectPage = null;
    LinearLayout oLLArea1 = null, oLLArea2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_change_avatar);

        toolBox = new ToolClass(this);
        db = new DataBase(this);

        _objectAvatar = (ImageView)findViewById(R.id.cAvatar);

        oLLArea1 = (LinearLayout)findViewById(R.id.area1);
        oLLArea2 = (LinearLayout)findViewById(R.id.area2);

        _objectBack = (ImageView)findViewById(R.id.imgBack);
        _objectNext = (ImageView)findViewById(R.id.imgNext);

        Button _objectButton = (Button)findViewById(R.id.okButton);

        _objectAvatar.setImageBitmap(toolBox.getBitmap(db.getAvatarUrl(Integer.parseInt(db.getConfig("Avatar"))),70*toolBox._pixelSize,70*toolBox._pixelSize));

        _objectPage = (TextView)findViewById(R.id.txtPage);

        intMaxPage = (db.getAvatarCount()/4);

        loadPage();

        _objectNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                if(intPage == intMaxPage){
                    return;
                }

                intPage++;

                loadPage();
            }
        });

        _objectBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolBox.playSound(R.raw.soundclick);
                if(intPage == 0){
                    return;
                }

                intPage--;

                loadPage();
            }
        });

        _objectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Toast.makeText(ChangeAvatar.this, getString(R.string.changeafteravatar), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void loadPage(){
        oLLArea1.removeAllViews();
        oLLArea2.removeAllViews();

        int index = 0;

        final Cursor _cr =  db.getAvatars(4,intPage);



        while (_cr.moveToNext()){
            ImageView _avatar = new ImageView(getApplicationContext());

            final Bitmap _bmAvatar = toolBox.getBitmap(_cr.getString(1),0,0);

            _avatar.setImageBitmap(_bmAvatar);

            final int imageId = _cr.getInt(0);

            _avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toolBox.playSound(R.raw.soundclick);
                    _objectAvatar.setImageBitmap(_bmAvatar);

                     db.setConfig("Avatar", String.valueOf(imageId));
                }
            });

            _avatar.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            if(index < 2){
                oLLArea1.addView(_avatar);
            }else{
                oLLArea2.addView(_avatar);
            }
            index++;
        }

        _objectPage.setText((intPage+1)+"/"+(intMaxPage+1));
    }
}
