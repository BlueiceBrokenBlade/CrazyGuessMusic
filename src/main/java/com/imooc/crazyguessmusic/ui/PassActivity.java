package com.imooc.crazyguessmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.imooc.crazyguessmusic.R;

import util.Util;

public class PassActivity extends Activity {
    FrameLayout frameLayout;
    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_pass_view);

        frameLayout = (FrameLayout) findViewById(R.id.layout_bar_coin);
        frameLayout.setVisibility(View.INVISIBLE);

        btn_back = (ImageButton) findViewById(R.id.btn_bar_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startActivity(PassActivity.this, FirstActivity.class);
            }
        });
    }
}
