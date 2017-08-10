package com.imooc.crazyguessmusic.model;

/**
 * Created by xhx12366 on 2017-07-29.
 */

public class Song {
    private String mSongName;//歌曲名称
    private String mSongFileName;//歌曲的文件名
    private int mNameLength;//歌名长度

//    /**
//     * 歌曲名转换为字符数组
//     * 作用：便于将歌曲名拆分为一个一个的字符
//     * @return
//     */
//    public char[] getNameCharacters(){
//        return mSongName.toCharArray();
//    }

    public int getNameLength() {
        return mNameLength;
    }

    public String getSongFileName() {
        return mSongFileName;
    }

    public void setSongFileName(String songFileName) {
        this.mSongFileName = songFileName;
    }

    public String getSongName() {
        return mSongName;
    }

    public void setSongName(String songName) {
        this.mSongName = songName;

        this.mNameLength = songName.length();
    }
}
