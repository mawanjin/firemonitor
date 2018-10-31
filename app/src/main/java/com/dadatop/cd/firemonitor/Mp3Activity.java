package com.dadatop.cd.firemonitor;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

public class Mp3Activity extends Activity{
    private static MediaPlayer mediaPlayer=null;

    private String mp3 = "1.mp3";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp3);
        String mp3 = getIntent().getStringExtra("mp3");
        play();

    }

    private void play() {
        if(mediaPlayer==null){
            //创建播放实例
            if(mp3.contains("1.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.one);
            }else if(mp3.contains("2.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.two);
            }else if(mp3.contains("3.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.three);
            }else if(mp3.contains("4.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.four);
            }else if(mp3.contains("5.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.five);
            }else if(mp3.contains("6.mp3")){
                mediaPlayer=MediaPlayer.create(this, R.raw.six);
            }
        }

        mediaPlayer.setLooping(false);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }
}
