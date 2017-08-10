package com.imooc.crazyguessmusic.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import java.io.IOException;

/**
 * 音乐播放类
 * Created by xhx12366 on 2017-08-02.
 */

public class MyPlayer {
    //音效种类
    public final static int INDEX_SOUND_ENTER = 0;
    public final static int INDEX_SOUND_CANCEL = 1;
    public final static int INDEX_SOUND_COIN = 2;

    //音效文件名
    private final static String[] SOUND_NAMES = {
            "enter.mp3","cancel.mp3","coin.mp3"
    };

    //音效player集合
    private static MediaPlayer[] mSoundMediaPlayer = new
            MediaPlayer[SOUND_NAMES.length];

    //音乐player
    private static MediaPlayer mMusicMediaPlayer;

    /**
     * 歌曲播放
     * @param context
     * @param fileName
     */
    public static void playSong(Context context, String fileName){
        if(mMusicMediaPlayer == null){
            mMusicMediaPlayer = new MediaPlayer();
        }

        //强制重置
        mMusicMediaPlayer.reset();

        //加载音频文件
        AssetManager assetManager = context.getAssets();
        try {
            AssetFileDescriptor fileDescriptor = assetManager.openFd(fileName);

            mMusicMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),fileDescriptor.getLength());

            mMusicMediaPlayer.prepare();
            mMusicMediaPlayer.start();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * 音乐暂停
     * @param context
     */
    public static void stopSong(Context context){
        if(mMusicMediaPlayer != null){
            mMusicMediaPlayer.stop();
        }
    }

    /**
     * 播放不同的音效
     * @param context
     * @param index
     */
    public static void playSound(Context context, int index){
        //1.获取AssetManager对象
        AssetManager assetManager = context.getAssets();

        if(mSoundMediaPlayer[index] == null){
            mSoundMediaPlayer[index] = new MediaPlayer();

            try{
                //2.获得AssetFileDescriptor对象
                AssetFileDescriptor fileDescriptor = assetManager.
                        openFd(SOUND_NAMES[index]);
                //3.加载音乐文件
                mSoundMediaPlayer[index].setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),fileDescriptor.getLength());
                mSoundMediaPlayer[index].prepare();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        mSoundMediaPlayer[index].start();
    }

    public static void setCompletionListener(MediaPlayer.OnCompletionListener listener){
        if(mMusicMediaPlayer == null){
            mMusicMediaPlayer = new MediaPlayer();
        }

        mMusicMediaPlayer.setOnCompletionListener(listener);
    }

}
