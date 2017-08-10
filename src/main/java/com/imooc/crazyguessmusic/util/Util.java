package com.imooc.crazyguessmusic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.imooc.crazyguessmusic.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.imooc.crazyguessmusic.data.Const;
import com.imooc.crazyguessmusic.model.IAlterDialogButtonListener;
import com.imooc.crazyguessmusic.myview.MyGirdView;

/**
 * Created by xhx12366 on 2017-07-28.
 */

public class Util {
    private static AlertDialog alertDialog;

    public static View getView(Context context,int layoutId){
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layoutId,null);;
        return view;
    }

    /**
     * 生成一个随机汉字
     * @return
     */
    private static char getRandomChar(){
        String str = "";
        int hightPos;
        int lowPos;

        Random random = new Random();

        //汉字区从16区开始，高位字节范围：176~247（ 0xB0 - 0xF7） 差值：72
        hightPos = (176 + Math.abs(random.nextInt(39)));
        //低位字节范围：161~254（0xA1 - 0xFE） 差值：94    因此占用码位：72 * 94 = 6768
        lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try {
            str = new String(b,"GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return str.charAt(0);
    }

    /**
     * 生成所有待选文字
     * @return
     */
    public static String[] generateWord(String songName){
        String[] words = new String[MyGirdView.COUNTS_WORDS];

        //存入歌名
        for(int i = 0; i < songName.length() ;i++){
            words[i] = songName.toCharArray()[i] + "";
        }
        //存入随机汉字
        for(int i = songName.length(); i < MyGirdView.COUNTS_WORDS; i++){
            words[i] = getRandomChar() + "";
        }

        //将所有元素中随机读取一个元素与第一个元素进行交换
        //然后在第二个之后随机选择一个元素与第二个元素进行交换，直到最后一个元素
        Random random = new Random();
        for(int i = 0; i < words.length; i++){
            int index = random.nextInt(words.length - i);

            String temp = words[index];
            words[index] = words[words.length - i - 1];
            words[words.length - i - 1] = temp;
        }

        return words;
    }

    /**
     * 跳转页面
     * @param context
     * @param desti
     */
    public static void startActivity(Context context, Class desti){
        Intent intent = new Intent(context, desti);
        context.startActivity(intent);

        ((Activity)context).finish();
    }

    /**
     * 显示自定义对话框
     * @param context
     * @param message
     * @param listener
     */
    public static void showDialog(final Context context, String message,
                                  final IAlterDialogButtonListener listener){
        View dialogView = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialogView = View.inflate(context, R.layout.dialog_view, null);

        ImageButton btnCancel = (ImageButton) dialogView.findViewById(R.id.btn_cancel);
        ImageButton btnOkView = (ImageButton) dialogView.findViewById(R.id.btn_ok);
        TextView textView = (TextView) dialogView.findViewById(R.id.text_message);

        textView.setText(message);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }

                //取消的音效
                MyPlayer.playSound(context,MyPlayer.INDEX_SOUND_CANCEL);
            }
        });

        btnOkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(alertDialog != null){
                    alertDialog.dismiss();
                }

                if(listener != null){
                    listener.onClick();
                }

                //确定的音效
                MyPlayer.playSound(context,MyPlayer.INDEX_SOUND_ENTER);
            }
        });

        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 关卡，金币数据存储
     * @param context
     * @param stageIndex
     * @param coins
     */
    public static void savaData(Context context, int stageIndex, int coins){
        FileOutputStream fis = null;

        try {
            fis = context.openFileOutput(Const.FILE_NAME_SAVA_DATA,Context.MODE_PRIVATE);

            DataOutputStream dos = new DataOutputStream(fis);

            dos.writeInt(stageIndex);
            dos.writeInt(coins);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 关卡，金币数据读取
     * @param context
     * @return
     */
    public static int[] loadData(Context context){
        FileInputStream fis = null;
        int[] datas = {-1,Const.TOTAL_COINS};

        try {
            fis = context.openFileInput(Const.FILE_NAME_SAVA_DATA);

            DataInputStream dis = new DataInputStream(fis);

            datas[Const.INDEX_LOAD_DATA_STAGE] = dis.readInt();
            datas[Const.INDEX_LOAD_DATA_COINS] = dis.readInt();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return datas;
    }
}
