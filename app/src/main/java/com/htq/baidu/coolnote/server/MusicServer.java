package com.htq.baidu.coolnote.server;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.htq.baidu.coolnote.R;

import java.util.Random;


/**
 * Created by Dell on 2017/4/13.
 */
public class MusicServer extends Service {

    private MediaPlayer mediaPlayer;


    int position = new Random().nextInt(2);

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (mediaPlayer == null) {

            // R.raw.mmp是资源文件，MP3格式的
            playNext();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                    playNext();

                }

            });
        }
    }

    private void playNext() {

        if (position == 0) {
            mediaPlayer = MediaPlayer.create(MusicServer.this, R.raw.a);
            position = 1;

        } else {
            mediaPlayer = MediaPlayer.create(MusicServer.this, R.raw.b);
            position = 0;

        }
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}