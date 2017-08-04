package com.imooc.crazyguessmusic.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.imooc.crazyguessmusic.R;

import data.Const;
import util.Util;

public class FirstActivity extends Activity {
    private ImageButton btn_start;
    private TextView text_current_stage;
    private int[] datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btn_start = (ImageButton) findViewById(R.id.btn_start);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startActivity(FirstActivity.this,
                        MainActivity.class);
            }
        });

        //继续的关卡数
        text_current_stage = (TextView) findViewById(R.id.text_current_stage);
    }

    @Override
    protected void onResume() {

        datas = Util.loadData(FirstActivity.this);
        text_current_stage.setText(datas[Const.INDEX_LOAD_DATA_STAGE]+2+"");

        super.onResume();
    }
}
