package com.imooc.crazyguessmusic.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.imooc.crazyguessmusic.R;

import java.util.ArrayList;

import com.imooc.crazyguessmusic.model.IWordButtonClickListener;
import com.imooc.crazyguessmusic.model.WordButton;
import com.imooc.crazyguessmusic.util.Util;

/**
 * Created by xhx12366 on 2017-07-27.
 */

public class MyGirdView extends GridView {
    private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
    private MyGirdAdapter mAdapter;
    private Context mContext;
    private Animation mScaleAnimation;
    private IWordButtonClickListener mWordButtonClickListener;
    public final static int COUNTS_WORDS = 24;

    public MyGirdView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);

        mContext = context;
        mAdapter = new MyGirdAdapter();
        this.setAdapter(mAdapter);
    }

    /**
     * 更新文字选择部分数据
     */
    public void updateData(ArrayList<WordButton> list){
        mArrayList = list;

        //girdview重新设置数据源
        setAdapter(mAdapter);
    }

    class MyGirdAdapter extends BaseAdapter{
        public int getCount(){
            return mArrayList.size();
        }

        public Object getItem(int posion){
            return mArrayList.get(posion);
        }

        public long getItemId(int posion){
            return posion;
        }

        public View getView(int posion,View v,ViewGroup p){
            final WordButton holder;
            if(v == null){
                v = Util.getView(mContext, R.layout.self_view_gridview_item);

                holder = mArrayList.get(posion);
                //加载动画
                mScaleAnimation = AnimationUtils.loadAnimation(mContext,R.anim.scale);
                mScaleAnimation.setStartOffset(100*posion);//动画延迟时间

                holder.setMindex(posion);
//                if(holder.getmViewButton() == null){
                    holder.setmViewButton((Button) v.findViewById(R.id.item_btn));
                    holder.getmViewButton().setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mWordButtonClickListener.onWordButtonClick(holder);
                        }
                    });
//                }

                v.setTag(holder);//对象和视图相关联
            }else{
                holder = (WordButton) v.getTag();
            }
//            v.setLayoutParams(getLayoutParams(v));
//            Log.e("GirdView",holder.getmWordString()+holder.getmViewButton());
            holder.getmViewButton().setText(holder.getmWordString());

            //播放文字按钮载入动画
            v.startAnimation(mScaleAnimation);

            return v;
        }
    }

    /**
     * 根据GirdView大小动态确定子控件大小(未实现)
     * @param converview
     * @return
     */
    private GridView.LayoutParams getLayoutParams(View converview){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = (this.getWidth() - 4)/8;
        int height = (this.getHeight() - 4)/3;
        GridView.LayoutParams layoutParams = (GridView.LayoutParams) converview.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        return layoutParams;
    }

    /**
     * 注册监听接口
     * @param listener
     */
    public void registerOnWordButtonClick(IWordButtonClickListener listener){
       mWordButtonClickListener = listener;
    }
}
