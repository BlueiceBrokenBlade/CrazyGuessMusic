package com.imooc.crazyguessmusic.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imooc.crazyguessmusic.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import data.Const;
import model.IAlterDialogButtonListener;
import model.IWordButtonClickListener;
import model.Song;
import model.WordButton;
import myview.MyGirdView;
import util.MyPlayer;
import util.Util;

public class MainActivity extends Activity implements IWordButtonClickListener{
    //唱片相关动画
    private Animation mPanAnim;
    private LinearInterpolator mPanLin;

    private Animation mBarInAnim;
    private LinearInterpolator mBarInLin;

    private Animation mBarOutAnim;
    private LinearInterpolator mBarOutLin;

    //返回按钮
    private ImageButton btn_bar_back;

    //动画作用view
    private ImageView mViewPan;
    private ImageView mViewPanBar;

    //按键事件
    private ImageButton mBtnPlayStart;

    //是否运行标志
    private boolean mIsRunnig = false;

    //文字框容器
    private ArrayList<WordButton> mAllWords;
    private ArrayList<WordButton> mBtnSelectWords;

    private MyGirdView mMyGirdView;

    //已选文字框UI容器
    private LinearLayout mViewWordsContainer;

    //当前关卡歌曲
    private Song mCurrentSong;
    //当前关索引
    private int mCurrentStageIndex = -1;
    //Log的TAG
    public final static String TAG = "MainActivity";

    //答案状态
    public final static int STATUS_ANSWER_RIGHT = 1;//正确
    public final static int STATUS_ANSWER_WRONG = 2;//错误
    public final static int STATUS_ANSWER_LACK = 3;//不完整

    //当前关索引视图
    private TextView mViewCurrentStage;

    //答案错误提示闪烁次数
    public final static int SPARK_COUNT = 6;

    //过关界面
    private View mPassView;

    //当前金币数量
    private int mCurrentCoins = Const.TOTAL_COINS;
    //金币view
    private TextView mViewCurrentCoins;

    //过关索引的TextView
    private TextView mViewCurrentStagePass;
    //过关歌曲名
    private TextView mViewCurrentSongNamePass;

    //对话框按键事件Listenner
    IAlterDialogButtonListener listener;

