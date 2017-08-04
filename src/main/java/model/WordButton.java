package model;

import android.widget.Button;

/**
 * 文字按钮
 * Created by xhx12366 on 2017-07-27.
 */

public class WordButton {
    private int mindex;
    private boolean mIsVisiable;
    private String mWordString;
    private Button mViewButton;


    public WordButton(){
        mIsVisiable = true;
        mWordString = "";
    }

    public int getMindex() {
        return mindex;
    }

    public void setMindex(int mindex) {
        this.mindex = mindex;
    }

    public boolean ismIsVisiable() {
        return mIsVisiable;
    }

    public void setmIsVisiable(boolean mIsVisiable) {
        this.mIsVisiable = mIsVisiable;
    }

    public String getmWordString() {
        return mWordString;
    }

    public void setmWordString(String mWordString) {
        this.mWordString = mWordString;
    }

    public Button getmViewButton() {
        return mViewButton;
    }

    public void setmViewButton(Button mViewButton) {
        this.mViewButton = mViewButton;
    }
}
