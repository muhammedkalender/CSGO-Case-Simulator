package com.tombulscasesimulator;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeAvatar extends AppCompatActivity {

    int intPage = 0, intMaxPage = 0;

    ImageView _objectAvatar = null;
    DataBase db = null;
    GridLayout _objectAvatarList = null;
    ImageView _objectBack = null, _objectNext = null;
    ToolClass toolBox = null;
    TextView _objectPage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_change_avatar);

        toolBox = new ToolClass(this);
        db = new DataBase(this);

        _objectAvatar = (ImageView)findViewById(R.id.cAvatar);

        _objectAvatarList = (GridLayout)findViewById(R.id.glAvatar);

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
            }
        });
    }

    public void loadPage(){
        _objectAvatarList.removeAllViews();

        final Cursor _cr =  db.getAvatars(4,intPage);

        while (_cr.moveToNext()){
            ImageView _avatar = new ImageView(getApplicationContext());

            final Bitmap _bmAvatar = toolBox.getBitmap(_cr.getString(1),70*toolBox._pixelSize,70*toolBox._pixelSize);

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
            _objectAvatarList.addView(_avatar);
        }

        _objectPage.setText((intPage+1)+"/"+(intMaxPage+1));
    }
}