    //对话框类型常量
    public static final int ID_DIALOG_DELETE_WORD = 1;
    public static final int ID_DIALOG_TIP_WORD = 2;
    public static final int ID_DIALOG_LACK_COINS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //读取关卡数据
        int[] datas = Util.loadData(MainActivity.this);
        mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
        mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];

        //初始化控件
        mViewPan = (ImageView) findViewById(R.id.imag_discLight);
        mViewPanBar = (ImageView) findViewById(R.id.imageView2);
        mMyGirdView = (MyGirdView) findViewById(R.id.girdview);
        mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
        mViewCurrentCoins = (TextView) findViewById(R.id.text_bar_coin);
        btn_bar_back = (ImageButton) findViewById(R.id.btn_bar_back);

        btn_bar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startActivity(MainActivity.this, FirstActivity.class);
            }
        });

        mViewCurrentCoins.setText(mCurrentCoins+"");

        //注册待选文字框按钮监听器
        mMyGirdView.registerOnWordButtonClick(this);

        //初始化动画
        mPanAnim = AnimationUtils.loadAnimation(this,R.anim.rotate);
        mPanLin = new LinearInterpolator();
        mPanAnim.setInterpolator(mPanLin);
        mPanAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewPanBar.startAnimation(mBarOutAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mBarInAnim = AnimationUtils.loadAnimation(this,R.anim.rotate_in);
        mBarInLin = new LinearInterpolator();
        mBarInAnim.setFillAfter(true);
        mBarInAnim.setInterpolator(mBarInLin);
        mBarInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mViewPan.startAnimation(mPanAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mBarOutAnim = AnimationUtils.loadAnimation(this,R.anim.rotate_out);
        mBarOutLin= new LinearInterpolator();
        mBarOutAnim.setInterpolator(mBarOutLin);
        mBarOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //
                mIsRunnig = false;
                mBtnPlayStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mBtnPlayStart = (ImageButton)findViewById(R.id.btn_play_start);
        mBtnPlayStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayButton();
            }
        });

        //Mediaplayer监听器
        MyPlayer.setCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mViewPan.clearAnimation();
            }
        });

        //初始化文字数据
        initCurrentStageData();

        //金币消费事件1：排除一个错误文字
        handleDeletWord();
        //金币消费事件2：得到正确答案
        handleTipAnswer();

    }

    /**
     * 按钮点击事件
     * @param wordButton
     */
    @Override
    public void onWordButtonClick(WordButton wordButton){
//        Toast.makeText(MainActivity.this,wordButton.getMindex()+"",Toast.LENGTH_SHORT).show();
        setSelectWord(wordButton);

        //检查答案
        switch (checkTheAnswer()){
            case STATUS_ANSWER_RIGHT:
            //答案正确，过关并获取奖励
                handlePassEvent();
                break;
            case STATUS_ANSWER_WRONG:
            //答案错误，闪烁提示
                sparkTheWrods();
                break;
            case STATUS_ANSWER_LACK:
            //答案缺失,

                break;
        }
    }

    /**
     * 清除答案
     * @param wordButton
     */
    private void clearTheAnswer(WordButton wordButton){
        //设置答案框文字以及可见性
        wordButton.getmViewButton().setText("");
        wordButton.setmWordString("");
        wordButton.setmIsVisiable(false);

        //设置待选文字框的可见性
        mAllWords.get(wordButton.getMindex()).getmViewButton().setVisibility(View.VISIBLE);
        mAllWords.get(wordButton.getMindex()).setmIsVisiable(true);

        //答案闪烁提示清除显示正常白色文字
        for (int i = 0; i < mBtnSelectWords.size() ;i++){
            mBtnSelectWords.get(i).getmViewButton().setTextColor(Color.WHITE);
        }
    }

    /**
     * 选择答案
     * @param wordButton
     */
    private void setSelectWord(WordButton wordButton){
        for(int i = 0; i < mBtnSelectWords.size(); i++){
            if(mBtnSelectWords.get(i).getmWordString().length() == 0){
                //设置答案文字框内容以及可见性
                mBtnSelectWords.get(i).setMindex(wordButton.getMindex());
                mBtnSelectWords.get(i).getmViewButton().setText(wordButton.getmWordString());
                mBtnSelectWords.get(i).setmIsVisiable(true);
                //记录索引值
                mBtnSelectWords.get(i).setmWordString(wordButton.getmWordString());

                //设置待选文字框的可见性
                wordButton.getmViewButton().setVisibility(View.INVISIBLE);
                wordButton.setmIsVisiable(false);
                break;
            }
        }
    }

    /**
     * 点击盘片中央的播放按钮，开始播放音乐
     */
    private void handlePlayButton(){
        if(mViewPanBar != null){
            if(!mIsRunnig){
                mIsRunnig = true;

                //开始拨杆进入动画
                mViewPanBar.startAnimation(mBarInAnim);
                mBtnPlayStart.setVisibility(View.INVISIBLE);

                //播放音乐
                MyPlayer.playSong(MainActivity.this,
                        mCurrentSong.getSongFileName());
            }
        }
    }

    @Override
    protected void onPause() {
        //暂停动画
        mViewPan.clearAnimation();

        //暂停音乐
        MyPlayer.stopSong(MainActivity.this);

        //保存关卡数据
        Util.savaData(MainActivity.this, mCurrentStageIndex-1, mCurrentCoins);

        super.onPause();
    }

    /**
     * 获取当前关卡歌曲信息
     * @param stageIndex
     * @return
     */
    private Song loadStageSongInfo(int stageIndex){
        Song song = new Song();

        String[] stage = Const.SONG_INFO[stageIndex];

        song.setSongFileName(stage[Const.INDEX_FILE_NAME]);
        song.setSongName(stage[Const.INDEX_SONG_NAME]);

        return song;
    }

    /**
     * 初始化当前关卡数据
     */
    private void initCurrentStageData(){

        //获取当前关卡歌曲信息
        mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);

        //初始化已选文字框数据
        mBtnSelectWords = initWordSelect();

        //清除原来的答案框
        mViewWordsContainer.removeAllViews();

        //增加新的答案框
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150,150);
        for(int i = 0; i< mBtnSelectWords.size(); i++){
            mViewWordsContainer.addView(mBtnSelectWords.get(i).getmViewButton(),
                    params);
        }

        //当前关索引
        mViewCurrentStage= (TextView) findViewById(R.id.text_current_stage);
        if(mViewCurrentStage != null){
            mViewCurrentStage.setText(mCurrentStageIndex+1+"");
        }

        //初始化待选文字框数据
        mAllWords = initAllWord();

        //更新数据
        mMyGirdView.updateData(mAllWords);

        //一开始就播放音乐
        handlePlayButton();
    }

    /**
     * 初始化待选文字框
     * @return
     */
    private ArrayList<WordButton> initAllWord(){
        ArrayList<WordButton> data = new ArrayList<WordButton>();

        //获得所有待选文字
        String[] words = Util.generateWord(mCurrentSong.getSongName());

        //向待选文字区域填充文字
        for (int i = 0; i < MyGirdView.COUNTS_WORDS; i++){
            WordButton button = new WordButton();

            button.setmWordString(words[i]);

            data.add(button);
        }

        return data;
    }



    /**
     * 初始化已选文字框
     * @return
     */
    private ArrayList<WordButton> initWordSelect(){
        ArrayList<WordButton> data = new ArrayList<WordButton>();
        View v;

        for(int i=0; i < mCurrentSong.getNameLength(); i++){
            v = Util.getView(MainActivity.this,R.layout.self_view_gridview_item);

            final WordButton holder = new WordButton();

            holder.setmViewButton((Button) v.findViewById(R.id.item_btn));
            holder.getmViewButton().setTextColor(Color.WHITE);
            holder.setmWordString("");
            holder.setmIsVisiable(false);
            holder.getmViewButton().setBackgroundResource(R.drawable.game_wordblank);
            holder.getmViewButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearTheAnswer(holder);
                }
            });

            data.add(holder);
        }

        return data;
    }

    /**
     * 检查答案是否正确或者缺损
     * @return
     */
    public int checkTheAnswer(){
        //首先检查答案长度
        for(int i = 0; i < mBtnSelectWords.size(); i++){
            //如果有空值，代表答案不完整
            if(mBtnSelectWords.get(i).getmWordString().length() == 0){
                return STATUS_ANSWER_LACK;
            }
        }

        //组装答案框的字符串，然后比对歌名
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < mBtnSelectWords.size(); i++){
            sb.append(mBtnSelectWords.get(i).getmWordString());
        }

        return (sb.toString().equals(mCurrentSong.getSongName())) ?
                STATUS_ANSWER_RIGHT :
                STATUS_ANSWER_WRONG ;
    }

    /**
     * 处理过关事件
     */
    private void handlePassEvent(){
        //停止音乐
        MyPlayer.stopSong(MainActivity.this);

        //停止未完成的动画
        mViewPan.clearAnimation();

        //金币音效
        MyPlayer.playSound(MainActivity.this, MyPlayer.INDEX_SOUND_COIN);

        //奖励金币
        handleCoins(getResources().getInteger(R.integer.pass_stage_reward));

        //显示过关界面
        mPassView = (LinearLayout)findViewById(R.id.pass_view);
        mPassView.setVisibility(View.VISIBLE);

        //显示过关索引
        mViewCurrentStagePass = (TextView) findViewById
                (R.id.text_current_stage_pass);
        if(mViewCurrentStagePass != null){
            mViewCurrentStagePass.setText(mCurrentStageIndex+1+"");
        }

        //显示过关歌名
        mViewCurrentSongNamePass = (TextView) findViewById
                (R.id.text_current_song_name_pass);
        if(mViewCurrentSongNamePass != null){
            mViewCurrentSongNamePass.setText(mCurrentSong.getSongName());
        }

        //“下一题”按钮事件
        ImageButton btn_next_stage = (ImageButton) findViewById(R.id.btn_next);
        btn_next_stage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否通关
                if(mCurrentStageIndex == Const.SONG_INFO.length - 1){
                    //进入通关界面
                   Util.startActivity(MainActivity.this, PassActivity.class);
                    //重置游戏数据
                    resetData();
                }else{
                    mPassView.setVisibility(View.GONE);
                    //加载关卡数据
                    initCurrentStageData();
                }
            }
        });

        //"分享到微信"按钮事件
        ImageButton btn_share_weixin = (ImageButton) findViewById(R.id.btn_share);
        btn_share_weixin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 答案错误，闪烁提示
     */
    private void sparkTheWrods(){

        //定时器任务
        TimerTask task = new TimerTask() {
            boolean mChange = false;
            int mSparkTimes = 0;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(++mSparkTimes > SPARK_COUNT){
                            return;
                        }

                        for (int i = 0; i < mBtnSelectWords.size() ;i++){
                            mBtnSelectWords.get(i).getmViewButton().setTextColor
                                    (mChange ? Color.RED : Color.WHITE);
                        }

                        mChange = !mChange;
                    }
                });
            }
        };

        Timer timer = new Timer();
        timer.schedule(task,1,150);//表示1毫秒后开始 150毫秒为周期重复执行
    }

    /**
     * 金币消费事件1：处理删除待选文字事件
     */
    private void handleDeletWord(){
        ImageButton button = (ImageButton) findViewById(R.id.btn_delet_word);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //对话框提示用户确认操作
                showConfirmDialog(ID_DIALOG_DELETE_WORD);
            }
        });
    }

    /**
     * 删除待选文字
     */
    private void deleteWord(){
        int i = 0;
        final int deletCoins = this.getResources().getInteger(R.integer.pay_delete_word);
        while(++i <= MyGirdView.COUNTS_WORDS ){
            Random random = new Random();
            int index = random.nextInt(MyGirdView.COUNTS_WORDS);
            WordButton wordButton = mAllWords.get(index);
            //随机一个待选文字按钮，如果其可见且不为歌名所含文字，则可成功排除
            if(wordButton.ismIsVisiable() && !mCurrentSong.
                    getSongName().contains(wordButton.getmWordString())){

                if(!handleCoins(-deletCoins)){
                    //金币不够，对话框提示
                    showConfirmDialog(ID_DIALOG_LACK_COINS);

                    return;
                }

                wordButton.setmIsVisiable(false);
                wordButton.getmViewButton().setVisibility(View.INVISIBLE);
                break;
            }
        }
    }

    /**
     * 金币消费事件2：提示一个正确答案的文字
     */
    private void handleTipAnswer(){
        ImageButton button = (ImageButton) findViewById(R.id.btn_tip_answer);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //对话框提示用户确认操作
                showConfirmDialog(ID_DIALOG_TIP_WORD);
            }
        });
    }

    /**
     * 提示正确答案
     */
    private void tipAnswer(){
        boolean tipWord = false;
        final int tipCoins = this.getResources().getInteger(R.integer.pay_tip_answer);

        for(int i = 0; i < mBtnSelectWords.size(); i++){
            if(mBtnSelectWords.get(i).getmWordString().length() == 0){
                if(!handleCoins(-tipCoins)){
                    //金币不够，对话框提示
                    showConfirmDialog(ID_DIALOG_LACK_COINS);
                    return;
                }

                onWordButtonClick(findTheAnswerWord(i));
                tipWord = true;

                break;
            }
        }

        //因为答案框已满无法提示答案，闪烁提示
        if(!tipWord){
            sparkTheWrods();
        }

    }

    /**
     * 金币减少，增加操作
     * @param data
     * @return
     */
    private boolean handleCoins(int data){
        if(mCurrentCoins + data >= 0){
            mCurrentCoins += data;
            mViewCurrentCoins.setText(mCurrentCoins+"");
            return true;
        }else{
            //金币余额不足
            return false;
        }
    }

    /**
     * 获得歌名中某个文字所在的待选按钮
     * @param index
     * @return
     */
    private WordButton findTheAnswerWord(int index){
        WordButton wordButton = null;

        for(int i = 0; i < MyGirdView.COUNTS_WORDS; i++){
            wordButton = mAllWords.get(i);

            if(wordButton.getmWordString().equals(""+mCurrentSong.getSongName()
                    .toCharArray()[index])){
                return wordButton;
            }
        }

        return null;
    }

    /**
     * 删除一个错误答案的对话框事件
     */
    private IAlterDialogButtonListener mBtnOkDeleteWordListener = new
            IAlterDialogButtonListener() {
                @Override
                public void onClick() {
                    deleteWord();
                }
            };

    /**
     * 提示一个正确答案的对话框事件
     */
    private IAlterDialogButtonListener mBtnOkTipWordListener = new
            IAlterDialogButtonListener() {
                @Override
                public void onClick() {
                    tipAnswer();
                }
            };

    /**
     * 提示金币不足的对话框事件
     */
    private IAlterDialogButtonListener mBtnOkLackCoinsListener = new
            IAlterDialogButtonListener() {
                @Override
                public void onClick() {

                }
            };

    /**
     * 不同对话框事件处理
     * @param id
     */
    private void showConfirmDialog(int id){
        switch (id){
            case ID_DIALOG_DELETE_WORD:
                Util.showDialog(MainActivity.this,"确定花费"+
                        getResources().getInteger(R.integer.pay_delete_word)+
                        "个金币去掉一个错误答案",
                        mBtnOkDeleteWordListener);
                break ;
            case ID_DIALOG_TIP_WORD:
                Util.showDialog(MainActivity.this,"确定花费"+
                        getResources().getInteger(R.integer.pay_tip_answer)+
                        "个金币提示一个正确答案", mBtnOkTipWordListener);
                break;
            case ID_DIALOG_LACK_COINS:
                Util.showDialog(MainActivity.this,"金币不足!",mBtnOkLackCoinsListener);
                break;
        }
    }

    /**
     * 游戏通关重置数据
     */
    private void resetData(){
        //重置游戏关卡，金币保留
        Util.savaData(MainActivity.this, 0, mCurrentCoins);
        //先读取重置的关卡数据,因为onPause处会统一再次保存数据
        int[] datas = Util.loadData(MainActivity.this);
        mCurrentStageIndex = datas[Const.INDEX_LOAD_DATA_STAGE];
        mCurrentCoins = datas[Const.INDEX_LOAD_DATA_COINS];
    }

}
